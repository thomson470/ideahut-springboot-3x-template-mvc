package net.ideahut.springboot.template.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiHeaderValue;
import net.ideahut.springboot.api.ApiParameter;
import net.ideahut.springboot.api.ApiProcessor;
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.ApiSource;
import net.ideahut.springboot.api.ApiUser;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.api.processor.AgentHostJwtApiProcessor;
import net.ideahut.springboot.api.processor.AgentJwtApiProcessor;
import net.ideahut.springboot.api.processor.HostJwtApiProcessor;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.helper.ErrorHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.TimeHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Message;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.object.TimeValue;
import net.ideahut.springboot.redis.RedisHelper;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.object.UserData;

@Service
class AuthServiceImpl implements AuthService, BeanConfigure<AuthService> {
	
	private static final String REDIS_PREFIX = "AUTH-";
	private static final TimeValue REDIS_EXPIRY = TimeValue.of(TimeUnit.SECONDS, 3600L); // 1 jam
	private static final TimeValue API_ACCESS_EXPIRY = TimeValue.of(TimeUnit.HOURS, 24L); // 1 hari
	private static final List<String> JWT_PROCESSORS = Arrays.asList(
		StandardJwtApiProcessor.API_TYPE,
		AgentJwtApiProcessor.API_TYPE,
		HostJwtApiProcessor.API_TYPE,
		AgentHostJwtApiProcessor.API_TYPE
	);
	private static final String API_ROLE = "USER-MVC";
	private static final Map<String, UserData> users;
	static {
		Map<String, UserData> usersEi = new HashMap<>();
		usersEi.put("admin", new UserData().setRoleCode("APP-ADMIN").setPassword("admin123").setUserId("1").setUsername("admin"));
		usersEi.put("user", new UserData().setRoleCode("APP-USER").setPassword("user123").setUserId("1").setUsername("user"));
		users = usersEi;
	}
	
	private final DataMapper dataMapper;
	private final RedisTemplate<String, byte[]> redisTemplate;
	
	private WebMvcApiService apiService;
	private boolean configured = false;
	
	@Autowired
	AuthServiceImpl(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate
	) {
		this.dataMapper = dataMapper;
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public Callable<AuthService> onConfigureBean(
		ApplicationContext applicationContext
	) {
		AuthServiceImpl self = this;
		return new Callable<AuthService>() {
			@Override
			public AuthService call() throws Exception {
				// ApiService diambil di sini agar tidak terjadi cycle dependency
				self.apiService = applicationContext.getBean(WebMvcApiService.class);
				configured = true;
				return self;
			}
		};
	}

	@Override
	public boolean isBeanConfigured() {
		return configured;
	}

	@Override
	public ApiAuth login(
		ApiRequest apiRequest, 
		String username, 
		String password
	) throws Exception {
		UserData user = users.get(username);
		Assert.notNull(user, "User not found");
		Assert.isTrue(user.getPassword().equals(password), "Invalid password");
		
		String apiType = apiRequest.getHeader(apiService.getApiHeaderName().getType(), StandardJwtApiProcessor.API_TYPE);
		ApiProcessor apiProcessor = apiService.getApiProcessor(apiType);
		Assert.notNull(apiProcessor, "ApiProcessor not found");
		boolean isJwtCheck = apiRequest.getHeader(boolean.class, "Jwt-Check", false);
		boolean isJwtType = JWT_PROCESSORS.contains(apiType);
		
		Long createdOn = TimeHelper.currentEpochMillis();
		ApiAccess apiAccess = new ApiAccess()
		.setCreatedOn(createdOn)
		.setExpiredOn(createdOn + API_ACCESS_EXPIRY.toMillis())
		.setApiUser(new ApiUser()
			.setId(user.getUserId())
			.setUsername(user.getUsername())
			.setAttribute(ApiUser.Attribute.ROLE, user.getRoleCode())
		)
		// set appid
		.setAttribute(ApiAccess.Attribute.APP_ID, apiService.getApiName());
		if (isJwtType && isJwtCheck) {
			apiAccess.setAttribute("check", "true");
		}
		
		ApiParameter apiParameter = new ApiParameter()
		.setApiType(apiType)
		.setApiName(apiService.getApiName())
		.setApiRequest(apiRequest);
		
		ApiAuth apiAuth = apiProcessor.createApiAuth(apiParameter, apiAccess);
		byte[] bytes = dataMapper.writeAsBytes(apiAccess, DataMapper.JSON);
		RedisHelper.setValue(
			redisTemplate,
			REDIS_PREFIX, 
			apiAuth.getApiKey(), 
			bytes, 
			REDIS_EXPIRY
		);
		return apiAuth;
	}

	@Override
	public ApiAccess logout(
		ApiRequest apiRequest
	) {
		ApiAccess apiAccess = apiService.getApiAccess(apiRequest);
		if (apiAccess != null) {
			Assert.isTrue(isInternalApiAccess(apiAccess), "Invalid ApiPublisher");
			redisTemplate.delete(REDIS_PREFIX + apiAccess.getApiKey());
			apiService.removeApiAccess(null, apiAccess.getApiKey());
		}
		return apiAccess;
	}

	@Override
	public ApiAccess info(
		ApiRequest apiRequest
	) {
		ApiParameter apiParameter = apiService.getApiParameter(apiRequest);
		String apiKey = apiParameter != null ? apiParameter.getApiKey() : null;
		if (apiKey != null) {
			byte[] bytes = RedisHelper.getValue(redisTemplate, REDIS_PREFIX, apiKey);
			if (bytes != null) {
				ApiAccess apiAccess = dataMapper.read(bytes, ApiAccess.class);
				Assert.isTrue(isInternalApiAccess(apiAccess), "Invalid ApiPublisher");
				return apiAccess;
			}
		}
		return null;
	}

	/*
	 * ApiAccess untuk request dari ApiProvider lain
	 */
	@Override
	public ApiAccess getApiAccessForExternal(
		ApiRequest apiRequest
	) {
		ApiParameter apiParameter = apiService.getApiParameter(apiRequest);
		String apiKey = ObjectHelper.useOrDefault(apiParameter.getApiKey(), "");
		Assert.hasLength(apiKey, "ApiKey required");
		ApiHeaderValue apiHeaderValue = apiRequest.getApiHeaderValue();
		String from = ObjectHelper.useOrDefault(apiHeaderValue.getFrom(), "");
		Assert.hasLength(from, "Header '" + apiHeaderValue.getApiHeaderName().getFrom() + "' required");
		ApiSource apiSource = apiService.getApiSource(from);
		Assert.notNull(apiSource, "ApiSource not found");
		Message message = apiService.getApiTokenService().validateSignature(apiSource, apiHeaderValue);
		ErrorHelper.throwIf(message != null, () -> ResultRuntimeException.of(Result.error(message)));
		byte[] bytes = RedisHelper.getValue(redisTemplate, REDIS_PREFIX, apiKey);
		if (bytes == null) {
			return null;
		}
		ApiAccess apiAccess = dataMapper.read(bytes, ApiAccess.class);
		return apiAccess.setApiRole(API_ROLE);
	}

	/*
	 * ApiAccess untuk request dari internal service
	 */
	@Override
	public ApiAccess getApiAccessForInternal(ApiParameter apiParameter) {
		String apiKey = apiParameter != null && apiParameter.getApiKey() != null ? apiParameter.getApiKey() : "";
		Assert.hasLength(apiKey, "ApiKey required");
		byte[] bytes = RedisHelper.getValue(redisTemplate, REDIS_PREFIX, apiKey);
		if (bytes == null) {
			return null;
		}
		ApiAccess apiAccess = dataMapper.read(bytes, ApiAccess.class);
		return apiAccess.setApiRole(API_ROLE);
	}
	
	@Override
	public String createConsumerToken(ApiRequest apiRequest) {
		ApiHeaderValue apiHeaderValue = apiRequest.getApiHeaderValue();
		String from = ObjectHelper.useOrDefault(apiHeaderValue.getFrom(), "");
		Assert.hasLength(from, "Header '" + apiHeaderValue.getApiHeaderName().getFrom() + "' required");
		ApiSource apiSource = apiService.getApiSource(from);
		Assert.notNull(apiSource, "ApiSource not found");
		return apiService.getApiTokenService().createConsumerApiToken(apiSource, apiRequest);
	}
	
	private boolean isInternalApiAccess(ApiAccess apiAccess) {
		if (apiAccess != null) {
			return apiService.getApiName().equals(apiAccess.getAttribute(ApiAccess.Attribute.APP_ID));
		}
		return false;
	}

}
