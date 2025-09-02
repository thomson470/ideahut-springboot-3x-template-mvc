package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.crud.CrudSpecificValueGetter;
import net.ideahut.springboot.module.FrameworkModules;

public class CrudSupport {

private CrudSupport() {}
	
	/*
	 * SPECIFIC VALUE GETTERS
	 */
	public static Map<String, CrudSpecificValueGetter> getSpecificValueGetters() {
		Map<String, CrudSpecificValueGetter> specifics = new HashMap<>();
		specifics.putAll(FrameworkModules.getCrudSpesificValueGetters());
		return specifics;
	}
	
}
