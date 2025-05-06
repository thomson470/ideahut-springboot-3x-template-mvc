package net.ideahut.springboot.template.support;

import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;

import net.ideahut.springboot.api.processor.AgentAuthApiProcessor;
import net.ideahut.springboot.api.processor.AgentHeaderApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostAuthApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostHeaderApiProcessor;
import net.ideahut.springboot.api.processor.AgentHostJwtApiProcessor;
import net.ideahut.springboot.api.processor.AgentJwtApiProcessor;
import net.ideahut.springboot.api.processor.HostAuthApiProcessor;
import net.ideahut.springboot.api.processor.HostHeaderApiProcessor;
import net.ideahut.springboot.api.processor.HostJwtApiProcessor;
import net.ideahut.springboot.api.processor.StandardAuthApiProcessor;
import net.ideahut.springboot.api.processor.StandardHeaderApiProcessor;
import net.ideahut.springboot.api.processor.StandardJwtApiProcessor;
import net.ideahut.springboot.crud.Condition;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.grid.GridAdditional;
import net.ideahut.springboot.grid.GridOption;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.object.Option;
import net.ideahut.springboot.support.GridAdditionalFromCollector;
import net.ideahut.springboot.support.GridOptionFromCollector;

public final class GridSupport {
	
	private GridSupport() {}
	
	/*
	 * OPTIONS
	 */
	public static Map<String, GridOption> getOptions() {
		Map<String, GridOption> options = new HashMap<>();
		options.put("GENDER", StaticOption.GENDER);
		options.put("LANGUAGE", StaticOption.LANGUAGE);
		options.put("MESSAGE_TYPE", StaticOption.MESSAGE_TYPE);
		options.put("YES_NO", StaticOption.YES_NO);
		options.put("MENU_TYPE", StaticOption.MENU_TYPE);
		options.put("DIGEST", StaticOption.DIGEST);
		options.put("API_TYPE", StaticOption.API_TYPE);
		options.put("CRUD_ACTION", StaticOption.CRUD_ACTION);
		options.put("CRUD_CONDITION", StaticOption.CRUD_CONDITION);
		return options;
	}
	public static class StaticOption {
		private StaticOption() {}
		
		// YES / NO
		public static final GridOption YES_NO = new GridOptionFromCollector(() ->
			Arrays.asList(
				new Option("Y", "Yes"),
				new Option("N", "No")
			)
		);
		
		// GENDER
		public static final GridOption GENDER =  new GridOptionFromCollector(() ->
			Arrays.asList(
				new Option("M", "Male"),
				new Option("F", "Female")
			)
		);
		
		// LANGUAGE
		public static final GridOption LANGUAGE = new GridOptionFromCollector(() ->
			Arrays.asList(
				new Option("id", "Bahasa"),
				new Option("en", "English")
			)
		);
		
		// MESSAGE TYPE
		public static final GridOption MESSAGE_TYPE = new GridOptionFromCollector(() ->
			Arrays.asList(
				new Option("E", "Error"),
				new Option("G", "Grid"),
				new Option("L", "Label"),
				new Option("M", "Message"),
				new Option("N", "Notification"),
				new Option("Q", "Question"),
				new Option("R", "Menu"),
				new Option("W", "Web")
			)
		);
		
		// CRUD ACTION
		public static final GridOption CRUD_ACTION = new GridOptionFromCollector(() -> {
			List<Option> options = new ArrayList<>();
			for (CrudAction action : CrudAction.values()) {
				options.add(new Option(action.name(), action.name()));
			}
			Collections.sort(options, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
			return options;
		});
		
		// CRUD CONDITION
		public static final GridOption CRUD_CONDITION = new GridOptionFromCollector(() -> {
			List<Option> options = new ArrayList<>();
			for (Condition condition : Condition.values()) {
				options.add(new Option(condition.name(), condition.name()));
			}
			Collections.sort(options, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
			return options;
		});
		
		// MENU TYPE
		public static final GridOption MENU_TYPE = new GridOptionFromCollector(() ->
			Arrays.asList(
				new Option("mobile", "Mobile"),
				new Option("portal", "Portal")
			)
		);
		
		// DIGEST
		public static final GridOption DIGEST = new GridOptionFromCollector(() -> {
			Set<String> digests = Security.getAlgorithms("MessageDigest");
			List<Option> options = new ArrayList<>();
			for (String digest : digests) {
				options.add(new Option(digest, digest));
			}
			Collections.sort(options, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
			return options;
		});
		
		// API TYPE
		public static final GridOption API_TYPE = new GridOptionFromCollector(() -> {
			List<Option> options = new ArrayList<>();
			options.add(Option.of(StandardAuthApiProcessor.API_TYPE, StandardAuthApiProcessor.API_TYPE + " - StandardAuth"));
			options.add(Option.of(StandardHeaderApiProcessor.API_TYPE, StandardHeaderApiProcessor.API_TYPE + " - StandardHeader"));
			options.add(Option.of(StandardJwtApiProcessor.API_TYPE, StandardJwtApiProcessor.API_TYPE + " - StandardJWT"));
			
			options.add(Option.of(AgentAuthApiProcessor.API_TYPE, AgentAuthApiProcessor.API_TYPE + " - AgentAuth"));
			options.add(Option.of(AgentHeaderApiProcessor.API_TYPE, AgentHeaderApiProcessor.API_TYPE + " - AgentHeader"));
			options.add(Option.of(AgentJwtApiProcessor.API_TYPE, AgentJwtApiProcessor.API_TYPE + " - AgentJWT"));
			
			options.add(Option.of(HostHeaderApiProcessor.API_TYPE, HostHeaderApiProcessor.API_TYPE + " - HostHeader"));
			options.add(Option.of(HostAuthApiProcessor.API_TYPE, HostAuthApiProcessor.API_TYPE + " - HostAuth"));
			options.add(Option.of(HostJwtApiProcessor.API_TYPE, HostJwtApiProcessor.API_TYPE + " - HostJWT"));
			
			options.add(Option.of(AgentHostHeaderApiProcessor.API_TYPE, AgentHostHeaderApiProcessor.API_TYPE + " - AgentHostHeader"));
			options.add(Option.of(AgentHostAuthApiProcessor.API_TYPE, AgentHostAuthApiProcessor.API_TYPE + " - AgentHostAuth"));
			options.add(Option.of(AgentHostJwtApiProcessor.API_TYPE, AgentHostJwtApiProcessor.API_TYPE + " - AgentHostJWT"));
			
			Collections.sort(options, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
			return options;
		});
		
	}
	
	/*
	 * ADDITIONALS
	 */
	public static Map<String, GridAdditional> getAdditionals() {
		Map<String, GridAdditional> additionals = new HashMap<>();
		additionals.put("DAYS", StaticAdditional.DAYS);
		additionals.put("MONTHS", StaticAdditional.MONTHS);
		return additionals;
	}
	public static class StaticAdditional {
		private StaticAdditional() {}
		
		// MONTHS
		public static final GridAdditional MONTHS = new GridAdditionalFromCollector(() -> {
			String str = "[\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\", \"Jan\", \"Feb\", \"Mar\", \"Apr\", \"May\", \"Jun\", \"Jul\", \"Aug\", \"Sep\", \"Oct\", \"Nov\", \"Dec\"]";
			return FrameworkHelper.defaultDataMapper().read(str, ArrayNode.class);
		});
		
		// DAYS
		public static final GridAdditional DAYS = new GridAdditionalFromCollector(() -> {
			String str = "[\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\", \"Sun\", \"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\"]";
			return FrameworkHelper.defaultDataMapper().read(str, ArrayNode.class);
		});
		
	}

}
