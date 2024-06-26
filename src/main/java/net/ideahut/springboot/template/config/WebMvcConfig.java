package net.ideahut.springboot.template.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.config.WebMvcBasicConfig;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.interceptor.RequestInterceptor;

/*
 * Konfigurasi Web MVC
 * 
 */
@Configuration
@EnableWebMvc
class WebMvcConfig extends WebMvcBasicConfig {
	
	private final DataMapper dataMapper;
	private final RequestInterceptor requestInterceptor;
	private final AdminHandler adminHandler;
	
	WebMvcConfig(
		DataMapper dataMapper,
		RequestInterceptor requestInterceptor,
		AdminHandler adminHandler
	) {
		this.dataMapper = dataMapper;
		this.requestInterceptor = requestInterceptor;
		this.adminHandler = adminHandler;
	}
	
	
	@Override
	protected String parameterName() {
		return "_fmt";
	}

	@Override
	protected boolean enableAcceptHeader() {
		return true;
	}

	@Override
	protected DataMapper dataMapper() {
		return dataMapper;
	}

	@Override
	protected HandlerInterceptor[] handlerInterceptors() {
		return new HandlerInterceptor[] {
			requestInterceptor
		};
	}

	@Override
	protected boolean enableExtension() {
		return false;
	}
	
	@Override
	protected Map<String, MediaType> mediaTypes() {
		return new HashMap<>();
	}
	
	
	/*
	 * Stream download
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	    taskExecutor.setCorePoolSize(10);
	    taskExecutor.setMaxPoolSize(10);
	    taskExecutor.afterPropertiesSet();
	    configurer.setTaskExecutor(taskExecutor);
		super.configureAsyncSupport(configurer);
	}

	
	/*
	 * Resource Handlers
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		AdminProperties.Resource resource = adminHandler.getProperties().getResource();
		if (resource != null) {
			registry
			.addResourceHandler(resource.getRequestPath() + "/**")
			.addResourceLocations(resource.getLocations())
			.setCacheControl(CacheControl.maxAge(60, TimeUnit.DAYS))
	        .resourceChain(false)
	        .addResolver(new VersionResourceResolver().addContentVersionStrategy(resource.getRequestPath() + "/**"));
		}
		super.addResourceHandlers(registry);
	}
	
}
