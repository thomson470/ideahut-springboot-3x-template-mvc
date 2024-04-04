package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.AuditRequest;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.object.Page;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.EntityFill;
import net.ideahut.springboot.util.FrameworkUtil;
import net.ideahut.springboot.util.RequestUtil;

/*
 * API untuk melihat data audit
 */
@ComponentScan
@RestController
@RequestMapping("/audit")
class AuditController {
	
	@Autowired
	private EntityTrxManager entityTrxManager;
	@Autowired
	private AuditHandler auditHandler;
	
	
	@PostMapping(value = "/list")
	protected Result list() throws Exception {
		byte[] data = RequestUtil.getBodyAsBytes();
		AuditRequest auditRequest = auditHandler.getRequest(data);
		TrxManagerInfo trxManagerInfo;
		String manager = auditRequest.getManager();
		if (manager != null && !manager.isEmpty()) {
			trxManagerInfo = entityTrxManager.getTrxManagerInfo(manager);
		} else {
			trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		}
		if (trxManagerInfo == null) {
			throw new ResultRuntimeException(Result.error("AUDIT-01", "Unknown manager: " + manager));
		}
		String entity = auditRequest.getEntity();
		if (entity != null && !entity.isEmpty() && auditRequest.getClassOfEntity() == null) {
			try {
				Class<?> classOfEntity = FrameworkUtil.classOf(entity);
				auditRequest.setClassOfEntity(classOfEntity);
			} catch(Exception e1) {
				try {
					Class<?> type = FrameworkUtil.classOf(EntityFill.class.getPackage().getName() + "." + entity);
					auditRequest.setClassOfEntity(type);	
				} catch (Exception e2) {
					throw new ResultRuntimeException(Result.error("AUDIT-02", "Entity is not found, for: " + entity));
				}
			}
		}			
		Page page = auditHandler.getList(auditRequest);
		return Result.success(page);
	}
	
	
	@GetMapping(value = "/bytes")
	protected Result bytes(
		@RequestParam(name = "manager", required = false) String manager,
		@RequestParam(name = "id") String id
	) {
		byte[] bytes = auditHandler.getBytes(manager, id);
		return Result.success(bytes);
	}
	
	
}
