package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.DatabaseMultiAuditHandler;
import net.ideahut.springboot.audit.DatabaseSingleAuditHandler;
import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.cache.CacheHandler;
import net.ideahut.springboot.cache.RedisCacheGroupHandler;
import net.ideahut.springboot.cache.RedisCacheHandler;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridHandlerImpl;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.init.InitHandlerImpl;
import net.ideahut.springboot.job.SchedulerHandler;
import net.ideahut.springboot.job.SchedulerHandlerImpl;
import net.ideahut.springboot.kafka.KafkaHandler;
import net.ideahut.springboot.kafka.KafkaHandlerImpl;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.rest.OkHttpRestHandler;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamHandlerImpl;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;
import net.ideahut.springboot.template.support.HandlerSupport;

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
 * - KafkaHandler
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
		
		// Endpoint untuk inisialisasi DispatcherServlet
		.setEndpoint(() -> "http://localhost:" + FrameworkHelper.getPort(applicationContext) + "/warmup")
		
		// Custom jika mekanisme berbeda
		//.setServletCall(null)
		;
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
		AppProperties.Audit audit = appProperties.getAudit();
		if (Boolean.TRUE.equals(audit.getIsSingleAudit())) {
			return new DatabaseSingleAuditHandler()
			.setEntityTrxManager(entityTrxManager)
			.setProperties(audit.getProperties())
			.setRejectNonAuditEntity(!Boolean.FALSE.equals(audit.getRejectNonAuditEntity()))
			.setTaskHandler(taskHandler);
		} else {
			return new DatabaseMultiAuditHandler()
			.setEntityTrxManager(entityTrxManager)
			.setProperties(audit.getProperties())
			.setRejectNonAuditEntity(!Boolean.FALSE.equals(audit.getRejectNonAuditEntity()))
			.setTaskHandler(taskHandler);
		}
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
				
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Directory lokasi file-file template
		.setLocation(grid.getLocation())
		
		// File definisi order, title, dll yang akan ditampilakan di UI
		.setDefinition(grid.getDefinition())
		
		// RedisTemplate
		.setRedisTemplate(redisTemplate)
		
		// Untuk menerjemahkan judul, label, deskripsi, dll yang ada di template grid
		//.setMessageHandler(null)
		
		// Daftar array yang digunakan di template grid, contoh: DAYS, MONTHS, dll
		.setAdditionals(GridSupport.getAdditionals())
		
		// Daftar option select ynag digunakan di template grid, contoh: GENDER, BOOLEAN, dll
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
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// Daftar Entity class dan nama trxManager yang terkait dengan SysParamHandler
		// default semua class di package 'net.ideahut.springboot.sysparam.entity'
		//.setEntityClass(null)
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// AppId akan digabungkan dengan prefix untuk key
		//.setRedisAppIdEnabled(null)
		
		// Redis prefix untuk key
		.setRedisPrefix("SYS_PARAM")
		
		// RedisTemplate
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
		
		// DataMapper		
		.setDataMapper(dataMapper)
		
		// Daftar Entity class dan nama trxManager yang terkait dengan SchedulerHandler
		// default semua class di package 'net.ideahut.springboot.job.entity'
		//.setEntityClass(null)
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// Untuk membagi job yang dieksekusi berdasarkan instance (lihat Entity JobTrigger)
		// Jika tidak diset akan digunakan ID dari application context atau dari property 'spring.application.name'
		//.setInstanceId(null)
		
		// Daftar package class-class job, sehingga di database bisa disimpan menggunakan SimpleClassName
		.setJobPackages(Application.Package.APPLICATION + ".job")
		
		// Service untuk menghandle fungsi-fungsi job, seperti mengambil trigger, menyimpan hasil, dll
		// Secara default sudah ada, hanya diperlukan jika custom
		//.setJobService(null)
		
		// SchedulerFactory
		// Secara default sudah ada, hanya diperlukan jika custom
		//.setSchedulerFactory(null)
		
		// TaskHandler
		.setTaskHandler(taskHandler)
		
		// Terkait dengan logger, nama key untuk id log yang digenerate random, default: 'traceId'
		//.setTraceKey(null)
		;
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
	ReportHandler reportHandler(
		AppProperties appProperties		
	) {
		if (Boolean.FALSE.equals(appProperties.getHandler().getEnableReport())) {
			return HandlerSupport.UNSUPPORTED_REPORT_HANDLER;
		} else {
			return new ReportHandlerImpl();
		}
	}
	
	
	/*
	 * KAFKA
	 */
	@Bean
	KafkaHandler kafkaHandler(
		AppProperties appProperties
	) {
		if (Boolean.FALSE.equals(appProperties.getHandler().getEnableKafka())) {
			return HandlerSupport.UNSUPPORTED_KAFKA_HANDLER;
		} else {
			return new KafkaHandlerImpl()
			.setConfigurationFile(appProperties.getKafkaConfigurationFile());
		}
	}
	
}
