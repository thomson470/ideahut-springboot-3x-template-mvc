package net.ideahut.springboot.template.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.config.WebMvcBasicConfig;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.TimeValue;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.task.TaskManager;
import net.ideahut.springboot.template.app.AppConstants;

/*
 * Konfigurasi Web MVC
 * 
 */
@Configuration
@EnableWebMvc
class WebMvcConfig extends WebMvcBasicConfig {
	
	private final DataMapper dataMapper;
	private final HandlerInterceptor handlerInterceptor;
	private final AdminHandler adminHandler;
	private final AsyncTaskExecutor asyncTaskExecutor;
	
	@Autowired
	WebMvcConfig(
		DataMapper dataMapper,
		HandlerInterceptor handlerInterceptor,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Task.WEB_ASYNC)
		TaskHandler taskHandler
	) {
		this.dataMapper = dataMapper;
		this.handlerInterceptor = handlerInterceptor;
		this.adminHandler = adminHandler;
		AsyncTaskExecutor asyncTaskExecutorEi = null;
		if (ObjectHelper.isInstance(TaskManager.class, taskHandler)) {
			TaskManager taskManager = (TaskManager) taskHandler;
			asyncTaskExecutorEi = (AsyncTaskExecutor) taskManager.executor();
		}
		Assert.notNull(asyncTaskExecutorEi, "asyncTaskExecutor null");
		this.asyncTaskExecutor = asyncTaskExecutorEi;
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
			handlerInterceptor
		};
	}

	@Override
	protected boolean enableExtension() {
		return false;
	}
	
	@Override
	protected Map<String, MediaType> mediaTypes() {
		return Collections.emptyMap();
	}
	
	
	/*
	 * AsyncSupport -> Stream download
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(asyncTaskExecutor);
		super.configureAsyncSupport(configurer);
	}

	
	/*
	 * Resource Handlers
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (adminHandler.isWebEnabled() && !registry.hasMappingForPattern(adminHandler.getWebPath() + "/**")) {
			TimeValue maxAge = adminHandler.getWebCacheMaxAge();
			registry
			.addResourceHandler(adminHandler.getWebPath() + "/**")
			.addResourceLocations(adminHandler.getWebLocation())
			.setCacheControl(CacheControl.maxAge(maxAge.getValue(), maxAge.getUnit()))
	        .resourceChain(adminHandler.isWebResourceChain())
	        .addResolver(new VersionResourceResolver().addContentVersionStrategy(adminHandler.getWebPath() + "/**"));
		}
		super.addResourceHandlers(registry);
	}
	
}
