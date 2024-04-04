package net.ideahut.springboot.template.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.audit.AuditHandler;

@Component
@ComponentScan
public class EntityPostListener implements net.ideahut.springboot.entity.EntityPostListener {
	
	@Autowired
	private AuditHandler auditHandler;

	@Override
	public void onPostDelete(Object entity) {
		updateCache(entity, true);
		onAudit(entity, "DELETE");
	}

	@Override
	public void onPostInsert(Object entity) {
		updateCache(entity, false);
		insertData(entity);
		onAudit(entity, "INSERT");
	}

	@Override
	public void onPostUpdate(Object entity) {
		updateCache(entity, false);
		onAudit(entity, "UPDATE");
	}
	
	
	private void updateCache(Object entity, boolean isDelete) {
		
	}
	
	private void insertData(Object entity) {
			
	}
	
	// AUDIT
	private void onAudit(Object entity, String action) {
		Audit audit = entity.getClass().getAnnotation(Audit.class);
		if (audit != null && audit.value()) {
			auditHandler.save(action, entity);
		}
	}
	
}
