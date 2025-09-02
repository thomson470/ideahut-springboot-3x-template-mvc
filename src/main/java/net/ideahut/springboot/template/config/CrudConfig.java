package net.ideahut.springboot.template.config;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiService;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudHandlerImpl;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.crud.CrudProperties;
import net.ideahut.springboot.crud.CrudResource;
import net.ideahut.springboot.definition.CrudDefinition;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.helper.ErrorHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.support.CrudSupport;

@Configuration
class CrudConfig {
	
	/*
	 * CRUD HANDLER
	 */
	@Bean
	CrudHandler crudHandler(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		DataMapper dataMapper,
		CrudResource resource,
		CrudPermission permission
	) {
		CrudDefinition crud = ObjectHelper.useOrDefault(
			appProperties.getCrud(), 
			CrudDefinition::new
		);
		return new CrudHandlerImpl()
				
		// semua query menggunakan sql (native) atau tidak
		.setAlwaysUseNative(crud.getAlwaysUseNative())
		
		// default maksimum jumlah data saat retrieve (PAGE, LIST, MAP)
		.setDefaultMaxLimit(crud.getDefaultMaxLimit())
		
		// EntityTrxManager
		.setEntityTrxManager(entityTrxManager)
		
		// CrudPermission
		.setPermission(permission)
		
		// CrudResource
		.setResource(resource)
		
		// Daftar filter specific yang akan disertakan saat query
		.setSpecificValueGetters(CrudSupport.getSpecificValueGetters());
		
	}
	
	/*
	 * CRUD RESOURCE
	 */
	@Bean
	CrudResource crudResource(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		ApiService apiService
	) {
		CrudDefinition crud = ObjectHelper.useOrDefault(
			appProperties.getCrud(), 
			CrudDefinition::new
		);
		if (Boolean.TRUE.equals(crud.getEnableApiService())) {
			 // CrudResource  diambil menggunakan ApiService (PRODUCTION)
			 // - Parameter manager yang didefinisikan di CrudRequest tidak akan digunakan, karena sudah ada di table
			 // - Parameter name = crudCode
			return (manager, name) -> {
				ApiAccess apiAccess = ApiAccess.fromContext();
				CrudProperties properties = apiService.getApiCrudProperties(apiAccess, name);
				Assert.notNull(properties, "CrudProperties is not found: " + name);
				return properties;
			};
		} else {
			// CrudResource berdasarkan nama class yang didefinisikan di CrudRequest (DEVELOPMENT)
			return (manager, name) -> {
				try {
					Class<?> clazz = ObjectHelper.classOf(Application.Package.APPLICATION + ".entity." + name);
					TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
					if (manager != null && !manager.isEmpty()) {
						trxManagerInfo = entityTrxManager.getTrxManagerInfo(manager);
					}
					EntityInfo entityInfo = trxManagerInfo.getEntityInfo(clazz);
					CrudProperties properties = new CrudProperties();
					properties.setEntityInfo(entityInfo);
					properties.setMaxLimit(crud.getDefaultMaxLimit());
					properties.setUseNative(crud.getAlwaysUseNative());
					return properties;
				} catch (Exception e) {
					throw ErrorHelper.exception(e);
				}
			};
		}
		
	}
	
	/*
	 * CRUD PRERMISSION
	 */
	@Bean
	CrudPermission crudPermission(
		AppProperties appProperties	
	) {
		CrudDefinition crud = ObjectHelper.useOrDefault(
			appProperties.getCrud(), 
			CrudDefinition::new
		);
		if (!Boolean.FALSE.equals(crud.getEnablePermission())) {
			// Cek berdasarkan action (CREATE, UPDATE, DELETE, dll)
			return (action, request) -> {
				CrudProperties properties = request.getProperties();
				Set<CrudAction> actions = properties.getActions();
				return actions != null && actions.contains(action);
			};
		} else {
			// Semua request diijinkan
			return (action, request) -> true;
		}
		
	}
	
}
