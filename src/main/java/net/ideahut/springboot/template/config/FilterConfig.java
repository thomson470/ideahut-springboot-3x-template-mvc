package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.filter.DefaultRequestFilter;
import net.ideahut.springboot.filter.SecurityAuthorizationFilter;
import net.ideahut.springboot.security.SecurityAuthorization;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Autowired
	private Environment environment;
	@Autowired
	private AppProperties appProperties;
	
	@Bean
	protected FilterRegistrationBean<DefaultRequestFilter> defaultRequestFilter() {		
		return FrameworkUtil.createFilterBean(
			environment,
			new DefaultRequestFilter()
				.setCORSHeaders(appProperties.getCors())
				.setRequestWrapperEnable(true)
				.setTraceEnable(true)
				.initialize(), 
			1, 
			"/*"
		);
	}
	
	@Bean
	protected FilterRegistrationBean<SecurityAuthorizationFilter> adminFilter(
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN) SecurityAuthorization adminSecurity
	) {
		return FrameworkUtil.createFilterBean(
			environment,
			new SecurityAuthorizationFilter()
				.setSecurityAuthorization(adminSecurity),
			2,
			adminHandler.getProperties().getResource().getRequestPath() + "/*",
			adminHandler.getProperties().getApi().getRequestPath() + "/*"
		);
	}
	
}
