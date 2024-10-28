package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import net.ideahut.springboot.api.ApiEntityClass;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiHandlerImpl;
import net.ideahut.springboot.api.dto.ApiProviderConfigDto;
import net.ideahut.springboot.api.dto.ApiProviderCrudActionDto;
import net.ideahut.springboot.api.dto.ApiProviderCrudDto;
import net.ideahut.springboot.api.dto.ApiProviderDto;
import net.ideahut.springboot.api.dto.ApiProviderHostDto;
import net.ideahut.springboot.api.dto.ApiProviderRequestDto;
import net.ideahut.springboot.api.dto.ApiProviderRoleDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudActionDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudFilterDto;
import net.ideahut.springboot.api.dto.ApiRoleRequestDto;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.support.EntityPostListenerForAllAction;
import net.ideahut.springboot.util.FrameworkUtil;

public class ApiSupport {
	
	private ApiSupport() {}
	
	public static Map<Class<?>, EntityPostListener> getEntityPostListeners(
		DataMapper dataMapper,
		ApiHandler apiHandler,
		EntityTrxManager entityTrxManager
	) {
		Map<Class<?>, EntityPostListener> listeners = new HashMap<>();
		
		ApiHandlerImpl apiHandlerImpl = (ApiHandlerImpl) apiHandler;
		ApiEntityClass classOfEntity = apiHandlerImpl.getEntityClass();
		TrxManagerInfo trxManagerInfo = FrameworkUtil.getTrxManagerInfo(entityTrxManager, classOfEntity.getTrxManagerName());
		Assert.notNull(trxManagerInfo, "TrxManager is not found, for: " + classOfEntity.getTrxManagerName());
		
		
		/**
		 * NOTE
		 * - Untuk perubahan ApiCrud & ApiCrudField, sebaiknya update redis dilakukan dari menu Reload di aplikasi admin
		 */
		// ApiCrud
		// ApiCrudField
		
		
		// ApiProvider
		listeners.put(classOfEntity.getApiProvider(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderDto dto = dataMapper.copy(entity, ApiProviderDto.class);
			apiHandlerImpl.removeApiProvider(dto.getApiName());
		}));
		
		// ApiProviderConfig
		listeners.put(classOfEntity.getApiProviderConfig(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderConfigDto dto = dataMapper.copy(entity, ApiProviderConfigDto.class);
			apiHandlerImpl.removeApiProvider(dto.getApiName());
		}));
		
		// ApiProviderCrud
		listeners.put(classOfEntity.getApiProviderCrud(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderCrudDto dto = dataMapper.copy(entity, ApiProviderCrudDto.class);
			apiHandlerImpl.removeApiCrud(dto);
		}));
		
		// ApiProviderCrudAction
		listeners.put(classOfEntity.getApiProviderCrudAction(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderCrudActionDto dto = dataMapper.copy(entity, ApiProviderCrudActionDto.class);
			apiHandlerImpl.removeApiCrud(ApiProviderCrudDto.create().setApiName(dto.getApiName()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		// ApiProviderHost
		listeners.put(classOfEntity.getApiProviderHost(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderHostDto dto = dataMapper.copy(entity, ApiProviderHostDto.class);
			apiHandlerImpl.removeApiProvider(dto.getApiName());
		}));
		
		// ApiProviderRequest
		listeners.put(classOfEntity.getApiProviderRequest(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderRequestDto dto = dataMapper.copy(entity, ApiProviderRequestDto.class);
			apiHandlerImpl.removeApiRequest(dto);
		}));
		
		// ApiProviderRole
		listeners.put(classOfEntity.getApiProviderRole(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderRoleDto dto = dataMapper.copy(entity, ApiProviderRoleDto.class);
			apiHandlerImpl.removeApiProvider(dto.getApiName());
		}));
		
		
		// ApiRole
		listeners.put(classOfEntity.getApiRole(), new EntityPostListenerForAllAction(entity -> {
			apiHandlerImpl.clearCache();
		}));
		
		// ApiRoleCrud
		listeners.put(classOfEntity.getApiRoleCrud(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudDto dto = dataMapper.copy(entity, ApiRoleCrudDto.class);
			apiHandlerImpl.removeApiCrud(dto);
		}));
		
		// ApiRoleCrudAction
		listeners.put(classOfEntity.getApiRoleCrudAction(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudActionDto dto = dataMapper.copy(entity, ApiRoleCrudActionDto.class);
			apiHandlerImpl.removeApiCrud(ApiRoleCrudDto.create().setApiRoleCode(dto.getApiRoleCode()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		// ApiRoleCrudFilter
		listeners.put(classOfEntity.getApiRoleCrudFilter(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudFilterDto dto = dataMapper.copy(entity, ApiRoleCrudFilterDto.class);
			ApiRoleCrudDto apiRoleCrud = dto.getApiRoleCrud();
			if (apiRoleCrud != null) {
				apiHandlerImpl.removeApiCrud(apiRoleCrud);
			}
		}));
		
		// ApiRoleRequest
		listeners.put(classOfEntity.getApiRoleRequest(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleRequestDto dto = dataMapper.copy(entity, ApiRoleRequestDto.class);
			apiHandlerImpl.removeApiRequest(dto);
		}));
		
		return listeners;
	}
	
}
