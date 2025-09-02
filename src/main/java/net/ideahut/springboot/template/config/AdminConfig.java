package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminHandlerImpl;
import net.ideahut.springboot.admin.WebMvcAdminSecurity;
import net.ideahut.springboot.definition.AdminDefinition;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.module.ModuleAdmin;
import net.ideahut.springboot.redis.RedisParam;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.security.LocalMemoryCredential;
import net.ideahut.springboot.security.RedisMemoryCredential;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.WebMvcBasicAuthSecurity;
import net.ideahut.springboot.security.WebMvcSecurity;
import net.ideahut.springboot.serializer.BinarySerializer;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;

/*
 * Konfigurasi Admin
 * Dapat diakses menggunakan browser
 * http://<host>:<port>/_/web
 */

@Configuration
class AdminConfig {
	
	/*
	 * ADMIN HANDLER
	 */
	@Bean()
	AdminHandler adminHandler(
		AppProperties appProperties,
		ApplicationContext applicationContext,
		BinarySerializer binarySerializer,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
		RedisTemplate<String, byte[]> redisTemplate,
		RestHandler restHandler
	) {
		AdminDefinition.Handler handler = ObjectHelper.callOrElse(
			appProperties.getAdmin() != null && appProperties.getAdmin().getHandler() != null, 
			() -> appProperties.getAdmin().getHandler(), 
			AdminDefinition.Handler::new
		);
		return new AdminHandlerImpl()
				
		// Memperbaharui data-data di javascript & html resource admin sesuai dengan konfigurasi, seperti: judul, timeout, dll
		.setAfterReload(h -> ModuleAdmin.afterReload(h, applicationContext, ""))
		
		// Path untuk mengakses API Admin
		.setApiPath(handler.getApiPath())
		
		// Serialize & deserialize byte array ke redis
		.setBinarySerializer(binarySerializer)
		
		// Custom cek token ke central, secara default sudah tersedia
		//.setCheckTokenCentral(null)
		
		// Lokasi konfigurasi file untuk fitur admin
		.setConfigurationFile(handler.getConfigurationFile())
		
		// Daftar array yang digunakan di template grid, contoh: DAYS, MONTHS, dll
		.setGridAdditionals(GridSupport.getAdditionals())
		
		// Daftar option select ynag digunakan di template grid, contoh: GENDER, BOOLEAN, dll
		.setGridOptions(GridSupport.getOptions())
		
		// Untuk menerjemahkan judul, deskripsi, dll yang ada di template grid
		.setMessageHandler(null)
		
		// Opsi AdminProperties jika configuration file tidak di-set
		.setProperties(null)
		
		// Untuk menyimpan data-data admin, seperti: template grid, authorization, dll
		.setRedisTemplate(redisTemplate)
		
		// Menggunakan nama bean, jika RequestMappingHandlerMapping di application context lebih dari satu
		//.setRequestMappingHandlerMappingBeanName(null)
		
		// Untuk sinkronisasi ke central
		.setRestHandler(restHandler)
		
		// Mekanisme penyimpanan key di storage (redis / local memory)
		.setStorageKeyParam(handler.getStorageKeyParam())
		
		// Custom sinkronisasi ke central, secara default sudah tersedia 
		//.setSyncToCentral(null)
		
		// Umur cache resource Admin UI
		.setWebCacheMaxAge(handler.getWebCacheMaxAge())
		
		// Flag Admin UI bisa diakses atau tidak
		.setWebEnabled(handler.getWebEnabled())
		
		// Lokasi resource Admin UI
		.setWebLocation(handler.getWebLocation())
		
		// Context Path untuk mengakses Admin UI
		.setWebPath(handler.getWebPath())
		
		// Flag resource chain atau tidak
		.setWebResourceChain(handler.getWebResourceChain());
		
	}
	
	
	/*
	 * ADMIN CREDENTIAL
	 */
	@Bean(name = AppConstants.Bean.Credential.ADMIN)
	SecurityCredential adminCredential(
		AppProperties appProperties,
		BinarySerializer binarySerializer,
		@Qualifier(AppConstants.Bean.Redis.COMMON)
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AdminDefinition.Credential credential = ObjectHelper.callOrElse(
			appProperties.getAdmin() != null && appProperties.getAdmin().getCredential() != null, 
			() -> appProperties.getAdmin().getCredential(), 
			AdminDefinition.Credential::new
		);
		if (Boolean.TRUE.equals(credential.getUseLocalMemory())) {
			
			// Local Memory
			return new LocalMemoryCredential()
					
			// Serialize & deserialize byte array di local memory
			.setBinarySerializer(binarySerializer)
					
			// Cek yang sudah kadaluarsa
			.setCheckInterval(credential.getCheckInterval())
			
			// Lokasi file kredensial
			.setCredentialFile(credential.getCredentialFile())
			
			// Optional, jika tidak didefinisikan di credential file
			.setExpiry(credential.getExpiry())
			
			// Optional, jika tidak didefinisikan di credential file
			.setPasswordType(credential.getPasswordType())
			
			// Optional, jika tidak didefinisikan di credential file
			.setUsers(credential.getUsers());
			
		} else {
			
			// Redis memory
			return new RedisMemoryCredential()
			
			// Serialize & deserialize byte array di redis	
			.setBinarySerializer(binarySerializer)
			
			// Lokasi file kredensial
			.setCredentialFile(credential.getCredentialFile())
			
			// Optional, jika tidak didefinisikan di credential file
			.setExpiry(credential.getExpiry())
			
			// Optional, jika tidak didefinisikan di credential file
			.setPasswordType(credential.getPasswordType())
			
			// RedisTemplate dan definisi penyimpanan key-nya
			.setRedisParam(
				new RedisParam<String, byte[]>(credential.getStorageKeyParam())
				.setRedisTemplate(redisTemplate)
			)
			
			// Optional, jika tidak didefinisikan di credential file
			.setUsers(credential.getUsers());
		}
	}
	
	
	/*
	 * ADMIN SECURITY
	 */
	@Bean(name = AppConstants.Bean.Security.ADMIN)
	WebMvcSecurity adminSecurity(
		AppProperties appProperties,
		DataMapper dataMapper,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Credential.ADMIN)
		SecurityCredential credential
	) {
		AdminDefinition.Security security = ObjectHelper.callOrElse(
			appProperties.getAdmin() != null && appProperties.getAdmin().getSecurity() != null, 
			() -> appProperties.getAdmin().getSecurity(), 
			AdminDefinition.Security::new
		);
		if (Boolean.TRUE.equals(security.getUseBasicAuth())) {
			
			// Bsic Auth
			return new WebMvcBasicAuthSecurity()
					
			// Credential
			.setCredential(credential)
			
			// Realm, ditampilkan di-popup browser
			.setRealm(security.getRealm());
			
		} else {
			
			// Berdasarkan AdminHandler
			return new WebMvcAdminSecurity()
					
			// Admin handler		
			.setAdminHandler(adminHandler)
			
			// Credential
			.setCredential(credential)
			
			// DataMapper
			.setDataMapper(dataMapper)
			
			// Pengecekan host yang diperoleh pada saat login
			.setEnableRemoteHost(security.getEnableRemoteHost())
			
			// Pengecekan User-Agent yang diperoleh pada saat login
			.setEnableUserAgent(security.getEnableUserAgent())
			
			// Http header untuk menyimpan token, default: 'Authorization'
			.setHeaderKey(security.getHeaderKey());
		}
	}
	
}
