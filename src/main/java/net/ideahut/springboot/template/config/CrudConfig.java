package net.ideahut.springboot.template.config;

import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudHandlerImpl;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.crud.CrudProperties;
import net.ideahut.springboot.crud.CrudResource;
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
		ApplicationContext applicationContext,
		EntityTrxManager entityTrxManager,
		DataMapper dataMapper,
		CrudResource resource,
		CrudPermission permission
	) {
		return new CrudHandlerImpl()
		//.setAlwaysUseNative(null) // semua query menggunakan sql atau hql
		.setApplicationContext(applicationContext)
		//.setDefaultMaxLimit(null) // maksimum jumlah data saat retrieve (PAGE, LIST, MAP)
		.setEntityTrxManager(entityTrxManager)
		.setPermission(permission)
		.setResource(resource)
		.setSpecifics(CrudSupport.getSpecifics()); // Daftar filter specific yang akan disertakan saat query
	}
	
	
	/*
	 * CRUD RESOURCE
	 */
	@Bean
	CrudResource crudResource(
		AppProperties appProperties,
		EntityTrxManager entityTrxManager,
		WebMvcApiService apiService
	) {
		AppProperties.Crud crud = ObjectHelper.useOrDefault(
			appProperties.getCrud(), 
			AppProperties.Crud::new
		);
		if (Boolean.TRUE.equals(crud.getEnableApiService())) {
			 // CrudResource  diambil menggunakan ApiService (PRODUCTION)
			 // - Parameter manager yang didefinisikan di CrudRequest tidak akan digunakan, karena sudah ada di table
			 // - Parameter name = crudCode
			return (manager, name) -> {
				ApiAccess apiAccess = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
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
					properties.setMaxLimit(200);
					properties.setUseNative(false);
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
		AppProperties.Crud crud = ObjectHelper.useOrDefault(
			appProperties.getCrud(), 
			AppProperties.Crud::new
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
