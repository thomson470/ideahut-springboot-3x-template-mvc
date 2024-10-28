package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.ideahut.springboot.api.ApiConfigHelper;
import net.ideahut.springboot.entity.EntityApiExcludeParam;
import net.ideahut.springboot.entity.EntityAuditParam;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.EntityTrxManagerImpl;
import net.ideahut.springboot.job.JobConfigHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.mapper.DataMapperImpl;
import net.ideahut.springboot.message.entity.Language;
import net.ideahut.springboot.message.entity.Message;
import net.ideahut.springboot.sysparam.entity.SysParam;
import net.ideahut.springboot.template.properties.AppProperties;

@Configuration
class CommonConfig {

	@Bean
	DataMapper dataMapper() {
		return new DataMapperImpl();
	}
	
	@Bean
	EntityTrxManager entityTrxManager(
		AppProperties appProperties
	) {
		return new EntityTrxManagerImpl()
		.setForeignKeyParam(appProperties.getForeignKey())
		.setApiExcludeParams(
			new EntityApiExcludeParam()
			.addEntityClasses(ApiConfigHelper.getApiExcludeEntities())
			.addEntityClasses(JobConfigHelper.getApiExcludeEntities())
			.addEntityClasses(
				SysParam.class,
				Language.class,
				Message.class
			)
		)
		.setAuditParams(
			new EntityAuditParam()
			.addEntityClasses(ApiConfigHelper.getAuditEntities())
			.addEntityClasses(JobConfigHelper.getAuditEntities())
			.addEntityClasses(
				SysParam.class,
				Language.class,
				Message.class
			)
		);
	}
	
}
