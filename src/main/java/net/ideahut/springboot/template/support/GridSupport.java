package net.ideahut.springboot.template.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.node.ArrayNode;

import net.ideahut.springboot.crud.Condition;
import net.ideahut.springboot.grid.GridAdditional;
import net.ideahut.springboot.grid.GridOption;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.mapper.DataMapperImpl;
import net.ideahut.springboot.object.Option;
import net.ideahut.springboot.template.AppConstants;

public final class GridSupport {
	
	private GridSupport() {}
	
	private static DataMapper mapper = new DataMapperImpl();
	
	/*
	 * OPTIONS
	 */
	public static Map<String, GridOption> getOptions() {
		Map<String, GridOption> options = new HashMap<>();
		options.put("CRUD_CONDITION", StaticOption.CRUD_CONDITION);
		options.put("GENDER", StaticOption.GENDER);
		options.put("YES_NO", StaticOption.YES_NO);
		options.put("USER_STATUS", StaticOption.USER_STATUS);
		options.put("MENU_TYPE", StaticOption.MENU_TYPE);
		return options;
	}
	public static class StaticOption {
		
		// YES / NO
		public static GridOption YES_NO = new GridOption() {
			@Override
			public List<Option> getOption(ApplicationContext context) {
				return Arrays.asList(
					new Option("Y", "Yes"),
					new Option("N", "No")
				);
			}
		};
		
		// GENDER
		public static GridOption GENDER = new GridOption() {
			@Override
			public List<Option> getOption(ApplicationContext context) {
				return Arrays.asList(
					new Option("M", "Male"),
					new Option("F", "Female")
				);
			}
		};
		
		// CRUD CONDITION
		public static GridOption CRUD_CONDITION = new GridOption() {
			@Override
			public List<Option> getOption(ApplicationContext context) {
				List<Option> options = new ArrayList<>();
				for (Condition condition : Condition.values()) {
					options.add(new Option(condition.name(), condition.name()));
				}
				return options;
			}
		};
		
		// USER STATUS
		public static GridOption USER_STATUS = new GridOption() {
			@Override
			public List<Option> getOption(ApplicationContext context) {
				return Arrays.asList(
					new Option(AppConstants.Profile.Status.REGISTER + "", "Register"),
					new Option(AppConstants.Profile.Status.ACTIVE + "", "Active"),
					new Option(AppConstants.Profile.Status.INACTIVE + "", "InActive"),
					new Option(AppConstants.Profile.Status.BLOCKED + "", "Blocked")
				);
			}
		};
		
		// MENU TYPE
		public static GridOption MENU_TYPE = new GridOption() {
			@Override
			public List<Option> getOption(ApplicationContext context) {
				return Arrays.asList(
					new Option("mobile", "Mobile"),
					new Option("portal", "Portal")
				);
			}
		};
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
		
		// MONTHS
		public static GridAdditional MONTHS = new GridAdditional() {
			@Override
			public ArrayNode getAdditional(ApplicationContext context) {
				String str = "[\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\", \"Jan\", \"Feb\", \"Mar\", \"Apr\", \"May\", \"Jun\", \"Jul\", \"Aug\", \"Sep\", \"Oct\", \"Nov\", \"Dec\"]";
				return mapper.read(str, ArrayNode.class);
			}
		};
		
		
		// DAYS
		public static GridAdditional DAYS = new GridAdditional() {
			@Override
			public ArrayNode getAdditional(ApplicationContext context) {
				String str = "[\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\", \"Sun\", \"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\"]";
				return mapper.read(str, ArrayNode.class);
			}
		};
		
		
	}

}
