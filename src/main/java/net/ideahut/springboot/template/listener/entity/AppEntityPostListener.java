package net.ideahut.springboot.template.listener.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.support.ApiSupport;
import net.ideahut.springboot.template.support.SystemSupport;

@Component
@ComponentScan
class AppEntityPostListener implements EntityPostListener, BeanConfigure<EntityPostListener> {
	
	private final DataMapper dataMapper;
	private final EntityTrxManager entityTrxManager;
	private final AuditHandler auditHandler;
	private final TaskHandler taskHandler;
	private final ApiHandler apiHandler;
	private final SysParamHandler sysParamHandler;
	
	private Map<Class<?>, EntityPostListener> entities = new HashMap<>();
	
	@Autowired
	AppEntityPostListener(
		DataMapper dataMapper,
		EntityTrxManager entityTrxManager,
		AuditHandler auditHandler,
		TaskHandler taskHandler,
		ApiHandler apiHandler,
		SysParamHandler sysParamHandler
	) {
		this.dataMapper = dataMapper;
		this.entityTrxManager = entityTrxManager;
		this.auditHandler = auditHandler;
		this.taskHandler = taskHandler;
		this.apiHandler = apiHandler;
		this.sysParamHandler = sysParamHandler;
	}
	
	@Override
	public Callable<EntityPostListener> onConfigureBean(ApplicationContext applicationContext) {
		AppEntityPostListener self = this;
		return new Callable<EntityPostListener>() {
			@Override
			public EntityPostListener call() throws Exception {
				
				entities.clear();
				
				// System
				entities.putAll(SystemSupport.getEntityPostListeners(sysParamHandler));
				
				// Api
				entities.putAll(ApiSupport.getEntityPostListeners(dataMapper, apiHandler, entityTrxManager));
				
				return self;
			}
		};
	}

	@Override
	public boolean isBeanConfigured() {
		return true;
	}

	@Override
	public void onPostDelete(Object entity) {
		AuditInfo auditInfo = AuditInfo.context();
		taskHandler.execute(() -> {
			auditInfo.setToContext();
			auditHandler.save("DELETE", entity);
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostDelete(entity);
			}
		});
	}

	@Override
	public void onPostInsert(Object entity) {
		AuditInfo auditInfo = AuditInfo.context();
		taskHandler.execute(() -> {
			auditInfo.setToContext();
			auditHandler.save("INSERT", entity);
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostInsert(entity);
			}
		});
	}

	@Override
	public void onPostUpdate(Object entity) {
		AuditInfo auditInfo = AuditInfo.context();
		taskHandler.execute(() -> {
			auditInfo.setToContext();
			auditHandler.save("UPDATE", entity);
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostUpdate(entity);
			}
		});
	}
	
}
