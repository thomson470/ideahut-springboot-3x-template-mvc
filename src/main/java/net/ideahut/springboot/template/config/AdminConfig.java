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
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;

/*
 * Konfigurasi Admin
 * Dapat diakses menggunakan browser
 * http://<host>: <port>/_
 */

@Configuration
class AdminConfig {
	
	@Bean()
	AdminHandler adminHandler(
		ApplicationContext applicationContext,
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
		RedisTemplate<String, byte[]> redisTemplate,
		RestHandler restHandler
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new AdminHandlerImpl()
		.setAfterReload(handler -> AdminConfigHelper.afterReloadAdminResource(handler, applicationContext, ""))
		.setApiPath(admin.getApiPath())
		//.setCheckTokenCentral(null)
		.setConfigurationFile(admin.getConfigurationFile())
		.setDataMapper(dataMapper)
		.setGridAdditionals(GridSupport.getAdditionals())
		.setGridOptions(GridSupport.getOptions())
		//.setMessageHandler(null)
		//.setProperties(null)
		.setRedisTemplate(redisTemplate)
		.setRestHandler(restHandler)
		//.setSyncToCentral(null)
		.setWebEnabled(admin.getWebEnabled())
		.setWebLocation(admin.getWebLocation())
		.setWebPath(admin.getWebPath());
	}
	
	@Bean(name = AppConstants.Bean.Credential.ADMIN)
	SecurityCredential adminCredential(
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON)
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new RedisMemoryCredential()
		.setCredentialFile(admin.getCredentialFile())
		.setDataMapper(dataMapper)
		//.setExpiry(null)
		//.setPasswordType(null)
		.setRedisPrefix("ADMIN-CREDENTIAL")
		.setRedisTemplate(redisTemplate)
		//.setUsers(null)
		;
	}
	
	
	@Bean(name = AppConstants.Bean.Security.ADMIN)
	WebMvcAdminSecurity adminSecurity(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Credential.ADMIN)
		SecurityCredential credential
	) {
		return new WebMvcAdminSecurity()
		.setAdminHandler(adminHandler)
		.setCredential(credential)
		.setDataMapper(dataMapper)
		//.setEnableRemoteHost(true)
		//.setEnableUserAgent(true)
		//.setProperties(adminHandler.getProperties())
		;
	}
	
	/*
	@Bean(name = AppConstants.Bean.Security.ADMIN)
	BasicAuthSecurity adminSecurity(
		@Qualifier(AppConstants.Bean.Credential.ADMIN) SecurityCredential credential
	) {//-
		return new BasicAuthSecurity()
		.setCredential(credential)
		.setRealm("Admin");//-
	}//-
	*/
	
}
