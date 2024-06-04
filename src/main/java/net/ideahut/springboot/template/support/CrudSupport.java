package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.crud.CrudSpecific;

public class CrudSupport {

	private CrudSupport() {}
	
	/*
	 * SPECIFICS
	 */
	public static Map<String, CrudSpecific> getSpecifics() {
		Map<String, CrudSpecific> specifics = new HashMap<>();
		specifics.put("ROLE_CODE", StaticSpecific.ROLE_CODE);
		specifics.put("USER_ID", StaticSpecific.USER_ID);
		return specifics;
	}
	
	private static class StaticSpecific {
		private StaticSpecific() {}
		
		// ROLE CODE
		private static final CrudSpecific ROLE_CODE = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getRole();
			}
			return null;
		};
		
		// USER ID
		private static final CrudSpecific USER_ID = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getUser() != null ? access.getUser().getId() : null;
			}
			return null;
		};
	}
	
}
