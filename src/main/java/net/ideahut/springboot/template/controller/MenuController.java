package net.ideahut.springboot.template.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridParent;
import net.ideahut.springboot.grid.GridSource;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.entity.app.Menu;
import net.ideahut.springboot.template.entity.app.MenuId;

@ComponentScan
@RestController
@RequestMapping("/menu")
class MenuController {
	
	private static class Strings {
		private static final String GRID_PREFIX = "grid_";
		private static final String MENU_ICON = "table_view";
	}

	private final DataMapper dataMapper;
	private final GridHandler gridHandler;
	
	@Autowired
	MenuController(
		DataMapper dataMapper,
		GridHandler gridHandler
	) {
		this.dataMapper = dataMapper;
		this.gridHandler = gridHandler;
	}
	
	@GetMapping("list")
	List<Menu> list() {
		List<Menu> menus = new ArrayList<>();
		menus.add(cache());
		Menu grid = grid();
		if (grid != null) {
			menus.add(grid);
		}
		return menus;
	}
	
	private Menu cache() {
		Menu menu = new Menu();
		menu.setId(new MenuId("cache", ""));
		menu.setTitle("Cache");
		menu.setIcon("memory_alt");
		menu.setLink("/cache");
		return menu;
	}
	
	private Menu grid() {
		List<GridParent> parents = gridHandler.getTree();
		if (parents == null || parents.isEmpty()) {
			return null;
		}
		Menu root = new Menu();
		root.setId(new MenuId("grid", ""));
		root.setTitle("Grid");
		root.setIcon("apps");
		root.setChildren(new ArrayList<>());
		if (parents.size() == 1) {
			GridParent parent = parents.get(0);
			if (parent.getTitle() != null && !parent.getTitle().trim().isEmpty()) {
				root.setTitle(parent.getTitle());
			}
			Menu proot = dataMapper.copy(root, Menu.class);
			for (GridSource source : parent.getSources()) {
				Menu menu = new Menu();
				menu.setId(new MenuId(Strings.GRID_PREFIX + source.getName(), ""));
				menu.setTitle(source.getTitle());
				menu.setIcon(Strings.MENU_ICON);
				menu.setLink("/grid?name=" + source.getName() + "&parent=" + parent.getName());
				menu.setParent(proot);
				root.getChildren().add(menu);
			}
		} else {
			Menu proot = dataMapper.copy(root, Menu.class);
			for (GridParent parent : parents) {
				Menu menu = new Menu();
				menu.setId(new MenuId(Strings.GRID_PREFIX + parent.getName(), ""));
				menu.setTitle(parent.getTitle());
				menu.setIcon(Strings.MENU_ICON);
				menu.setParent(proot);
				Menu pmenu = dataMapper.copy(menu, Menu.class);
				root.getChildren().add(menu);
				for (GridSource source : parent.getSources()) {
					Menu child = new Menu();
					child.setId(new MenuId(Strings.GRID_PREFIX + parent.getName() + "_" + source.getName(), ""));
					child.setTitle(source.getTitle());
					child.setIcon(Strings.MENU_ICON);
					child.setLink("/grid?name=" + source.getName() + "&parent=" + parent.getName());
					child.setParent(pmenu);
					menu.getChildren().add(child);
				}
			}
		}
		return root;
	}
	
}
