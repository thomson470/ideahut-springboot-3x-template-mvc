package net.ideahut.springboot.template.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiAccessInternalService;
import net.ideahut.springboot.api.ApiConsumerService;
import net.ideahut.springboot.api.ApiConsumerServiceImpl;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
import net.ideahut.springboot.api.ApiTokenService;
import net.ideahut.springboot.api.ApiTokenServiceImpl;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.api.WebMvcApiServiceImpl;
import net.ideahut.springboot.api.processor.AgentAuthApiProcessor;
import net.ideahut.springboot.api.processor.AgentHeaderApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostAuthApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostHeaderApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostJwtApiProcessor;
import net.ideahut.springboot.api.processor.AgentJwtApiProcessor;
import net.ideahut.springboot.api.processor.HostAuthApiProcessor;
import net.ideahut.springboot.api.processor.HostHeaderApiProcessor;
import net.ideahut.springboot.api.processor.HostJwtApiProcessor;
import net.ideahut.springboot.api.processor.StandardAuthApiProcessor;
import net.ideahut.springboot.api.processor.StandardHeaderApiProcessor;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

@Configuration
class ApiConfig {
	
	/*
	 * HANDLER (CRUD & REQUEST)
	 */
	@Bean
	ApiHandler apiHandler(
		AppProperties appProperties,
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON)
		TaskHandler taskHandler
		
	) {
		AppProperties.Api.Enable enable = appProperties.getApi().getEnable();
		return new ApiHandlerImpl()
		.setEnableSync(enable.getSync())
		.setEnableCrud(enable.getCrud())
		.setEnableConsumer(enable.getConsumer())
		.setDataMapper(dataMapper)
		.setEntityTrxManager(entityTrxManager)
		.setRedisPrefix("API-HANDLER")
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(taskHandler);
	}
	
	@Bean
	WebMvcApiService apiService(
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate,
		ApiHandler apiHandler,
		@Qualifier(AppConstants.Bean.Task.COMMON)
		TaskHandler taskHandler,
		ApiTokenService apiTokenService,
		ApiAccessInternalService apiAccessInternalService
		
	) {
		AppProperties.Api.RedisExpiry redisExpiry = appProperties.getApi().getRedisExpiry();
		return new WebMvcApiServiceImpl()
		.setApiAccessInternalService(apiAccessInternalService)
		//.setApiAccessRemoteService(null)
		.setApiHandler(apiHandler)
		//.setApiName(null)
		.setApiProcessors(Arrays.asList(
			StandardAuthApiProcessor.class,
			StandardHeaderApiProcessor.class,
			StandardJwtApiProcessor.class,
			AgentAuthApiProcessor.class,
			AgentHeaderApiProcessor.class,
			AgentJwtApiProcessor.class,
			HostAuthApiProcessor.class,
			HostHeaderApiProcessor.class,
			HostJwtApiProcessor.class,
			AgentHostAuthApiProcessor.class,
			AgentHostHeaderApiProcessor.class,
			AgentHostJwtApiProcessor.class
		))
		//.setApiSourceService(null)
		.setApiTokenService(apiTokenService)
		.setDataMapper(dataMapper)
		.setDefaultDigest("SHA-256")
		//.setHeader(null)
		.setRedisExpiry(new WebMvcApiServiceImpl.RedisExpiry()
			.setAccessItem(redisExpiry.getAccessItem())
			.setAccessNull(redisExpiry.getAccessNull())
			.setConsumerItem(redisExpiry.getConsumerItem())
			.setConsumerNull(redisExpiry.getConsumerItem())
		)
		.setRedisPrefix("API-SERVICE")
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(taskHandler);
	}
	
	@Bean
	ApiTokenService apiTokenService(
		AppProperties appProperties,
		DataMapper dataMapper
	) {
		AppProperties.Api.Consumer consumer = appProperties.getApi().getConsumer();
		AppProperties.Api.JwtProcessor jwtProcessor = appProperties.getApi().getJwtProcessor();
		return new ApiTokenServiceImpl()
		.setConsumer(new ApiTokenServiceImpl.Consumer()
			.setDigest(consumer.getDigest())
			.setExpiry(consumer.getExpiry())
			.setSecret(consumer.getSecret())
		)
		.setDataMapper(dataMapper)
		.setJwtProcessor(new ApiTokenServiceImpl.JwtProcessor()
			.setDigest(jwtProcessor.getDigest())
			.setExpiry(jwtProcessor.getExpiry())
			.setSecret(jwtProcessor.getSecret())
		)
		.setSignatureTimeSpan(appProperties.getApi().getSignatureTimeSpan());
	}
	
	@Bean
	ApiAccessInternalService apiAccessInternalService(
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.ACCESS) RedisTemplate<String, byte[]> redisTemplate
	) {
		return parameter -> {
			return null;
		};
	}
	
	@Bean
	ApiConsumerService apiConsumerService(
		DataMapper dataMapper,
		WebMvcApiService apiService
	) {
		return new ApiConsumerServiceImpl()
		.setApiService(apiService)
		.setDataMapper(dataMapper);
	}
	
}

