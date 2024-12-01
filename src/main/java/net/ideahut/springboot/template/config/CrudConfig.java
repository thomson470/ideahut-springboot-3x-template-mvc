package net.ideahut.springboot.template.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
import net.ideahut.springboot.template.support.CrudSupport;

@Configuration
class CrudConfig {
	
	@Bean
	CrudHandler crudHandler(
		ApplicationContext applicationContext,
		EntityTrxManager entityTrxManager,
		DataMapper dataMapper,
		CrudResource resource,
		CrudPermission permission
	) {
		return new CrudHandlerImpl()
		.setApplicationContext(applicationContext)
		.setEntityTrxManager(entityTrxManager)
		.setResource(resource)
		.setPermission(permission)
		.setSpecifics(CrudSupport.getSpecifics());
	}
	
	
	
	/*
	 * Contoh CrudResource berdasarkan nama class yang didefinisikan di CrudRequest
	 */
	@Bean
	CrudResource crudResource(
		EntityTrxManager entityTrxManager
	) {
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
	
	/*
	 * Contoh CrudResource yang diambil menggunakan ApiService
	 * - Parameter manager yang didefinisikan di CrudRequest tidak akan digunakan, karena sudah ada di table
	 * - Parameter name = crudCode
	 */
	/*
	@Bean
	CrudResource crudResource(
		WebMvcApiService apiService
	) {//-
		return (manager, name) -> {//-
			ApiAccess apiAccess = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);//-
			CrudProperties properties = apiService.getApiCrudProperties(apiAccess, name);//-
			Assert.notNull(properties, "CrudProperties is not found: " + name);//-
			return properties;//-
		};//-
	}//-
	*/
	
	
	/*
	 * Contoh CrudPermission mengijinkan semua CrudRequest
	 */
	@Bean
	CrudPermission crudPermission() {
		return (action, request) -> true;
	}
	
	
	/*
	 * Contoh CrudPermission yang mengecek apakah action didefinisikan di table
	 */
	/*
	@Bean
	CrudPermission crudPermission() {//-
		return (action, request) -> {//-
			CrudProperties properties = request.getProperties();//-
			Set<CrudAction> actions = properties.getActions();//-
			return actions != null && actions.contains(action);//-
		};//-
	}//-
	*/
	
}
