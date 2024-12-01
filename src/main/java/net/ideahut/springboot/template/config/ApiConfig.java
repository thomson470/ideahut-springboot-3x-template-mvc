package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiAccessInternalService;
import net.ideahut.springboot.api.ApiConfigHelper;
import net.ideahut.springboot.api.ApiConsumerService;
import net.ideahut.springboot.api.ApiConsumerServiceImpl;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
import net.ideahut.springboot.api.ApiTokenService;
import net.ideahut.springboot.api.ApiTokenServiceImpl;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.api.WebMvcApiServiceImpl;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.rest.RestHandler;
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
		.setDataMapper(dataMapper)
		.setEnableConsumer(enable.getConsumer())
		.setEnableCrud(enable.getCrud())
		.setEnableSync(enable.getSync())
		//.setEntityClass(null)
		.setEntityTrxManager(entityTrxManager)
		//.setNullExpiry(null)
		.setRedisPrefix("API-HANDLER")
		//.setRedisAppIdEnabled(true)
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
		RestHandler restHandler,
		ApiTokenService apiTokenService,
		ApiAccessInternalService apiAccessInternalService
	) {
		return new WebMvcApiServiceImpl()
		.setApiAccessInternalService(apiAccessInternalService)
		//.setApiAccessRemoteService(null)
		.setApiHandler(apiHandler)
		.setApiName(appProperties.getApi().getName())
		.setApiProcessors(ApiConfigHelper.getAllDefaultProcessors())
		//.setApiSourceService(null)
		.setApiTokenService(apiTokenService)
		.setDataMapper(dataMapper)
		.setDefaultDigest("SHA-256")
		//.setHeader(null)
		.setRedisExpiry(appProperties.getApi().getRedisExpiry())
		.setRedisPrefix("API-SERVICE")
		.setRedisTemplate(redisTemplate)
		.setRestHandler(restHandler);
	}
	
	@Bean
	ApiTokenService apiTokenService(
		AppProperties appProperties,
		DataMapper dataMapper
	) {
		AppProperties.Api api = appProperties.getApi();
		return new ApiTokenServiceImpl()
		.setConsumer(api.getConsumer())
		.setDataMapper(dataMapper)
		.setJwtProcessor(api.getJwtProcessor())
		.setSignatureTimeSpan(api.getSignatureTimeSpan());
	}
	
	@Bean
	ApiAccessInternalService apiAccessInternalService() {
		return parameter -> null;
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

