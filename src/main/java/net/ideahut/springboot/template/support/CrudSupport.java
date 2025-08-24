package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.api.ApiAccess;
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
		specifics.put("USER_NAME", StaticSpecific.USER_NAME);
		specifics.put("API_ROLE", StaticSpecific.API_ROLE);
		return specifics;
	}
	
	private static class StaticSpecific {
		private StaticSpecific() {}
		
		// APP_ID
		private static final CrudSpecific APP_ID = context ->
			ApiAccess.fromContext().getAttribute(String.class, ApiAccess.Attribute.APP_ID);
		
		// APP_ROLE
		private static final CrudSpecific APP_ROLE = context ->
			ApiAccess.fromContext().getAttribute(String.class, ApiAccess.Attribute.APP_ROLE);
		
		// USER ID
		private static final CrudSpecific USER_ID = context -> {
			ApiAccess access = ApiAccess.fromContext();
			return access.getApiUser() != null ? access.getApiUser().getId() : null;
		};
		
		// USER NAME
		private static final CrudSpecific USER_NAME = context -> {
			ApiAccess access = ApiAccess.fromContext();
			return access.getApiUser() != null ? access.getApiUser().getUsername() : null;
		};
		
		// API_ROLE
		private static final CrudSpecific API_ROLE = context -> ApiAccess.fromContext().getApiRole();
		
	}
	
}
