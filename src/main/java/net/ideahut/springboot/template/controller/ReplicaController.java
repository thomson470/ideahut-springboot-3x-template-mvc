package net.ideahut.springboot.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityReplica;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.Information;
import net.ideahut.springboot.template.entity.InformationLink;

@ComponentScan
@RestController
@RequestMapping("/replica")
class ReplicaController {
	
	@Autowired
	private EntityTrxManager entityTrxManager;

	@PostMapping(value = "/create/information")
	protected Result createInformationReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<EntityReplica.Creation> creations = EntityReplica.create(entityInfo, 2);
		return Result.success(creations);
	}
	
	@GetMapping(value = "/sql/information")
	protected Result sqlInformationReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<String> sqls = EntityReplica.getSQL(entityInfo, 2);
		return Result.success(sqls);
	}
	
	@PostMapping(value = "/create/information/link")
	protected Result createInformationLinkReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(InformationLink.class);
		EntityInfo refEntityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<EntityReplica.Creation> creations = EntityReplica.create(entityInfo, 2, refEntityInfo);
		return Result.success(creations);
	}
	
	@GetMapping(value = "/sql/information/link")
	protected Result sqlInformationLinkReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(InformationLink.class);
		EntityInfo refEntityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<String> sqls = EntityReplica.getSQL(entityInfo, 2, refEntityInfo);
		return Result.success(sqls);
	}
	
}
