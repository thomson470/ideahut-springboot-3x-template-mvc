package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.service.MessageService;

@ComponentScan
@RestController
@RequestMapping("/message")
class MessageController {

	@Autowired
	private MessageService messageService;

	@Public
	@GetMapping(value = "/mobile")
	protected Result mobile() {
		JsonNode mobile = messageService.getResource("mobile");
		return Result.success(mobile);
	}
	
	@Public
	@GetMapping(value = "/portal")
	protected Result portal() {
		JsonNode portal = messageService.getResource("portal");
		return Result.success(portal);
	}
	
}
