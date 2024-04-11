package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.object.Result;

@ComponentScan
@RestController
@RequestMapping("/grid")
class GridController {
	
	@Autowired
	private GridHandler gridHandler;

	@Public
	@GetMapping
	protected Result get(
		@RequestParam("name") String name,
		@RequestParam("parent") String parent
	) {
		ObjectNode grid = gridHandler.getGrid(parent, name);
		ArrayNode actions = grid.putArray("actions");
		actions.add("PAGE");
		actions.add("CREATE");
		actions.add("UPDATE");
		actions.add("DELETE");
		actions.add("DELETES");
		return Result.success(grid);
	}
	
}
