package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiUser;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.crud.CrudSpecific;

public class CrudSupport {

private CrudSupport() {}
	
	/*
	 * SPECIFICS
	 */
	public static Map<String, CrudSpecific> getSpecifics() {
		Map<String, CrudSpecific> specifics = new HashMap<>();
		specifics.put("APP_ID", StaticSpecific.APP_ID);
		specifics.put("APP_ROLE", StaticSpecific.APP_ROLE);
		specifics.put("USER_ID", StaticSpecific.USER_ID);
		specifics.put("USER_ROLE", StaticSpecific.USER_ROLE);
		return specifics;
	}
	
	private static class StaticSpecific {
		private StaticSpecific() {}
		
		// APP_ID
		private static final CrudSpecific APP_ID = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getAttribute(String.class, ApiAccess.Attribute.APP_ID);
			}
			return null;
		};
		
		// APP_ROLE
		private static final CrudSpecific APP_ROLE = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getAttribute(String.class, ApiAccess.Attribute.APP_ROLE);
			}
			return null;
		};
		
		// USER ROLE
		private static final CrudSpecific USER_ROLE = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getApiUser() != null ? access.getApiUser().getAttribute(String.class, ApiUser.Attribute.ROLE) : null;
			}
			return null;
		};
		
		// USER ID
		private static final CrudSpecific USER_ID = context -> {
			ApiAccess access = RequestContext.currentContext().getAttribute(ApiAccess.CONTEXT);
			if (access != null) {
				return access.getApiUser() != null ? access.getApiUser().getId() : null;
			}
			return null;
		};
	}
	
}
