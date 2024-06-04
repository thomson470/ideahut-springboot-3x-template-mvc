package net.ideahut.springboot.template.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamReloader;
import net.ideahut.springboot.sysparam.SysParamRemover;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.entity.system.SysParam;
import net.ideahut.springboot.template.support.ApiSupport;

@Component
@ComponentScan
class AppEntityPostListener implements EntityPostListener, InitializingBean {
	
	private final AuditHandler auditHandler;
	private final TaskHandler taskHandler;
	private final ApiHandler apiHandler;
	private final SysParamHandler sysParamHandler;
	
	private Map<Class<?>, EntityPostListener> entities = new HashMap<>();
	
	@Autowired
	AppEntityPostListener(
		TaskHandler taskHandler,
		AuditHandler auditHandler,
		ApiHandler apiHandler,
		SysParamHandler sysParamHandler
	) {
		this.taskHandler = taskHandler;
		this.auditHandler = auditHandler;
		this.apiHandler = apiHandler;
		this.sysParamHandler = sysParamHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		entities.clear();
		
		// SysParm
		entities.put(SysParam.class, new EntityPostListener() {
			@Override
			public void onPostUpdate(Object entity) {
				if (sysParamHandler instanceof SysParamReloader reloader) {
					SysParam sysParam = (SysParam) entity;
					reloader.reloadSysParam(sysParam.getId().getSysCode(), sysParam.getId().getParamCode());
				}
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostUpdate(entity);
			}
			@Override
			public void onPostDelete(Object entity) {
				if (sysParamHandler instanceof SysParamRemover remover) {
					SysParam sysParam = (SysParam) entity;
					remover.removeSysParam(sysParam.getId().getSysCode(), sysParam.getId().getParamCode());
				}
			}
		});
		
		// Api
		entities.putAll(ApiSupport.getEntityPostListeners(apiHandler));
		
	}

	@Override
	public void onPostDelete(Object entity) {
		taskHandler.execute(() -> {
			onAudit(entity, "DELETE");
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostDelete(entity);
			}
		});
	}

	@Override
	public void onPostInsert(Object entity) {
		taskHandler.execute(() -> {
			onAudit(entity, "INSERT");
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostInsert(entity);
			}
		});
	}

	@Override
	public void onPostUpdate(Object entity) {
		taskHandler.execute(() -> {
			onAudit(entity, "UPDATE");
			EntityPostListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPostUpdate(entity);
			}
		});
	}
	
	// AUDIT
	private void onAudit(Object entity, String action) {
		Audit audit = entity.getClass().getAnnotation(Audit.class);
		if (audit != null && audit.value()) {
			auditHandler.save(action, entity);
		}
	}
	
}
