package net.ideahut.springboot.template.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Result;

@ComponentScan
@RestController
@RequestMapping("/reload")
public class ReloadController {
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired(required = false)
	private AdminHandler adminHandler;
	
	
	
	@GetMapping("/options")
	public Result options() {
		Set<JsonNode> reloads = new HashSet<>();
		if (adminHandler != null) {
			Set<String> names = adminHandler.reload();
			if (names != null) {
				for (String name : names) {
					ObjectNode reload = dataMapper.createObjectNode();
					reload.put("icon", "memory");
					reload.put("path", "/" + name);
					reload.put("title", name.substring(0, 1).toUpperCase() + name.substring(1));
					reloads.add(reload);
				}
			}
		}
		return Result.success(reloads);
	}
	
	@PostMapping("/{name}")
	public Result reload(
		@PathVariable("name") String name
	) throws Exception {
		if (adminHandler != null) {
			boolean loaded = adminHandler.reload(name);
			return Result.success(loaded);
		}
		return Result.error("ERR.NULL_HANDLER", "AdminHandler is null");
	}

}
