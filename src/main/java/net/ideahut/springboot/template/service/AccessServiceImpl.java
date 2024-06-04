package net.ideahut.springboot.template.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiConfiguration;
import net.ideahut.springboot.api.ApiParameter;
import net.ideahut.springboot.api.ApiProcessor;
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.ApiUser;
import net.ideahut.springboot.api.WebMvcApiValidator;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.SessionCallable;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.entity.app.User;
import net.ideahut.springboot.util.TimeUtil;

@Service
public class AccessServiceImpl implements AccessService {
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private EntityTrxManager entityTrxManager;
	@Autowired
	private WebMvcApiValidator apiValidator;
	@Autowired
	@Qualifier(AppConstants.Bean.Redis.ACCESS)
	private RedisTemplate<String, byte[]> redisTemplate;

	@Override
	public ApiAuth login(
		HttpServletRequest httpRequest, 
		String username, 
		String password
	) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		User user = trxManagerInfo.transaction(new SessionCallable<User>() {
			@Override
			public User call(Session session) throws Exception {
				User user = session.createQuery("from User where username=?1", User.class)
				.setParameter(1, username)
				.uniqueResult();
				trxManagerInfo.loadLazy(username, User.class);
				return user;
			}
		});
		Assert.notNull(user, "User is not found");
		Assert.isTrue(AppConstants.Profile.Status.ACTIVE == user.getStatus(), "User is not active");
		Assert.isTrue("pass123".equals(password), "Invalid password");
	    
		ApiAccess access = new ApiAccess()
		.setExpiration(TimeUtil.currentEpochMillis() + 3_600_000)
		.setRole("ADMIN")
		.setService("paygw", "PUBLIC")
		.setService(apiValidator.getApiName(), "ADMIN")
		.setSource(apiValidator.getApiName())
		.setUser(new ApiUser()
			.setId(user.getUserId())
			.setUsername(user.getUsername())
		);
		
		ApiRequest request = apiValidator.getApiRequest(httpRequest, true);
	    ApiParameter parameter = new ApiParameter()
	    .setObject(access)
	    .setRequest(request)
	    .setToken(UUID.randomUUID().toString());
		
	    ApiConfiguration configuration = new ApiConfiguration()
	    .setApiName(apiValidator.getApiName())
	    .setExpiry(3600)
	    .setKeyValue("SIGNATURE_SECRET", "rahasia123");
	    
		ApiProcessor processor = apiValidator.getApiProcessor(StandardJwtApiProcessor.CODE);
		ApiAuth auth = processor.createAuth(configuration, parameter);
		if (auth.getAttributes() != null) {
			access.setAttributeMap(auth.getAttributes()); 
		}
		byte[] value = dataMapper.writeAsBytes(access, DataMapper.JSON);
		redisTemplate.opsForValue().set("ACCESS-" + auth.getKey(), value, 3600, TimeUnit.SECONDS);
		return auth;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ApiAccess getApiAccess(ApiParameter apiParameter) {
		// TODO Auto-generated method stub
		return null;
	}

}
