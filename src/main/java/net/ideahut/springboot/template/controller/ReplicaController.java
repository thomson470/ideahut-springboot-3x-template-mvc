package net.ideahut.springboot.template.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityReplica;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.app.Information;
import net.ideahut.springboot.template.entity.app.InformationLink;

@Public
@ComponentScan
@RestController
@RequestMapping("/replica")
class ReplicaController {
	
	private final EntityTrxManager entityTrxManager;
	
	@Autowired
	ReplicaController(
		EntityTrxManager entityTrxManager	
	) {
		this.entityTrxManager = entityTrxManager;
	}

	@PostMapping(value = "/create/information")
	Result createInformationReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<EntityReplica.Creation> creations = EntityReplica.create(entityInfo, 2);
		return Result.success(creations);
	}
	
	@GetMapping(value = "/sql/information")
	Result sqlInformationReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<String> sqls = EntityReplica.getSQL(entityInfo, 2);
		return Result.success(sqls);
	}
	
	@PostMapping(value = "/create/information/link")
	Result createInformationLinkReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(InformationLink.class);
		EntityInfo refEntityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<EntityReplica.Creation> creations = EntityReplica.create(entityInfo, 2, refEntityInfo);
		return Result.success(creations);
	}
	
	@GetMapping(value = "/sql/information/link")
	Result sqlInformationLinkReplica() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(InformationLink.class);
		EntityInfo refEntityInfo = trxManagerInfo.getEntityInfo(Information.class);
		List<String> sqls = EntityReplica.getSQL(entityInfo, 2, refEntityInfo);
		return Result.success(sqls);
	}
	
}
