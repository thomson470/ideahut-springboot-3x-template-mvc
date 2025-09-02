package net.ideahut.springboot.template.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.DatabaseAuditProperties;
import net.ideahut.springboot.audit.DatabaseMultiAuditHandler;
import net.ideahut.springboot.audit.DatabaseSingleAuditHandler;
import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.cache.CacheHandler;
import net.ideahut.springboot.cache.MemoryCacheGroupHandler;
import net.ideahut.springboot.cache.MemoryCacheHandler;
import net.ideahut.springboot.cache.RedisCacheGroupHandler;
import net.ideahut.springboot.cache.RedisCacheHandler;
import net.ideahut.springboot.definition.CacheGroupDefinition;
import net.ideahut.springboot.definition.DatabaseAuditDefinition;
import net.ideahut.springboot.definition.GridDefinition;
import net.ideahut.springboot.definition.KafkaDefinition;
import net.ideahut.springboot.definition.RestDefinition;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridHandlerImpl;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.init.InitHandlerImpl;
import net.ideahut.springboot.job.SchedulerHandler;
import net.ideahut.springboot.job.SchedulerHandlerImpl;
import net.ideahut.springboot.kafka.KafkaHandler;
import net.ideahut.springboot.kafka.KafkaHandlerImpl;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.TimeValue;
import net.ideahut.springboot.redis.RedisParam;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.rest.OkHttpRestHandler;
import net.ideahut.springboot.rest.RestHandler;
import net.ideahut.springboot.serializer.BinarySerializer;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamHandlerImpl;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;

/*
 * Konfigurasi handler:
 * - AuditHandler
 * - CacheGroupHandler
 * - CacheHandler
 * - CrudHandler
 * - GridHandler
 * - InitHandler
 * - MailHandler
 * - ReportHandler
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
		.setServletCall(null);
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
		DatabaseAuditDefinition audit = ObjectHelper.useOrDefault(
			appProperties.getAudit(),
			DatabaseAuditDefinition::new
		);
		DatabaseAuditProperties properties = ObjectHelper.useOrDefault(audit.getProperties(), DatabaseAuditProperties::new);
		DatabaseAuditProperties.Table table = ObjectHelper.useOrDefault(properties.getTable(), DatabaseAuditProperties.Table::new);
		String prefix = ObjectHelper.useOrDefault(table.getPrefix(), "audit_");
		table.setPrefix(prefix);
		properties.setTable(table);
		audit.setProperties(properties);
		if (Boolean.FALSE.equals(audit.getUseMultiTable())) {
			return new DatabaseSingleAuditHandler()
			.setEntityTrxManager(entityTrxManager)
			.setProperties(properties)
			.setRejectNonAuditEntity(!Boolean.FALSE.equals(audit.getRejectNonAuditEntity()))
			.setTaskHandler(taskHandler);
		} else {
			return new DatabaseMultiAuditHandler()
			.setEntityTrxManager(entityTrxManager)
			.setProperties(properties)
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
		BinarySerializer binarySerializer,
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) 
		TaskHandler taskHandler
	) {
		CacheGroupDefinition cache = ObjectHelper.useOrDefault(
			appProperties.getCache(), 
			CacheGroupDefinition::new
		);
		if (Boolean.TRUE.equals(cache.getUseLocalMemory())) {
			return new MemoryCacheGroupHandler()
			.setBinarySerializer(binarySerializer)
			.setGroups(cache.getGroups())
			.setTaskHandler(taskHandler);
		} else {
			return new RedisCacheGroupHandler()
			.setBinarySerializer(binarySerializer)
			.setGroups(cache.getGroups())
			.setRedisParam(
				new RedisParam<String, byte[]>(cache.getStorageKeyParam())
				.setRedisTemplate(redisTemplate)
			)
			.setTaskHandler(taskHandler);
		}
	}
	
	
	/*
	 * CACHE
	 */
	@Bean
	CacheHandler cacheHandler(
		AppProperties appProperties,
		BinarySerializer binarySerializer,
		@Qualifier(AppConstants.Bean.Redis.COMMON) 
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Task.COMMON) 
		TaskHandler taskHandler
	) {
		CacheGroupDefinition cache = ObjectHelper.useOrDefault(
			appProperties.getCache(), 
			CacheGroupDefinition::new
		);
		if (Boolean.TRUE.equals(cache.getUseLocalMemory())) {
			return new MemoryCacheHandler()
			.setBinarySerializer(binarySerializer)
			.setExpiryCheckInterval(TimeValue.of(TimeUnit.SECONDS, 30L))
			.setLimit(100)
			.setNullable(true)
			.setTaskHandler(taskHandler);
		} else {
			return new RedisCacheHandler()
			.setBinarySerializer(binarySerializer)
			.setLimit(100)
			.setNullable(true)
			.setRedisParam(
				new RedisParam<String, byte[]>()
				.setPrefix("_test")
				.setRedisTemplate(redisTemplate)
			)
			.setTaskHandler(taskHandler);
		}
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
		BinarySerializer binarySerializer,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		GridDefinition grid = ObjectHelper.useOrDefault(
			appProperties.getGrid(), 
			GridDefinition::new
		);
		return new GridHandlerImpl()
				
		// Daftar array yang digunakan di template grid, contoh: DAYS, MONTHS, dll		
		.setAdditionals(GridSupport.getAdditionals())
		
		// Serialize & deserialize byte array ke redis
		.setBinarySerializer(binarySerializer)
		
		// File definisi order, title, dll yang akan ditampilakan di UI
		.setDefinition(grid.getDefinition())
		
		// Directory lokasi file-file template
		.setLocation(grid.getLocation())
		
		// Untuk menerjemahkan judul, label, deskripsi, dll yang ada di template grid
		.setMessageHandler(null)
		
		// Daftar option select ynag digunakan di template grid, contoh: GENDER, BOOLEAN, dll
		.setOptions(GridSupport.getOptions())
		
		// RedisTemplate (jika null akan digunakan local memory)
		.setRedisTemplate(!Boolean.TRUE.equals(grid.getUseLocalMemory()) ? redisTemplate : null)
		
		// Mekanisme penyimpanan key di storage (redis / local memory)
		.setStorageKeyParam(grid.getStorageKeyParam());
	}
	
	
	/*
	 * SYSPARAM
	 */
	@Bean
	SysParamHandler sysParamHandler(
		BinarySerializer binarySerializer,
		RedisTemplate<String, byte[]> redisTemplate,
		EntityTrxManager entityTrxManager
	) {
		return new SysParamHandlerImpl()
				
		// Serialize & deserialize byte array ke redis		
		.setBinarySerializer(binarySerializer)
		
		// Daftar Entity class dan nama trxManager yang terkait dengan SysParamHandler
		// default semua class di package 'net.ideahut.springboot.sysparam.entity'
		.setEntityClass(null)
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// RedisTemplate dan definisi penyimpanan key-nya
		.setRedisParam(
			new RedisParam<String, byte[]>()	
			.setAppIdEnabled(true)
			.setEncryptEnabled(true)
			.setPrefix("SYS-PARAM")
			.setRedisTemplate(redisTemplate)
		);
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
		AppProperties appProperties,
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Task.REST)
		TaskHandler taskHandler
	) {
		RestDefinition rest = ObjectHelper.useOrDefault(
			appProperties.getRest(), 
			RestDefinition::new
		);
		return new OkHttpRestHandler()
		
		// Destroy Http client setelah mendapatkan respon
		.setClientDestroyable(rest.getClientDestroyable())
		
		// DataMapper
		.setDataMapper(dataMapper)
		
		// DefaultRestClient, untuk mensetting beberapa variable seperti proxy
		.setDefaultRestClient(rest.getDefaultRestClient())
		
		// Menambahkan waktu eksekusi di RestResponse (mulai dari request sampai mendapatkan response / error)
		.setEnableExecutionTime(rest.getEnableExecutionTime())
		
		// Membatasi jumlah request secara bersamaan, ini terkait dengan jumlah port lokal yang digunakan untuk request ke server
		// Jika true maka TaskHandler tidak boleh null, jumlah pool di TaskHandler menjadi batas jumlah request bersamaan
		.setEnableRequestLimit(rest.getEnableRequestLimit())
		
		// TaskHandler, jika enableRequestLimit=true
		.setTaskHandler(taskHandler);
	}
	
	
	/*
	 * REPORT
	 */
	@Bean
	ReportHandler reportHandler(
		AppProperties appProperties		
	) {
		return new ReportHandlerImpl();
	}
	
	
	/*
	 * KAFKA
	 */
	@Bean
	KafkaHandler kafkaHandler(
		AppProperties appProperties,
		BinarySerializer binarySerializer
	) {
		KafkaDefinition kafka = ObjectHelper.useOrDefault(
			appProperties.getKafka(), 
			KafkaDefinition::new
		);
		if (Boolean.FALSE.equals(kafka.getEnable())) {
			return KafkaHandler.empty();
		} else {
			return new KafkaHandlerImpl()
			.setBinarySerializer(binarySerializer)
			.setConfigurationFile(kafka.getConfigurationFile())
			.setProperties(kafka.getProperties());
		}
	}
	
}
