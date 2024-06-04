package net.ideahut.springboot.template.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;

import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.grid.GridAdditional;
import net.ideahut.springboot.grid.GridOption;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Option;

public final class GridSupport {
	
	private GridSupport() {}
	
	/*
	 * OPTIONS
	 */
	public static Map<String, GridOption> getOptions() {
		Map<String, GridOption> options = new HashMap<>();
		options.put("CRUD_ACTION", StaticOption.CRUD_ACTION);
		options.put("GENDER", StaticOption.GENDER);
		options.put("YES_NO", StaticOption.YES_NO);
		options.put("MENU_TYPE", StaticOption.MENU_TYPE);
		return options;
	}
	public static class StaticOption {
		private StaticOption() {}
		
		// YES / NO
		public static final GridOption YES_NO = context ->
			Arrays.asList(
				new Option("Y", "Yes"),
				new Option("N", "No")
			);
		
		// GENDER
		public static final GridOption GENDER = context ->
			Arrays.asList(
				new Option("M", "Male"),
				new Option("F", "Female")
			);
		
		// CRUD ACTION
		public static final GridOption CRUD_ACTION = context -> {
			List<Option> options = new ArrayList<>();
			for (CrudAction action : CrudAction.values()) {
				options.add(new Option(action.name(), action.name()));
			}
			Collections.sort(options, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
			return options;
		};
		
		// MENU TYPE
		public static final GridOption MENU_TYPE = context ->
			Arrays.asList(
				new Option("mobile", "Mobile"),
				new Option("portal", "Portal")
			);
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
		public static final GridAdditional MONTHS = context -> {
			String str = "[\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\", \"Jan\", \"Feb\", \"Mar\", \"Apr\", \"May\", \"Jun\", \"Jul\", \"Aug\", \"Sep\", \"Oct\", \"Nov\", \"Dec\"]";
			DataMapper mapper = context.getBean(DataMapper.class);
			return mapper.read(str, ArrayNode.class);
		};
		
		
		// DAYS
		public static final GridAdditional DAYS = context -> {
			String str = "[\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\", \"Sun\", \"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\"]";
			DataMapper mapper = context.getBean(DataMapper.class);
			return mapper.read(str, ArrayNode.class);
		};
		
		
	}

}
