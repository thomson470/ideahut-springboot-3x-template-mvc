package net.ideahut.springboot.template.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.filter.SecurityAuthorizationFilter;
import net.ideahut.springboot.filter.WebMvcRequestFilter;
import net.ideahut.springboot.security.SecurityAuthorization;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Bean
	protected FilterRegistrationBean<WebMvcRequestFilter> defaultRequestFilter(
		Environment environment,
		AppProperties appProperties,
		RequestMappingHandlerMapping handlerMapping
	) {		
		return FrameworkUtil.createFilterBean(
			environment,
			new WebMvcRequestFilter()
				.setHandlerMapping(handlerMapping)
				.setCorsHeaders(appProperties.getCors())
				.setTraceEnable(true)
				.setEnableTimeResult(true)
				.initialize(), 
			1, 
			"/*"
		);
	}
	
	@Bean
	protected FilterRegistrationBean<SecurityAuthorizationFilter> adminFilter(
		Environment environment,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN) SecurityAuthorization adminSecurity
	) {
		AdminProperties properties = adminHandler.getProperties();
		List<String> paths = new ArrayList<>();
		paths.add(properties.getApi().getRequestPath() + "/*");
		if (properties.getResource() != null && properties.getResource().getRequestPath() != null) {
			paths.add(properties.getResource().getRequestPath() + "/*");
		}
		return FrameworkUtil.createFilterBean(
			environment,
			new SecurityAuthorizationFilter()
				.setSecurityAuthorization(adminSecurity),
			2,
			paths.toArray(new String[0])
		);
	}
	
}
