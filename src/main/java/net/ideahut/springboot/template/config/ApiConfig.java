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
import net.ideahut.springboot.template.service.AuthService;

@Configuration
class ApiConfig {
	
	/*
	 * API HANDLER (CRUD & REQUEST)
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
		
		// Jumlah thread pada saat reload bean (menyiapkan crud & request mapping data), default: 3 thread
		//.setConfigureThreads(null)
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Flag apakah bisa di-consume oleh Api Service / Provider lain
		.setEnableConsumer(enable.getConsumer())
		
		// Flag apakah Api Crud aktif atau tidak
		.setEnableCrud(enable.getCrud())
		
		// Flag apakah data Crud & Request di database dihapus jika tidak tersedia di aplikasi
		.setEnableSync(enable.getSync())
		
		// Daftar Entity class dan nama trxManager yang terkait dengan ApiHandler
		// default semua class di package net.ideahut.springboot.api.entity
		//.setEntityClass(null)
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// Waktu kadaluarsa data null yang disimpan di redis (agar tidak perlu selalu cek ke database)
		//.setNullExpiry(null)
		
		// AppId akan digabungkan dengan prefix untuk key
		//.setRedisAppIdEnabled(null)
		
		// Redis prefix untuk key
		.setRedisPrefix("API-HANDLER")
		
		// Redis Template
		.setRedisTemplate(redisTemplate)
		
		// Menggunakan nama bean, jika RequestMappingHandlerMapping di application context lebih dari satu
		//.setRequestMappingHandlerMappingBeanName(null)
		
		// TaskHandler
		.setTaskHandler(taskHandler);
	}
	
	
	/*
	 * API SERVICE
	 */
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
		
		// Untuk mendapatkan ApiAccess, jika token diterbitkan oleh service ini sendiri
		.setApiAccessInternalService(apiAccessInternalService)
		
		// Untuk mendapatkan ApiAccess ke service yang menerbitkan token
		// Secara default sudah dihandle, fungsi ini diperlukan jika ada custom
		//.setApiAccessRemoteService(null)
		
		// ApiHandler
		.setApiHandler(apiHandler)
		
		// ApiName, akan didaftarkan ke database di setiap service yang terhubung
		// Jika tidak diset akan digunakan ID dari application context atau dari property 'spring.application.name'
		.setApiName(appProperties.getApi().getName())
		
		// Daftar ApiProcessor yang digunakan
		.setApiProcessors(ApiConfigHelper.getAllDefaultProcessors())
		
		// Untuk custom RestClient, seperti menambahkan sertifikat, timeout, dll.
		// Secara default sudah ada, hanya diperlukan jika custom
		//.setApiRestClientCreator(null)
		
		// Secara default sudah ada, hanya diperlukan untuk mendapatkan ApiSource custom
		// misalnya daftar ApiSource tersimpan di file
		//.setApiSourceService(null)
		
		// Service untuk meng-handle token
		.setApiTokenService(apiTokenService)
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Default digest jika tidak didefinisikan di database
		.setDefaultDigest("SHA-256")
		
		// Custom Header name
		//.setHeader(null)
		
		// AppId akan digabungkan dengan prefix untuk key
		//.setRedisAppIdEnabled(null)
		
		// Custom redis expiry, untuk data access & consumer baik yang null maupun tidak
		//.setRedisExpiry(null)
		
		// Redis prefix untuk key
		.setRedisPrefix("API-SERVICE")
		
		// RedisTemplate
		.setRedisTemplate(redisTemplate)
		
		// RestHandler
		.setRestHandler(restHandler);
	}
	
	
	/*
	 * API TOKEN SERVICE
	 */
	@Bean
	ApiTokenService apiTokenService(
		AppProperties appProperties,
		DataMapper dataMapper
	) {
		AppProperties.Api api = appProperties.getApi();
		return new ApiTokenServiceImpl()
				
		// Default consumer (komunikasi antar service) secret, digest , & expiry, jika tidak diset di database
		.setConsumer(api.getConsumer())
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Default JwtProcessor secret, digest , & expiry, jika tidak diset di database
		.setJwtProcessor(api.getJwtProcessor())
		
		// Batas atas & bawah timestamp signature yang dikirim
		// Contoh: jika diset 1 menit, berarti signature yang dikirim valid jika timestamp client dalam range -+ 1 menit
		.setSignatureTimeSpan(api.getSignatureTimeSpan());
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
	
	
	/*
	 * API CONSUMER SERVICE
	 */
	@Bean
	ApiConsumerService apiConsumerService(
		DataMapper dataMapper,
		WebMvcApiService apiService
	) {
		return new ApiConsumerServiceImpl()
				
		// Untuk mendapatkan token ke Api Service lain
		// Secara default sudah dihandle, fungsi ini diperlukan jika ada custom
		// contoh jika request menggunakan SSL certificate, atau custom header
		//.setApiConsumerTokenGetter(null)
		
		// Api Service
		.setApiService(apiService)
		
		// Data Mapper
		.setDataMapper(dataMapper);
	}
	
}

