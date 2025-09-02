package net.ideahut.springboot.template.listener.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.module.ModuleApi;
import net.ideahut.springboot.module.ModuleSysParam;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.app.AppConstants;

@Component
@ComponentScan
class AppEntityPostListener implements EntityPostListener, BeanConfigure<EntityPostListener> {
	
	private final AuditHandler auditHandler;
	private final TaskHandler taskHandler;
	private final ApiHandler apiHandler;
	private final SysParamHandler sysParamHandler;
	
	private Map<Class<?>, EntityPostListener> listeners = new HashMap<>();
	
	@Autowired
	AppEntityPostListener(
		AuditHandler auditHandler,
		@Qualifier(AppConstants.Bean.Task.AUDIT)
		TaskHandler taskHandler,
		ApiHandler apiHandler,
		SysParamHandler sysParamHandler
	) {
		this.auditHandler = auditHandler;
		this.taskHandler = taskHandler;
		this.apiHandler = apiHandler;
		this.sysParamHandler = sysParamHandler;
	}
	
	@Override
	public Callable<EntityPostListener> onConfigureBean(ApplicationContext applicationContext) {
		AppEntityPostListener self = this;
		return () -> {
			listeners.clear();
			
			// SysParam
			listeners.putAll(ModuleSysParam.getEntityPostListeners(sysParamHandler));
			
			// Api
			listeners.putAll(ModuleApi.getEntityPostListeners(apiHandler));
			
			return self;
		};
	}

	@Override
	public void onPostInsert(Object entity) {
		auditHandler.save("INSERT", entity);
		taskHandler.execute(() -> {
			EntityPostListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPostInsert(entity));
		});
	}

	@Override
	public void onPostUpdate(Object entity) {
		auditHandler.save("UPDATE", entity);
		taskHandler.execute(() -> {
			EntityPostListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPostUpdate(entity));
		});
	}
	
	@Override
	public void onPostDelete(Object entity) {
		auditHandler.save("DELETE", entity);
		taskHandler.execute(() -> {
			EntityPostListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPostDelete(entity));
		});
	}
	
}
