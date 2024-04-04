package net.ideahut.springboot.template.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudHandlerImpl;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.crud.CrudProps;
import net.ideahut.springboot.crud.CrudRequest;
import net.ideahut.springboot.crud.CrudResource;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.SessionCallable;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridHandlerImpl;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.init.InitHandlerImpl;
import net.ideahut.springboot.job.JobService;
import net.ideahut.springboot.job.SchedulerHandler;
import net.ideahut.springboot.job.SchedulerHandlerImpl;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.sysparam.SysParamDto;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamHandlerImpl;
import net.ideahut.springboot.sysparam.SysParamReloader;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.entity.EntityFill;
import net.ideahut.springboot.template.entity.SysParam;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi handler:
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
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AppProperties appProperties;
	
	@Bean
	protected InitHandler initHandler() {
		InitHandler.Endpoint endpoint = new InitHandler.Endpoint() {
			@Override
			public String getUrl() {
				return "http://localhost:" + FrameworkUtil.getPort(applicationContext) + "/warmup";
			}
		};
		return new InitHandlerImpl()
		.setEndpoint(endpoint);
	}

	@Bean
	protected AuditHandler auditHandler(
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
	
	@Bean
	protected CacheGroupHandler cacheGroupHandler(
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
	
	@Bean
	protected ReportHandler reportHandler() {
		return new ReportHandlerImpl();
	}
	
	@Bean
	protected MailHandler mailHandler(
    	@Qualifier(AppConstants.Bean.Task.COMMON) TaskHandler taskHandler
    ) {
		return new MailHandlerImpl()
		.setTaskHandler(taskHandler)
		.setMailProperties(appProperties.getMail());
	}
	
	@Bean
	protected GridHandler gridHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		return new GridHandlerImpl()
		.setApplicationContext(applicationContext)
		.setDataMapper(dataMapper)
		.setLocation(appProperties.getGridLocation())
		.setRedisTemplate(redisTemplate)
		.setAdditionals(GridSupport.getAdditionals())
		.setOptions(GridSupport.getOptions());
	}
	
	@Bean
	protected SysParamHandler sysParamHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate,
		EntityTrxManager entityTrxManager
	) {
		return new SysParamHandlerImpl()
		.setDataMapper(dataMapper)
		.setRedisTemplate(redisTemplate)
		.setReloader(new SysParamReloader() {
			@Override
			public List<SysParamDto> reload(Collection<String> sysCodes) {
				TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
				return trxManagerInfo.transaction(new SessionCallable<List<SysParamDto>>() {
					@Override
					public List<SysParamDto> call(Session session) throws Exception {
						boolean notEmpty = sysCodes != null && !sysCodes.isEmpty();
						Query<SysParam> query = session.createQuery(
							"from SysParam " + (notEmpty ? "where id.sysCode in (?1)": ""), 
							SysParam.class
						);
						if (notEmpty) {
							query.setParameter(1, sysCodes);
						}
						List<SysParamDto> dtos = new ArrayList<SysParamDto>();
						List<SysParam> entities = query.getResultList();
						while (!entities.isEmpty()) {
							SysParam entity = entities.remove(0);
							SysParamDto dto = new SysParamDto(entity.getId().getSysCode(), entity.getId().getParamCode());
							BeanUtils.copyProperties(entity, dto, "id");
							dtos.add(dto);
						}
						return dtos;
					}
				});
			}
		});
	}
	
	@Bean
	protected SchedulerHandler schedulerHandler(
		DataMapper dataMapper,
		JobService jobService
	) {
		return new SchedulerHandlerImpl()
		.setApplicationContext(applicationContext)
		.setInstanceId(appProperties.getInstanceId())
		.setJobPackages(AppConstants.PACKAGE + ".job")
		.setJobService(jobService);
	}
	
	@Bean
	protected CrudHandler crudHandler(
		EntityTrxManager entityTrxManager,
		DataMapper dataMapper,
		CrudResource resource,
		CrudPermission permission
	) {
		return new CrudHandlerImpl()
		.setEntityTrxManager(entityTrxManager)
		.setResource(resource);
		//.setPermission(permission);
	}
	
	// Contoh ambil Crud Resource
	// Bisa disimpan di database untuk Entity yang boleh di-crud
	// Ditambah dengan Permission untuk membatasi akses
	@Bean
	protected CrudResource crudResource(
		EntityTrxManager entityTrxManager
	) {
		return new CrudResource() {
			@Override
			public CrudProps getCrudProps(String manager, String name) {
				try {
					Class<?> clazz = FrameworkUtil.classOf(EntityFill.class.getPackageName() + "." + name);
					TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
					if (manager != null && !manager.isEmpty()) {
						trxManagerInfo = entityTrxManager.getTrxManagerInfo(manager);
					}
					EntityInfo entityInfo = trxManagerInfo.getEntityInfo(clazz);
					CrudProps resource = new CrudProps();
					resource.setEntityInfo(entityInfo);
					resource.setMaxLimit(200);
					resource.setUseNative(false);
					return resource;
				} catch (Exception e) {
					throw FrameworkUtil.exception(e);
				}
			}
		};
	}
	
	// Contoh cek akses crud
	// Bisa disimpan di database dengan memasangkan Resource & Role
	// Termasuk bisa diset Specific Filter di bagian ini
	@Bean
	protected CrudPermission crudPermission() {
		return new CrudPermission() {
			
			@Override
			public boolean isCrudAllowed(CrudAction action, CrudRequest request) {
				//request.setSpesifics(null);
				return true;
			}
		};
	}
	
}
