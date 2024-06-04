package net.ideahut.springboot.template.config;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminHandlerImpl;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.admin.WebMvcAdminSecurity;
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
	
	@Bean()
	protected AdminHandler adminHandler(
		ApplicationContext applicationContext,
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new AdminHandlerImpl()
		.setConfigFile(admin.getConfigFile())
		.setDataMapper(dataMapper)
		.setGridAdditionals(GridSupport.getAdditionals())
		.setGridOptions(GridSupport.getOptions())
		.setProperties(admin)
		.setRedisTemplate(redisTemplate)
		.setAfterReload(properties -> {
			AdminProperties.Central central = properties.getCentral();
			if (central != null && properties.getResource() != null) {
				String resDir = central.getResourceDirectory() != null ? central.getResourceDirectory().trim() : "";
				if (resDir.isEmpty()) {
					return;
				}
				File resourceDirectory = new File(resDir);
				if (resourceDirectory.isDirectory()) {
					File jsDir = new File(resourceDirectory, "js");
					File jsFile = null;
					if (jsDir.isDirectory()) {
						for (File file : jsDir.listFiles()) {
							if (file.getName().startsWith("app.") && file.getName().endsWith(".js") ){
								jsFile = file;
								break;
							}
						}
					}
					if (jsFile != null) {
						String ltxt = FileUtils.readFileToString(jsFile, StandardCharsets.UTF_8);
						int idx = ltxt.indexOf("app:{title:\"");
						if (idx != -1) {
							String title = properties.getResource().getTitle();
							title = title != null ? title : "";
							if (title.isEmpty()) {
								title = applicationContext.getId();
							}
							String rtxt = ltxt.substring(idx + 12);
							ltxt = ltxt.substring(0, idx + 12);
							idx = rtxt.indexOf("\"");
							if (idx != -1) {
								rtxt = rtxt.substring(idx);
							}
							ltxt += title + rtxt;
						}
						FileUtils.write(jsFile, ltxt, StandardCharsets.UTF_8);
					}
				}
			}
		});
	}
	
	@Bean(name = AppConstants.Bean.Credential.ADMIN)
	protected RedisMemoryCredential adminCredential(
		AppProperties appProperties,
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
	protected WebMvcAdminSecurity adminSecurity(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Credential.ADMIN) SecurityCredential credential
	) {
		return new WebMvcAdminSecurity()
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
