package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
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
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamHandlerImpl;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.entity.api.ApiCrud;
import net.ideahut.springboot.template.entity.api.ApiCrudAction;
import net.ideahut.springboot.template.entity.api.ApiCrudField;
import net.ideahut.springboot.template.entity.api.ApiCrudFilter;
import net.ideahut.springboot.template.entity.api.ApiCrudItem;
import net.ideahut.springboot.template.entity.api.ApiCrudRole;
import net.ideahut.springboot.template.entity.api.ApiRequestItem;
import net.ideahut.springboot.template.entity.api.ApiRequestRole;
import net.ideahut.springboot.template.entity.api.ApiRole;
import net.ideahut.springboot.template.entity.job.JobGroup;
import net.ideahut.springboot.template.entity.job.JobInstance;
import net.ideahut.springboot.template.entity.job.JobTrigger;
import net.ideahut.springboot.template.entity.job.JobTriggerConfig;
import net.ideahut.springboot.template.entity.job.JobType;
import net.ideahut.springboot.template.entity.job.JobTypeParam;
import net.ideahut.springboot.template.entity.system.SysParam;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi handler:
 * - ApiHandler
 * - AuditHandler
 * - CacheGroupHandler
 * - CacheHandler
 * - CrudHandler
 * - ReportHandler
 * - MailHandler
 * - SysParamHandler
 * - SchedulerHandler
 */
@Configuration
class HandlerConfig {
	
	/*
	 * INIT
	 */
	@Bean
	protected InitHandler initHandler(
		ApplicationContext applicationContext		
	) {
		return new InitHandlerImpl()
		.setEndpoint(() -> "http://localhost:" + FrameworkUtil.getPort(applicationContext) + "/warmup");
	}
	
	/*
	 * API
	 */
	@Bean
	protected ApiHandler apiHandler(
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Redis.COMMON) RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
		
	) {
		return new ApiHandlerImpl()
		.setEnableSync(Boolean.TRUE)
		.setEnableCrud(Boolean.TRUE)
		.setDataMapper(dataMapper)
		.setEntityClass(
			new ApiHandlerImpl.EntityClass()
			.setCrud(ApiCrud.class)
			.setCrudAction(ApiCrudAction.class)
			.setCrudField(ApiCrudField.class)
			.setCrudFilter(ApiCrudFilter.class)
			.setCrudItem(ApiCrudItem.class)
			.setCrudRole(ApiCrudRole.class)
			.setRequestItem(ApiRequestItem.class)
			.setRequestRole(ApiRequestRole.class)
			.setRole(ApiRole.class)
		)
		.setEntityTrxManager(entityTrxManager)
		.setPrefix("API-HANDLER")
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(taskHandler);
	}

	/*
	 * AUDIT
	 */
	@Bean
	protected AuditHandler auditHandler(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Task.AUDIT) TaskHandler taskHandler
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
		.setTaskHandler(auditAsync);
		*/
	}
	
	/*
	 * CACHE GROUP
	 */
	@Bean
	protected CacheGroupHandler cacheGroupHandler(
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
	protected CacheHandler cacheHandler(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Redis.COMMON) RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
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
	protected MailHandler mailHandler(
		AppProperties appProperties,
		@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
    ) {
		return new MailHandlerImpl()
		.setTaskHandler(taskHandler)
		.setMailProperties(appProperties.getMail());
	}
	
	/*
	 * GRID
	 */
	@Bean
	protected GridHandler gridHandler(
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
	protected SysParamHandler sysParamHandler(
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
	protected SchedulerHandler schedulerHandler(
		ApplicationContext applicationContext,
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
	) {
		return new SchedulerHandlerImpl()
		.setApplicationContext(applicationContext)
		.setEntityClass(new JobEntityClass()
			.setTrxManagerName("")
			.setGroup(JobGroup.class)
			.setInstance(JobInstance.class)
			.setTrigger(JobTrigger.class)
			.setTriggerConfig(JobTriggerConfig.class)
			.setType(JobType.class)
			.setTypeParam(JobTypeParam.class)
		)
		.setEntityTrxManager(entityTrxManager)
		.setInstanceId(applicationContext.getId())
		.setJobPackages(AppConstants.PACKAGE + ".job")
		.setTaskHandler(taskHandler);
	}
	
	
	/*
	 * REPORT
	 */
	@Bean
	protected ReportHandler reportHandler() {
		return new ReportHandlerImpl();
	}
	
}
