package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiAccessInternalService;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
import net.ideahut.springboot.api.ApiTokenService;
import net.ideahut.springboot.api.ApiTokenServiceImpl;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.api.WebMvcApiServiceImpl;
import net.ideahut.springboot.definition.ApiDefinition;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.module.ModuleApi;
import net.ideahut.springboot.redis.RedisParam;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.serializer.BinarySerializer;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.service.AuthService;

@Configuration
class ApiConfig {
	
	/*
	 * API HANDLER (CRUD & REQUEST)
	 */
	@Bean
	ApiHandler apiHandler(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		BinarySerializer binarySerializer,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON)
		TaskHandler taskHandler
	) {
		ApiDefinition.Handler handler = ObjectHelper.callOrElse(
			appProperties.getApi() != null && appProperties.getApi().getHandler() != null, 
			() -> appProperties.getApi().getHandler(), 
			ApiDefinition.Handler::new
		);
		return new ApiHandlerImpl()
		
		// Serialize & deserialize byte array ke redis
		.setBinarySerializer(binarySerializer)
		
		// Jumlah thread pada saat reload bean (menyiapkan data crud & request mapping), default: 3 thread
		.setConfigureThreads(handler.getConfigureThreads())
		
		// Flag apakah bisa di-consume oleh Api Service / Provider lain
		.setEnableConsumer(handler.getEnableConsumer())
		
		// Flag apakah Api Crud aktif atau tidak
		.setEnableCrud(handler.getEnableCrud())
		
		// Flag apakah data Crud & Request Mapping di database dihapus jika tidak tersedia di aplikasi
		.setEnableSync(handler.getEnableSync())
		
		// Daftar Entity class dan nama trxManager yang terkait dengan ApiHandler
		// default semua class di package net.ideahut.springboot.api.entity
		.setEntityClass(null)
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// RedisTemplate dan definisi penamaan key-nya
		.setRedisParam(
			new RedisParam<String, byte[]>(handler.getRedisParam())
			.setRedisTemplate(redisTemplate)
		)
		
		// Optional, jika bean 'RequestMappingHandlerMapping' lebih dari satu atau namanya berbeda 
		.setRequestMappingHandlerMappingBeanName(null)
		
		// TaskHandler, untuk asinkronus
		.setTaskHandler(taskHandler);
	}
	
	
	/*
	 * API SERVICE
	 */
	@Bean
	WebMvcApiService apiService(
		AppProperties appProperties,
		BinarySerializer binarySerializer,
		@Qualifier(AppConstants.Bean.Redis.ACCESS)
		RedisTemplate<String, byte[]> redisTemplate,
		ApiHandler apiHandler,
		@Qualifier(AppConstants.Bean.Task.COMMON)
		TaskHandler taskHandler,
		RestHandler restHandler,
		ApiTokenService apiTokenService,
		ApiAccessInternalService apiAccessInternalService
	) {
		ApiDefinition.Service service = ObjectHelper.callOrElse(
			appProperties.getApi() != null && appProperties.getApi().getService() != null, 
			() -> appProperties.getApi().getService(), 
			ApiDefinition.Service::new
		);
		return new WebMvcApiServiceImpl()
				
		// Untuk mendapatkan ApiAccess, jika token diterbitkan oleh service ini sendiri
		.setApiAccessInternalService(apiAccessInternalService)
		
		// Untuk mendapatkan ApiAccess ke service yang menerbitkan token
		// Secara default sudah dihandle, fungsi ini diperlukan jika ada custom
		.setApiAccessRemoteService(null)
		
		// ApiHandler
		.setApiHandler(apiHandler)
		
		// ApiName, akan didaftarkan ke database di setiap service yang terhubung
		// Jika tidak diset akan digunakan ID dari application context (property 'spring.application.name')
		.setApiName(service.getApiName())
		
		// Daftar ApiProcessor yang digunakan
		.setApiProcessors(ModuleApi.getDefaultProcessors())
		
		// Untuk custom RestClient, seperti menambahkan sertifikat, timeout, dll.
		// Secara default sudah ada, hanya diperlukan jika custom
		.setApiRestClientCreator(null)
		
		// Secara default sudah ada, hanya diperlukan untuk mendapatkan ApiSource custom
		// misalnya daftar ApiSource tersimpan di file
		.setApiSourceService(null)
		
		// Service untuk meng-handle token
		.setApiTokenService(apiTokenService)
		
		// Serialize & deserialize byte array ke redis
		.setBinarySerializer(binarySerializer)
		
		// Custom Header name
		.setHeaderName(null)
		
		// Custom redis expiry, untuk data access & consumer baik yang null maupun tidak
		.setRedisExpiry(service.getRedisExpiry())
		
		// RedisTemplate dan definisi penamaan key-nya
		.setRedisParam(
			new RedisParam<String, byte[]>(service.getRedisParam())
			.setRedisTemplate(redisTemplate)
		)
		
		// RestHandler
		.setRestHandler(restHandler);
	}
	
	
	/*
	 * API TOKEN SERVICE
	 */
	@Bean
	ApiTokenService apiTokenService(
		AppProperties appProperties,
		DataMapper dataMapper,
		SysParamHandler sysParamHandler
	) {
		ApiDefinition.Token token = ObjectHelper.callOrElse(
			appProperties.getApi() != null && appProperties.getApi().getToken() != null, 
			() -> appProperties.getApi().getToken(), 
			ApiDefinition.Token::new
		);
		return new ApiTokenServiceImpl()
		
		// Secara default sudah ada, diperlukan untuk mendapatkan ApiToken custom		
		.setApiTokenRetriever(null)
		
		// Default Consumer (komunikasi antar service) secret, digest , & expiry, jika tidak diset di database
		.setConsumerJwtParam(token.getConsumerJwtParam())
		
		// index redisTemplate untuk meyimpan consumer token jika menggunakan MultipleRedisTemplate, default index 1
		.setConsumerTokenStorageIndex(token.getConsumerTokenStorageIndex())
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Default JwtProcessor secret, digest , & expiry, jika tidak diset di database
		.setProcessorJwtParam(token.getProcessorJwtParam())
		
		// Batas atas & bawah timestamp signature yang dikirim
		// Contoh: jika diset 1 menit, berarti signature yang dikirim valid jika timestamp client dalam range -+ 1 menit
		.setSignatureTimeSpan(token.getSignatureTimeSpan())
		
		// Untuk menyimpan ApiToken di SysParam, sysCode = API_TOKEN, paramCode = [ApiName]
		.setSysParamHandler(sysParamHandler)
		
		// Pakai ApiToken di dalam ApiProcessor untuk request ke ApiSource lain, 
		// jika false atau ApiToken tidak ada, maka akan digunakan Signature (berdasarkan secret dan digest) 
		.setUseApiTokenInProcessor(token.getUseApiTokenInProcessor());
	}
	
	
	/*
	 * API ACCESS INTERNAL SERVICE
	 */
	@Bean
	ApiAccessInternalService apiAccessInternalService(
		AuthService authService
	) {
		return authService::getApiAccessForInternal;
	}
	
}

