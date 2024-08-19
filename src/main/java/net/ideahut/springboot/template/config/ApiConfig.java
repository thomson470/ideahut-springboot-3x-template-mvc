package net.ideahut.springboot.template.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiAccessService;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
import net.ideahut.springboot.api.ApiTokenService;
import net.ideahut.springboot.api.ApiTokenServiceImpl;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.api.WebMvcApiServiceImpl;
import net.ideahut.springboot.api.processor.AgentHostJwtApiProcessor;
import net.ideahut.springboot.api.processor.AgentJwtApiProcessor;
import net.ideahut.springboot.api.processor.HostJwtApiProcessor;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.service.AccessService;

@Configuration
class ApiConfig {
	
	/*
	 * API
	 */
	@Bean
	ApiHandler apiHandler(
		AppProperties appProperties,
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
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
		ApiAccessService apiAccessService,
		ApiTokenService apiTokenService
		
	) {
		AppProperties.Api.RedisExpiry redisExpiry = appProperties.getApi().getRedisExpiry();
		return new WebMvcApiServiceImpl()
		.setApiAccessService(apiAccessService)
		.setApiTokenService(apiTokenService)
		.setApiHandler(apiHandler)
		.setApiName(appProperties.getApi().getName())
		.setApiProcessors(Arrays.asList(
			//StandardAuthApiProcessor.class,
			//StandardHeaderApiProcessor.class,
			StandardJwtApiProcessor.class,
			//AgentAuthApiProcessor.class,
			//AgentHeaderApiProcessor.class,
			AgentJwtApiProcessor.class,
			//HostAuthApiProcessor.class,
			//HostHeaderApiProcessor.class,
			HostJwtApiProcessor.class,
			//AgentHostAuthApiProcessor.class,
			//AgentHostHeaderApiProcessor.class,
			AgentHostJwtApiProcessor.class
		))
		.setDataMapper(dataMapper)
		.setDefaultDigest(appProperties.getApi().getDefaultDigest())
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
	ApiAccessService apiAccessService(
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.ACCESS) 
		RedisTemplate<String, byte[]> redisTemplate,
		AccessService accessService
	) {
		return accessService::info;
	}
	
}
