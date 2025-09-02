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
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.object.Result;

@ComponentScan
@RestController
@RequestMapping("/grid")
class GridController {
	
	private final GridHandler gridHandler;
	
	@Autowired
	GridController(
		GridHandler gridHandler	
	) {
		this.gridHandler = gridHandler;
	}

	@Public
	@GetMapping
	protected Result get(
		@RequestParam("name") String name,
		@RequestParam("parent") String parent
	) {
		ObjectNode grid = (ObjectNode) gridHandler.getGrid(parent, name);
		ArrayNode actions = grid.putArray("actions");
		for (CrudAction crudAction : CrudAction.values()) {
			actions.add(crudAction.name());
		}
		return Result.success(grid);
	}
	
}
