package net.ideahut.springboot.template.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.definition.FilterDefinition;
import net.ideahut.springboot.filter.WebMvcRequestFilter;
import net.ideahut.springboot.filter.WebMvcSecurityFilter;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.WebMvcHelper;
import net.ideahut.springboot.security.WebMvcSecurity;
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
		FilterDefinition filter = ObjectHelper.useOrDefault(
			appProperties.getFilter(), 
			FilterDefinition::new
		);
		return WebMvcHelper.createFilterBean(
			environment,
			new WebMvcRequestFilter()
				.setCorsHeaders(filter.getCorsHeaders())
				.setEnableTimeResult(filter.getEnableTimeResult())
				.setHandlerMapping(handlerMapping)
				.setTraceEnable(filter.getTraceEnable())
				.setTraceKey(filter.getTraceKey())
				.initialize(), 
			1, 
			"/*"
		);
	}
	
	@Bean
	FilterRegistrationBean<WebMvcSecurityFilter> adminFilter(
		Environment environment,
		AdminHandler adminHandler,
		WebMvcSecurity adminSecurity
	) {
		List<String> paths = new ArrayList<>();
		paths.add(adminHandler.getApiPath() + "/*");
		if (adminHandler.isWebEnabled()) {
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
