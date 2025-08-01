package net.ideahut.springboot.template.controller.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

class Helper {

	private Helper() {}
	
	protected static Sort getSort(String string) {
		string = string != null ? string.trim() : "";
		if (!string.isEmpty()) {
			String[] split = string.split(",");
			List<Order> orders = new ArrayList<>();
			for (String s : split) {
				s = s.trim();
				if (!s.isEmpty()) {
					boolean desc = s.startsWith("-");
					if (desc) {
						s = s.substring(1);
						orders.add(Order.desc(s));
					} else {
						orders.add(Order.asc(s));
					}
				}
			}
			return Sort.by(orders);
		}
		return null;
	}
	
}
