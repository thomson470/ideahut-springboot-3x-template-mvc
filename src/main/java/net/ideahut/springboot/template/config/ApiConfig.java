package net.ideahut.springboot.template.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiInternalValidator;
import net.ideahut.springboot.api.WebMvcApiValidator;
import net.ideahut.springboot.api.WebMvcApiValidatorImpl;
import net.ideahut.springboot.api.processor.AgentAuthApiProcessor;
import net.ideahut.springboot.api.processor.AgentHeaderApiProcessor;
import net.ideahut.springboot.api.processor.AgentJwtApiProcessor;
import net.ideahut.springboot.api.processor.StandardAuthApiProcessor;
import net.ideahut.springboot.api.processor.StandardHeaderApiProcessor;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.AppConstants;

@Configuration
class ApiConfig {

	@Bean
	protected WebMvcApiValidator apiValidator(
		ApplicationContext applicationContext,
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.ACCESS) RedisTemplate<String, byte[]> redisTemplate,
		ApiInternalValidator internalValidator
		
	) {
		return new WebMvcApiValidatorImpl()
		.setApiName("APP00")
		.setApplicationContext(applicationContext)
		.setApiSourceCode(null)
		.setDataMapper(dataMapper)
		.setEntityClass(new WebMvcApiValidatorImpl.EntityClass()
			.setAccess(net.ideahut.springboot.template.entity.api.ApiAccess.class)
			.setConfig(net.ideahut.springboot.template.entity.api.ApiAccessConfig.class)
			.setTrxManagerName(null)
		)
		.setEntityTrxManager(entityTrxManager)
		.setExpiry(new WebMvcApiValidatorImpl.Expiry()
			.setConfiguration(300)
			.setAccessObject(300)
			.setAccessNull(120)
		)
		.setPrefix("API-VALIDATOR")
		.setRedisTemplate(redisTemplate)
		.setApiProcessors(Arrays.asList(
			StandardAuthApiProcessor.class,
			StandardHeaderApiProcessor.class,
			StandardJwtApiProcessor.class,
			AgentAuthApiProcessor.class,
			AgentHeaderApiProcessor.class,
			AgentJwtApiProcessor.class
		))
		.setApiInternalValidator(internalValidator);
	}
	
	@Bean
	protected ApiInternalValidator internalValidator(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.ACCESS) RedisTemplate<String, byte[]> redisTemplate
	) {
		return parameter -> {
			byte[] value = redisTemplate.opsForValue().get("ACCESS-" + parameter.getKey());
			if (value != null) {
				return dataMapper.read(value, ApiAccess.class);
			}
			return null;
		};
	}
	
}
