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
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.support.CrudSupport;
import net.ideahut.springboot.util.FrameworkUtil;

@Configuration
class CrudConfig {
	
	@Bean
	protected CrudHandler crudHandler(
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
	protected CrudResource crudResource(
		EntityTrxManager entityTrxManager
	) {
		return (manager, name) -> {
			try {
				Class<?> clazz = FrameworkUtil.classOf(AppConstants.PACKAGE + ".entity." + name);
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
				throw FrameworkUtil.exception(e);
			}
		};
	}
	
	/*
	 * Contoh CrudResource yang diambil menggunakan ApiHandler
	 * - Parameter manager yang didefinisikan di CrudRequest tidak akan digunakan, karena sudah ada di table
	 * - Parameter name = crudCode
	 */
	/*
	@Bean
	protected CrudResource crudResource(
		ApiHandler apiHandler
	) {
		return (manager, name) -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			CrudProperties properties = apiHandler.getCrudProperties(access.getRole(), name);
			Assert.notNull(properties, "CrudProperties is not available (" + access.getRole() + "::" + name + ")");
			return properties;
		};
	}
	*/
	
	
	
	/*
	 * Contoh CrudPermission mengijinkan semua CrudRequest
	 */
	@Bean
	protected CrudPermission crudPermission() {
		return (action, request) -> true;
	}
	
	
	/*
	 * Contoh CrudPermission yang mengecek apakah action didefinisikan di table
	 */
	/*
	@Bean
	protected CrudPermission crudPermission() {
		return (action, request) -> {
			CrudProperties properties = request.getProperties();
			Set<CrudAction> actions = properties.getActions();
			return actions != null && actions.contains(action);
		};
	}
	*/
	
}
