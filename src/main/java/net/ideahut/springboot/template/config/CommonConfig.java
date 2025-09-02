package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.ideahut.springboot.entity.EntityApiExcludeParam;
import net.ideahut.springboot.entity.EntityAuditParam;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.EntityTrxManagerImpl;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.mapper.DataMapperImpl;
import net.ideahut.springboot.message.entity.Language;
import net.ideahut.springboot.message.entity.Message;
import net.ideahut.springboot.module.ModuleApi;
import net.ideahut.springboot.module.ModuleJob;
import net.ideahut.springboot.serializer.BinarySerializer;
import net.ideahut.springboot.serializer.ForyBinarySerializer;
import net.ideahut.springboot.sysparam.entity.SysParam;
import net.ideahut.springboot.template.properties.AppProperties;

@Configuration
class CommonConfig {

	/*
	 * DATA MAPPER
	 */
	@Bean
	DataMapper dataMapper() {
		DataMapper dataMapper = new DataMapperImpl();
		FrameworkHelper.setDefaultDataMapper(dataMapper);
		return dataMapper;
	}
	
	
	/*
	 * BINARY SERIALIZER
	 * 
	 * Daftar binary serializer yang disupport:
	 * - DataMapperBinarySerializer (Jackson) -> http://fasterxml.com/
	 * - HessianBinarySerializer -> http://hessian.caucho.com/
	 * - JdkBinarySerializer -> https://docs.oracle.com/javase/7/docs/api/java/io/ObjectOutputStream.html
	 * - KryoBinarySerializer -> https://github.com/EsotericSoftware/kryo
	 * - ForyBinarySerializer -> https://fory.apache.org/
	 * 
	 */
	@Bean
	BinarySerializer binarySerializer() {
		//BinarySerializer binarySerializer = new DataMapperBinarySerializer().setFormat(DataMapper.JSON);//-
		//BinarySerializer binarySerializer = new DataMapperBinarySerializer().setFormat(DataMapper.XML);//-
		//BinarySerializer binarySerializer = new HessianBinarySerializer().setVersion(1);//-
		//BinarySerializer binarySerializer = new HessianBinarySerializer().setVersion(2);//-
		//BinarySerializer binarySerializer = new JdkBinarySerializer();//-
		//BinarySerializer binarySerializer = new KryoBinarySerializer();//-
		BinarySerializer binarySerializer = new ForyBinarySerializer();
		FrameworkHelper.setDefaultBinarySerializer(binarySerializer);
		return binarySerializer;
	}
	
	
	/*
	 * ENTITY TRX MANAGER
	 */
	@Bean
	EntityTrxManager entityTrxManager(
		AppProperties appProperties
	) {
		return new EntityTrxManagerImpl()
		
		// Entity / Model yang tidak memiliki anotasi @ApiExclude, dan tidak ingin dipublikasikan oleh ApiService
		.setApiExcludeParams(
			new EntityApiExcludeParam()
			.addEntityClasses(ModuleApi.getApiExcludeEntities())
			.addEntityClasses(ModuleJob.getApiExcludeEntities())
			.addEntityClasses(
				SysParam.class,
				Language.class,
				Message.class
			)
		)
		
		// Entity / Model yang tidak memiliki anotasi @Audit, dan ingin setiap perubahannya disimpan
		.setAuditParams(
			new EntityAuditParam()
			.addEntityClasses(ModuleApi.getAuditEntities())
			.addEntityClasses(ModuleJob.getAuditEntities())
			.addEntityClasses(
				SysParam.class,
				Language.class,
				Message.class
			)
		)
		
		// Daftar EntityPreListener & EntityPostListener, default autodetect = true
		.setEntityListenerParam(null)
		
		// Parameter untuk menghandle anotasi @ForeignKeyEntity
		// Ini solusi jika terjadi error saat membuat native image dimana entity memiliki @ManyToOne & @OneToMany
		// tapi package-nya berbeda dengan package project (error ByteCodeProvider saat runtime)
		.setForeignKeyParam(appProperties.getForeignKey());
	}
	
}
