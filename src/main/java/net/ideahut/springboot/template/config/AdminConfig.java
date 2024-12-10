package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.admin.AdminConfigHelper;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminHandlerImpl;
import net.ideahut.springboot.admin.WebMvcAdminSecurity;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.security.RedisMemoryCredential;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.WebMvcBasicAuthSecurity;
import net.ideahut.springboot.security.WebMvcSecurity;
import net.ideahut.springboot.template.AppConstants;
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
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
		RedisTemplate<String, byte[]> redisTemplate,
		RestHandler restHandler
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new AdminHandlerImpl()
				
		// Memperbaharui data-data di javascript & html resource admin sesuai dengan konfigurasi, seperti: judul, timeout, dll
		.setAfterReload(handler -> AdminConfigHelper.afterReloadAdminResource(handler, applicationContext, ""))
		
		// Path untuk mengakses API Admin
		.setApiPath(admin.getApiPath())
		
		// Custom cek token ke central, secara default sudah tersedia
		//.setCheckTokenCentral(null)
		
		// Lokasi konfigurasi file untuk fitur admin
		.setConfigurationFile(admin.getConfigurationFile())
		
		.setDataMapper(dataMapper)
		
		// Daftar array yang digunakan di template grid, contoh: DAYS, MONTHS, dll
		.setGridAdditionals(GridSupport.getAdditionals())
		
		// Daftar option select ynag digunakan di template grid, contoh: GENDER, BOOLEAN, dll
		.setGridOptions(GridSupport.getOptions())
		
		// Untuk menerjemahkan judul, deskripsi, dll yang ada di template grid
		//.setMessageHandler(null)
		
		// Opsi AdminProperties jika configuration file tidak di-set
		//.setProperties(null)
		
		// Untuk menyimpan data-data admin, seperti: template grid, authorization, dll
		.setRedisTemplate(redisTemplate)
		
		// Menggunakan nama bean, jika RequestMappingHandlerMapping di application context lebih dari satu
		//.setRequestMappingHandlerMappingBeanName(null)
		
		// Untuk sinkronisasi ke central
		.setRestHandler(restHandler)
		
		// Custom sinkronisasi ke central, secara default sudah tersedia 
		//.setSyncToCentral(null)
		
		// Flag Admin UI bisa diakses atau tidak
		.setWebEnabled(admin.getWebEnabled())
		
		// Lokasi resource Admin UI
		.setWebLocation(admin.getWebLocation())
		
		// Context Path untuk mengakses Admin UI
		.setWebPath(admin.getWebPath());
	}
	
	
	/*
	 * ADMIN CREDENTIAL
	 */
	@Bean(name = AppConstants.Bean.Credential.ADMIN)
	SecurityCredential adminCredential(
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON)
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new RedisMemoryCredential()
		
		// Lokasi file kredensial
		.setCredentialFile(admin.getCredentialFile())
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Optional expiry jika tidak didefinisikan di credential file
		//.setExpiry(null)
		
		// Optional passwordType jika tidak didefinisikan di credential file
		//.setPasswordType(null)
		
		// AppId akan digabungkan dengan prefix untuk key
		//.setRedisAppIdEnabled(null)
		
		// Redis prefix untuk key
		.setRedisPrefix("ADMIN-CREDENTIAL")
		
		// Redis template
		.setRedisTemplate(redisTemplate)
		
		// Optional daftar user jika tidak didefinisikan di credential file
		//.setUsers(null)
		;
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
		AppProperties.Admin admin = appProperties.getAdmin();
		if (Boolean.TRUE.equals(admin.getUseBasicAuth())) {
			// Basic Auth Security
			return new WebMvcBasicAuthSecurity()
			.setCredential(credential)
			.setRealm("Admin");
		} else {
			return new WebMvcAdminSecurity()
			.setAdminHandler(adminHandler)
			.setCredential(credential)
			.setDataMapper(dataMapper)
			
			// Remote host dicek atau tidak
			//.setEnableRemoteHost(null)
			
			// User Agent dicek atau tidal
			//.setEnableUserAgent(null)
			
			// Nama header untuk token
			//.setHeaderKey(null)
			;
		}
	}
	
}
