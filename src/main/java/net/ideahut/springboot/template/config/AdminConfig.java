package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminHandlerImpl;
import net.ideahut.springboot.admin.AdminSecurity;
import net.ideahut.springboot.mapper.DataMapper;
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
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AppProperties appProperties;
	
	
	@Bean()
	protected AdminHandler adminHandler(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new AdminHandlerImpl()
		.setApplicationContext(applicationContext)
		.setConfigFile(admin.getConfigFile())
		.setDataMapper(dataMapper)
		.setGridAdditionals(GridSupport.getAdditionals())
		.setGridOptions(GridSupport.getOptions())
		.setProperties(admin)
		.setRedisTemplate(redisTemplate);
	}
	
	@Bean(name = AppConstants.Bean.Credential.ADMIN)
	protected RedisMemoryCredential adminCredential(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new RedisMemoryCredential()
		.setConfigFile(admin.getCredentialFile())
		.setDataMapper(dataMapper)
		.setRedisPrefix("ADMIN-CREDENTIAL")
		.setRedisTemplate(redisTemplate);
	}
	
	
	@Bean(name = AppConstants.Bean.Security.ADMIN)
	protected AdminSecurity adminSecurity(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Credential.ADMIN) SecurityCredential credential
	) {
		return new AdminSecurity()
		.setCredential(credential)
		.setDataMapper(dataMapper)
		//.setEnableRemoteHost(true)
		//.setEnableUserAgent(true)
		.setProperties(adminHandler.getProperties());
	}
	
	/*
	@Bean(name = AppConstants.Bean.Security.ADMIN)
	protected BasicAuthSecurity adminSecurity(
		@Qualifier(AppConstants.Bean.Credential.ADMIN) SecurityCredential credential
	) {
		return new BasicAuthSecurity()
		.setCredential(credential)
		.setRealm("Admin");
	}
	*/
	
}
