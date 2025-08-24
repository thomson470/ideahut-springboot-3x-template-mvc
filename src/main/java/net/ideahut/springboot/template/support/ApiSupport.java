package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.api.ApiCache;
import net.ideahut.springboot.api.ApiEntityClass;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.dto.ApiConnectDto;
import net.ideahut.springboot.api.dto.ApiConnectRoleDto;
import net.ideahut.springboot.api.dto.ApiProviderConfigDto;
import net.ideahut.springboot.api.dto.ApiProviderCrudActionDto;
import net.ideahut.springboot.api.dto.ApiProviderCrudDto;
import net.ideahut.springboot.api.dto.ApiProviderCrudFilterDto;
import net.ideahut.springboot.api.dto.ApiProviderHostDto;
import net.ideahut.springboot.api.dto.ApiProviderRequestDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudActionDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudDto;
import net.ideahut.springboot.api.dto.ApiRoleCrudFilterDto;
import net.ideahut.springboot.api.dto.ApiRoleRequestDto;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.helper.ErrorHelper;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.support.EntityPostListenerForAllAction;

public class ApiSupport {
	
	private ApiSupport() {}
	
	public static Map<Class<?>, EntityPostListener> getEntityPostListeners(
		DataMapper dataMapper,
		ApiHandler apiHandler,
		EntityTrxManager entityTrxManager
	) {
		Map<Class<?>, EntityPostListener> listeners = new HashMap<>();
		
		ApiCache apiCache = ObjectHelper.callOrElse(
			ObjectHelper.isInstance(ApiCache.class, apiHandler), 
			() -> (ApiCache) apiHandler, 
			() -> ApiCache.EMPTY
		);
		ApiEntityClass classOfEntity = apiHandler.getApiEntityClass();
		TrxManagerInfo trxManagerInfo = FrameworkHelper.getTrxManagerInfo(entityTrxManager, classOfEntity.getTrxManagerName());
		ErrorHelper.throwIf(trxManagerInfo == null, "TrxManager not found, name: {}", classOfEntity.getTrxManagerName());
		
		//ApiConnect
		listeners.put(classOfEntity.getApiConnect(), new EntityPostListenerForAllAction(entity -> {
			ApiConnectDto dto = dataMapper.copy(entity, ApiConnectDto.class);
			apiCache.removeApiConnect(dto.getApiConnectCode());
		}));
		
		//ApiConnectRole
		listeners.put(classOfEntity.getApiConnectRole(), new EntityPostListenerForAllAction(entity -> {
			ApiConnectRoleDto dto = dataMapper.copy(entity, ApiConnectRoleDto.class);
			apiCache.removeApiConnect(dto.getApiConnectCode());
		}));
		
		//ApiCrud
		listeners.put(classOfEntity.getApiCrud(), new EntityPostListenerForAllAction(entity -> apiCache.clearCache()));
		
		//ApiCrudExclude
		listeners.put(classOfEntity.getApiCrudExclude(), new EntityPostListenerForAllAction(entity -> apiCache.clearCache()));
		
		//ApiCrudField
		listeners.put(classOfEntity.getApiCrudField(), new EntityPostListenerForAllAction(entity -> apiCache.clearCache()));
		
		//ApiProvider
		listeners.put(classOfEntity.getApiProvider(), new EntityPostListenerForAllAction(entity -> apiCache.clearCache()));
		
		//ApiProviderConfig
		listeners.put(classOfEntity.getApiProviderConfig(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderConfigDto dto = dataMapper.copy(entity, ApiProviderConfigDto.class);
			apiCache.removeApiProvider(dto.getApiName());
		}));
		
		//ApiProviderCrud
		listeners.put(classOfEntity.getApiProviderCrud(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderCrudDto dto = dataMapper.copy(entity, ApiProviderCrudDto.class);
			apiCache.removeApiCrud(dto);
		}));
		
		//ApiProviderCrudAction
		listeners.put(classOfEntity.getApiProviderCrudAction(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderCrudActionDto dto = dataMapper.copy(entity, ApiProviderCrudActionDto.class);
			apiCache.removeApiCrud(new ApiProviderCrudDto().setApiName(dto.getApiName()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		//ApiProviderCrudFilter
		listeners.put(classOfEntity.getApiProviderCrudFilter(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderCrudFilterDto dto = dataMapper.copy(entity, ApiProviderCrudFilterDto.class);
			apiCache.removeApiCrud(new ApiProviderCrudDto().setApiName(dto.getApiName()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		//ApiProviderHost
		listeners.put(classOfEntity.getApiProviderHost(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderHostDto dto = dataMapper.copy(entity, ApiProviderHostDto.class);
			apiCache.removeApiProvider(dto.getApiName());
		}));
		
		//ApiProviderRequest
		listeners.put(classOfEntity.getApiProviderRequest(), new EntityPostListenerForAllAction(entity -> {
			ApiProviderRequestDto dto = dataMapper.copy(entity, ApiProviderRequestDto.class);
			apiCache.removeApiRequest(dto);
		}));
		
		//ApiRole
		listeners.put(classOfEntity.getApiRole(), new EntityPostListenerForAllAction(entity -> apiCache.clearCache()));
		
		//ApiRoleCrud
		listeners.put(classOfEntity.getApiRoleCrud(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudDto dto = dataMapper.copy(entity, ApiRoleCrudDto.class);
			apiCache.removeApiCrud(dto);
		}));
		
		//ApiRoleCrudAction
		listeners.put(classOfEntity.getApiRoleCrudAction(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudActionDto dto = dataMapper.copy(entity, ApiRoleCrudActionDto.class);
			apiCache.removeApiCrud(new ApiRoleCrudDto().setApiRoleCode(dto.getApiRoleCode()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		//ApiRoleCrudFilter
		listeners.put(classOfEntity.getApiRoleCrudFilter(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleCrudFilterDto dto = dataMapper.copy(entity, ApiRoleCrudFilterDto.class);
			apiCache.removeApiCrud(new ApiRoleCrudDto().setApiRoleCode(dto.getApiRoleCode()).setApiCrudCode(dto.getApiCrudCode()));
		}));
		
		//ApiRoleRequest
		listeners.put(classOfEntity.getApiRoleRequest(), new EntityPostListenerForAllAction(entity -> {
			ApiRoleRequestDto dto = dataMapper.copy(entity, ApiRoleRequestDto.class);
			apiCache.removeApiRequest(dto);
		}));
		
		return listeners;
	}
	
}
