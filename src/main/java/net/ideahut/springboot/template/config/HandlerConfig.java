package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.DatabaseMultiAuditHandler;
import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.cache.CacheHandler;
import net.ideahut.springboot.cache.RedisCacheGroupHandler;
import net.ideahut.springboot.cache.RedisCacheHandler;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridHandlerImpl;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.init.InitHandlerImpl;
import net.ideahut.springboot.job.JobEntityClass;
import net.ideahut.springboot.job.SchedulerHandler;
import net.ideahut.springboot.job.SchedulerHandlerImpl;
import net.ideahut.springboot.job.entity.JobGroup;
import net.ideahut.springboot.job.entity.JobInstance;
import net.ideahut.springboot.job.entity.JobTrigger;
import net.ideahut.springboot.job.entity.JobTriggerConfig;
import net.ideahut.springboot.job.entity.JobType;
import net.ideahut.springboot.job.entity.JobTypeParam;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.rest.OkHttpRestHandler;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamHandlerImpl;
import net.ideahut.springboot.sysparam.entity.SysParam;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi handler:
 * - AuditHandler
 * - CacheGroupHandler
 * - CacheHandler
 * - CrudHandler
 * - GridHandler
 * - InitHandler
 * - ReportHandler
 * - MailHandler
 * - RestHandler
 * - SysParamHandler
 * - SchedulerHandler
 */
@Configuration
class HandlerConfig {
	
	/*
	 * INIT
	 */
	@Bean
	InitHandler initHandler(
		ApplicationContext applicationContext		
	) {
		return new InitHandlerImpl()
		.setEndpoint(() -> "http://localhost:" + FrameworkUtil.getPort(applicationContext) + "/warmup");
	}

	/*
	 * AUDIT
	 */
	@Bean
	AuditHandler auditHandler(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Task.AUDIT) 
		TaskHandler taskHandler
	) {
		return new DatabaseMultiAuditHandler()
		.setEntityTrxManager(entityTrxManager)
		.setProperties(appProperties.getAudit().getProperties())
		.setTaskHandler(taskHandler)
		.setRejectNonAuditEntity(true);
		/*
		return new DatabaseSingleAuditHandler()
		.setEntityTrxManager(entityTrxManager)
		.setProperties(appProperties.getAudit().getProperties())
		.setTaskHandler(taskHandler)
		.setRejectNonAuditEntity(true);
		*/
	}
	
	/*
	 * CACHE GROUP
	 */
	@Bean
	CacheGroupHandler cacheGroupHandler(
		AppProperties appProperties,
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
	) throws Exception {
		return new RedisCacheGroupHandler()
		.setDataMapper(dataMapper)
		.setGroups(appProperties.getCache().getGroups())
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(taskHandler);
	}
	
	/*
	 * CACHE
	 */
	@Bean
	CacheHandler cacheHandler(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) 
		TaskHandler taskHandler
	) throws Exception {
		return new RedisCacheHandler()
		.setDataMapper(dataMapper)
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(taskHandler)
		.setLimit(100)
		.setNullable(true)
		.setPrefix("_test");
	}
	
	/*
	 * MAIL
	 */
	@Bean
	MailHandler mailHandler(
		AppProperties appProperties,
		@Qualifier(AppConstants.Bean.Task.COMMON) 
		TaskHandler taskHandler
    ) {
		return new MailHandlerImpl()
		.setTaskHandler(taskHandler)
		.setMailProperties(appProperties.getMail());
	}
	
	/*
	 * GRID
	 */
	@Bean
	GridHandler gridHandler(
		AppProperties appProperties,
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Grid grid = appProperties.getGrid();
		return new GridHandlerImpl()
		.setDataMapper(dataMapper)
		.setLocation(grid.getLocation())
		.setDefinition(grid.getDefinition())
		.setRedisTemplate(redisTemplate)
		.setAdditionals(GridSupport.getAdditionals())
		.setOptions(GridSupport.getOptions());
	}
	
	/*
	 * SYSPARAM
	 */
	@Bean
	SysParamHandler sysParamHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate,
		EntityTrxManager entityTrxManager
	) {
		return new SysParamHandlerImpl()
		.setDataMapper(dataMapper)
		.setEntityTrxManager(entityTrxManager)
		.setEntityClass(new SysParamHandlerImpl.EntityClass()
			.setSysParam(SysParam.class)	
		)
		.setRedisTemplate(redisTemplate);
	}
	
	
	/*
	 * SCHEDULER
	 */
	@Bean
	SchedulerHandler schedulerHandler(
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Task.COMMON) 
		TaskHandler taskHandler
	) {
		return new SchedulerHandlerImpl()
		.setEntityClass(new JobEntityClass()
			.setTrxManagerName(null)
			.setGroup(JobGroup.class)
			.setInstance(JobInstance.class)
			.setTrigger(JobTrigger.class)
			.setTriggerConfig(JobTriggerConfig.class)
			.setType(JobType.class)
			.setTypeParam(JobTypeParam.class)
		)
		.setDataMapper(dataMapper)
		.setEntityTrxManager(entityTrxManager)
		.setInstanceId(null)
		.setJobPackages(Application.Package.APPLICATION + ".job")
		.setTaskHandler(taskHandler);
	}
	
	
	/*
	 * REST
	 */
	@Bean
	RestHandler restHandler(
		DataMapper dataMapper
	) {
		return new OkHttpRestHandler()
		.setDataMapper(dataMapper);
	}
	
	
	/*
	 * REPORT
	 */
	@Bean
	ReportHandler reportHandler() {
		return new ReportHandlerImpl();
	}
	
}
