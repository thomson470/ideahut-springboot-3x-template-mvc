package net.ideahut.springboot.template.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.grid.GridAdditional;
import net.ideahut.springboot.grid.GridOption;
import net.ideahut.springboot.module.FrameworkModules;
import net.ideahut.springboot.object.Option;
import net.ideahut.springboot.support.GridOptionFromCollector;

public final class GridSupport {
	
	private GridSupport() {}
	
	
	/*
	 * OPTIONS
	 */
	public static Map<String, GridOption> getOptions() {
		Map<String, GridOption> options = new HashMap<>();
		options.putAll(FrameworkModules.getGridOptions());
		options.put("GENDER", StaticOption.GENDER);
		options.put("LANGUAGE", StaticOption.LANGUAGE);
		options.put("MESSAGE_TYPE", StaticOption.MESSAGE_TYPE);
		options.put("MENU_TYPE", StaticOption.MENU_TYPE);
		return options;
	}
	public static class StaticOption {
		private StaticOption() {}
		
		// GENDER
		public static final GridOption GENDER =  new GridOptionFromCollector(() ->
			Arrays.asList(
				Option.of("M", "Male"),
				Option.of("F", "Female")
			)
		);
		
		// LANGUAGE
		public static final GridOption LANGUAGE = new GridOptionFromCollector(() ->
			Arrays.asList(
				Option.of("id", "Bahasa"),
				Option.of("en", "English")
			)
		);
		
		// MESSAGE TYPE
		public static final GridOption MESSAGE_TYPE = new GridOptionFromCollector(() ->
			Arrays.asList(
				Option.of("E", "Error"),
				Option.of("G", "Grid"),
				Option.of("L", "Label"),
				Option.of("M", "Message"),
				Option.of("N", "Notification"),
				Option.of("Q", "Question"),
				Option.of("R", "Menu"),
				Option.of("W", "Web")
			)
		);
		
		// MENU TYPE
		public static final GridOption MENU_TYPE = new GridOptionFromCollector(() ->
			Arrays.asList(
				Option.of("mobile", "Mobile"),
				Option.of("portal", "Portal")
			)
		);
		
	}
	
	/*
	 * ADDITIONALS
	 */
	public static Map<String, GridAdditional> getAdditionals() {
		Map<String, GridAdditional> additionals = new HashMap<>();
		additionals.putAll(FrameworkModules.getGridAdditionals());
		return additionals;
	}

}
