package net.ideahut.springboot.template.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiHeader;
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
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.TimeValue;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.util.TimeUtil;
import net.ideahut.springboot.util.WebMvcUtil;

@Service
class AccessServiceImpl implements AccessService, BeanConfigure<AccessService> {
	
	//private static final Long TIME_SPAN = 120_000L; // 2 menit ke bawah dan ke atas
	private static final Long API_ACCESS_EXPIRY = 86_400_000L; // 1 hari
	private static final List<String> JWT_PROCESSORS = Arrays.asList(
		StandardJwtApiProcessor.API_TYPE,
		AgentJwtApiProcessor.API_TYPE,
		HostJwtApiProcessor.API_TYPE,
		AgentHostJwtApiProcessor.API_TYPE
	);
	
	private static final String USERID = "1234567890";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String API_ROLE = "PUBLIC";
	
	private static final String REDIS_PREFIX = "ACCESS-";
	
	private WebMvcApiService apiService;
	
	private final DataMapper dataMapper;
	private final RedisTemplate<String, byte[]> redisTemplate;
	
	@Autowired
	AccessServiceImpl(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate
	) {
		this.dataMapper = dataMapper;
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public Callable<AccessService> onConfigureBean(ApplicationContext applicationContext) {
		apiService = applicationContext.getBean(WebMvcApiService.class);
		AccessServiceImpl self = this;
		return new Callable<AccessService>() {
			@Override
			public AccessService call() throws Exception {
				return self;
			}
		};
	}

	@Override
	public boolean isBeanConfigured() {
		return true;
	}

	@Override
	public ApiAuth login(
		HttpServletRequest httpRequest, 
		String username, 
		String password
	) throws Exception {
		String apiType = WebMvcUtil.getHeader(httpRequest, apiService.getApiHeader().getTypeHeader(), StandardJwtApiProcessor.API_TYPE);
		boolean isJwt = JWT_PROCESSORS.contains(apiType);
		Assert.isTrue(isJwt, "Currenty only support JWT Processor");
		Assert.isTrue(USERNAME.equals(username) && PASSWORD.equals(password), "Invalid user");
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		ApiAccess apiAccess = new ApiAccess()
		.setValidUntil(TimeValue.of(TimeUnit.MILLISECONDS, TimeUtil.currentEpochMillis() + API_ACCESS_EXPIRY))
		.setApiUser(new ApiUser()
			.setId(USERID)
			.setUsername(USERNAME)
		)
		.setServiceRole(apiService.getApiName(), API_ROLE);
		
		ApiParameter apiParameter = new ApiParameter()
		.setApiType(apiType)
		.setApiName(apiService.getApiName())
		.setApiRequest(apiRequest);
		
		ApiProcessor apiProcessor = apiService.getApiProcessor(apiType);
		ApiSource apiSource = new ApiSource();
		ApiAuth apiAuth = apiProcessor.createApiAuth(apiSource, apiParameter, apiAccess);
		byte[] values = dataMapper.writeAsBytes(apiAccess, DataMapper.JSON);
		ValueOperations<String, byte[]> valops = redisTemplate.opsForValue();
		valops.set(REDIS_PREFIX + apiAuth.getApiKey(), values, 3600, TimeUnit.SECONDS);
		return apiAuth;
	}

	@Override
	public ApiAccess logout() {
		ApiAccess apiAccess = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
		if (apiAccess != null) {
			redisTemplate.delete(REDIS_PREFIX + apiAccess.getApiKey());
			apiService.removeApiAccess(apiAccess.getApiKey());
		}
		return apiAccess;
	}

	@Override
	public ApiAccess info(ApiParameter apiParameter) {
		String apiKey = apiParameter != null ? apiParameter.getApiKey() : null;
		if (apiKey == null) {
			ApiRequest apiRequest = apiService.getApiRequest(WebMvcUtil.getRequest(), false);
			apiKey = apiService.getApiKey(apiRequest);
		}
		ValueOperations<String, byte[]> valops = redisTemplate.opsForValue();
		byte[] values = valops.get(REDIS_PREFIX + apiKey);
		return values != null ? dataMapper.read(values, ApiAccess.class) : null;
	}
	
	@Override
	public String token(HttpServletRequest httpRequest) {
		ApiHeader apiHeader = apiService.getApiHeader();
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, false);
		String from = apiRequest.getHeader(apiHeader.getFromHeader());
		Assert.hasLength(from, "Header " + apiHeader.getFromHeader() + " is required");
		ApiSource apiSource = apiService.getApiSource(from);
		Assert.notNull(apiSource, "ApiSource is not found");
		return apiService.getApiTokenService().createConsumerToken(apiHeader, apiSource, apiRequest);
	}

}
