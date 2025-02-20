package net.ideahut.springboot.template.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
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
import net.ideahut.springboot.config.WebMvcBasicConfig;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.task.TaskProperties;
import net.ideahut.springboot.template.interceptor.RequestInterceptor;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Web MVC
 * 
 */
@Configuration
@EnableWebMvc
class WebMvcConfig extends WebMvcBasicConfig {
	
	private final AppProperties appProperties;
	private final DataMapper dataMapper;
	private final RequestInterceptor requestInterceptor;
	private final AdminHandler adminHandler;
	
	@Autowired
	WebMvcConfig(
		AppProperties appProperties,
		DataMapper dataMapper,
		RequestInterceptor requestInterceptor,
		AdminHandler adminHandler
	) {
		this.appProperties = appProperties;
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
	 * AsyncSupport -> Stream download
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		TaskProperties properties = ObjectHelper.useOrDefault(appProperties.getWebAsync(), TaskProperties::new);
		Boolean allowCoreThreadTimeOut = ObjectHelper.useOrDefault(properties.getAllowCoreThreadTimeOut(), Boolean.FALSE);
		Integer awaitTerminationSeconds = properties.getAwaitTerminationSeconds();
		awaitTerminationSeconds = ObjectHelper.useOrElse(
			awaitTerminationSeconds != null && awaitTerminationSeconds > 0, 
			awaitTerminationSeconds, 
			() -> 0
		);
		Integer corePoolSize = properties.getCorePoolSize();
		corePoolSize = ObjectHelper.useOrElse(corePoolSize != null && corePoolSize > 1, corePoolSize, 1);
		Boolean daemon = ObjectHelper.useOrDefault(properties.getDaemon(), Boolean.FALSE);
		Integer keepAliveSeconds = properties.getKeepAliveSeconds();
		keepAliveSeconds = ObjectHelper.useOrElse(keepAliveSeconds != null && keepAliveSeconds > 0, keepAliveSeconds, 30);
		Integer maxPoolSize = properties.getMaxPoolSize();
		maxPoolSize = ObjectHelper.useOrElse(maxPoolSize != null && maxPoolSize > 0, maxPoolSize, corePoolSize);
		Integer queueCapacity = properties.getQueueCapacity();
		queueCapacity = ObjectHelper.useOrElse(queueCapacity != null && queueCapacity > 0, queueCapacity, Integer.MAX_VALUE);
		String threadNamePrefix = properties.getThreadNamePrefix();
		threadNamePrefix = ObjectHelper.useOrElse(
			threadNamePrefix != null && !threadNamePrefix.trim().isEmpty(), 
			threadNamePrefix, 
			() -> "WebAsync-" + System.nanoTime() + "-"
		);
		Integer threadPriority = properties.getThreadPriority();
		threadPriority = ObjectHelper.useOrElse(threadPriority != null && threadPriority > 0, threadPriority, Thread.NORM_PRIORITY);
		Boolean waitForJobsToCompleteOnShutdown = ObjectHelper.useOrDefault(properties.getWaitForJobsToCompleteOnShutdown(), Boolean.FALSE);
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
		taskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
		taskExecutor.setCorePoolSize(corePoolSize);
		taskExecutor.setDaemon(daemon);
		taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
		taskExecutor.setMaxPoolSize(maxPoolSize);
		taskExecutor.setQueueCapacity(queueCapacity);
		taskExecutor.setThreadNamePrefix(threadNamePrefix);
		taskExecutor.setThreadPriority(threadPriority);
		taskExecutor.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
		taskExecutor.afterPropertiesSet();
	    configurer.setTaskExecutor(taskExecutor);
		super.configureAsyncSupport(configurer);
	}

	
	/*
	 * Resource Handlers
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (adminHandler.isWebEnabled()) {
			registry
			.addResourceHandler(adminHandler.getWebPath() + "/**")
			.addResourceLocations(adminHandler.getWebLocation())
			.setCacheControl(CacheControl.maxAge(60, TimeUnit.DAYS))
	        .resourceChain(false)
	        .addResolver(new VersionResourceResolver().addContentVersionStrategy(adminHandler.getWebPath() + "/**"));
		}
		super.addResourceHandlers(registry);
	}
	
}
