package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.task.TaskHandlerImpl;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi TaskHandler
 * Untuk proses asynchronous
 */
@Configuration
class TaskConfig {
	
	@Autowired
	private AppProperties appProperties;
	
	@Primary
	@Bean(name = AppConstants.Bean.Task.COMMON, destroyMethod = "shutdown")
    protected TaskHandler commonTask() {
		return new TaskHandlerImpl()
		.setTaskProperties(appProperties.getTask().getCommon());
    }
	
	@Bean(name = AppConstants.Bean.Task.AUDIT, destroyMethod = "shutdown")
	protected TaskHandler auditTask() {
		return new TaskHandlerImpl()
		.setTaskProperties(appProperties.getTask().getAudit());
    }
	
}
