package net.ideahut.springboot.template.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridSource;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.Menu;
import net.ideahut.springboot.template.entity.MenuId;

@ComponentScan
@RestController
@RequestMapping("/menu")
class MenuController {

	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private GridHandler gridHandler;
	
	@GetMapping("list")
	protected Result list() {
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(cache());
		Map<String, List<GridSource>> grids = gridHandler.getSources();
		for (Map.Entry<String, List<GridSource>> entry : grids.entrySet()) {
			Menu menu = new Menu();
			//menus
			menus.add(menu);
		}
		return Result.success(menus);
	}
	
	private Menu cache() {
		Menu menu = new Menu();
		menu.setId(new MenuId("cache", ""));
		menu.setTitle("Cache");
		menu.setIcon("memory_alt");
		menu.setLink("/cache");
		return menu;
	}
	
	private Menu grid(Map.Entry<String, List<String>> entry) {
		Menu parent = new Menu();
		String pkey = entry.getKey();
		if (pkey.isEmpty() || "_".equals(pkey)) {
			pkey = "default";
		}
		parent.setId(new MenuId("grid_" + pkey, ""));
		parent.setTitle(pkey.substring(0, 1).toUpperCase() + pkey.substring(1));
		parent.setIcon("apps");
		Menu menu = dataMapper.copy(parent, Menu.class);
		menu.setChildren(new ArrayList<>());
		for (String name : entry.getValue()) {
			Menu child = new Menu();
			child.setId(new MenuId("grid_" + pkey + "_" + name, ""));
			child.setTitle(name);
			child.setIcon("table_view");
			child.setLink("/grid/" + name + "/" + entry.getKey());
			child.setParent(parent);
			menu.getChildren().add(child);
		}
		return menu;
	}
	
}
