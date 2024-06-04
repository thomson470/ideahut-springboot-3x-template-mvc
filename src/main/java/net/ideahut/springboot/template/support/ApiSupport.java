package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.ApiRemover;
import net.ideahut.springboot.api.entity.EntCrudActionId;
import net.ideahut.springboot.api.entity.EntCrudRoleId;
import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.template.entity.api.ApiCrudAction;
import net.ideahut.springboot.template.entity.api.ApiCrudFilter;
import net.ideahut.springboot.template.entity.api.ApiCrudRole;
import net.ideahut.springboot.template.entity.api.ApiRequestRole;

public class ApiSupport {
	
	private ApiSupport() {}

	public static Map<Class<?>, EntityPostListener> getEntityPostListeners(ApiHandler apiHandler) {
		Map<Class<?>, EntityPostListener> listeners = new HashMap<>();
		
		// ApiRequestRole
		listeners.put(ApiRequestRole.class, new EntityPostListener() {
			@Override
			public void onPostDelete(Object entity) {
				if (apiHandler instanceof ApiRemover remover) {
					remover.removeRequest(((ApiRequestRole) entity).getId());
				}
			}
			@Override
			public void onPostUpdate(Object entity) {
				onPostDelete(entity);
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostDelete(entity);
			}
		});
		
		// ApiCrudRole
		listeners.put(ApiCrudRole.class, new EntityPostListener() {
			@Override
			public void onPostDelete(Object entity) {
				if (apiHandler instanceof ApiRemover remover) {
					remover.removeCrud(((ApiCrudRole) entity).getId());
				}
			}
			@Override
			public void onPostUpdate(Object entity) {
				onPostDelete(entity);
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostDelete(entity);
			}
		});
		
		// ApiCrudAction
		listeners.put(ApiCrudAction.class, new EntityPostListener() {
			@Override
			public void onPostDelete(Object entity) {
				if (apiHandler instanceof ApiRemover remover) {
					EntCrudActionId id = ((ApiCrudAction) entity).getId();
					remover.removeCrud(new EntCrudRoleId(id.getRoleCode(), id.getCrudCode()));
				}
			}
			@Override
			public void onPostUpdate(Object entity) {
				onPostDelete(entity);
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostDelete(entity);
			}
		});
		
		// ApiCrudFilter
		listeners.put(ApiCrudFilter.class, new EntityPostListener() {
			@Override
			public void onPostDelete(Object entity) {
				if (apiHandler instanceof ApiRemover remover) {
					ApiCrudRole permission = ((ApiCrudFilter) entity).getPermission();
					if (permission != null && permission.getId() != null) {
						remover.removeCrud(permission.getId());
					}
				}
			}
			@Override
			public void onPostUpdate(Object entity) {
				onPostDelete(entity);
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostDelete(entity);
			}
		});
		
		return listeners;
	}
	
}
