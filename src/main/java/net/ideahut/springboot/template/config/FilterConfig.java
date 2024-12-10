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
import net.ideahut.springboot.filter.WebMvcRequestFilter;
import net.ideahut.springboot.filter.WebMvcSecurityFilter;
import net.ideahut.springboot.helper.WebMvcHelper;
import net.ideahut.springboot.security.WebMvcSecurity;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Bean
	FilterRegistrationBean<WebMvcRequestFilter> defaultRequestFilter(
		Environment environment,
		AppProperties appProperties,
		RequestMappingHandlerMapping handlerMapping
	) {		
		return WebMvcHelper.createFilterBean(
			environment,
			new WebMvcRequestFilter()
				.setHandlerMapping(handlerMapping)
				.setCORSHeaders(appProperties.getCors())
				.setTraceEnable(true)
				.setEnableTimeResult(true)
				.initialize(), 
			1, 
			"/*"
		);
	}
	
	@Bean
	FilterRegistrationBean<WebMvcSecurityFilter> adminFilter(
		Environment environment,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN) 
		WebMvcSecurity adminSecurity
	) {
		List<String> paths = new ArrayList<>();
		// API wajib ada
		paths.add(adminHandler.getApiPath() + "/*");
		if (adminHandler.isWebEnabled()) {
			// Web tidak wajib ada
			paths.add(adminHandler.getWebPath() + "/*");
		}
		return WebMvcHelper.createFilterBean(
			environment,
			new WebMvcSecurityFilter()
				.setWebMvcSecurity(adminSecurity),
			2,
			paths.toArray(new String[0])
		);
		
	}
	
}
