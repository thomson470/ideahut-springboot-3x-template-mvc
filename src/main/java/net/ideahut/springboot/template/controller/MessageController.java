package net.ideahut.springboot.template.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.template.service.MessageService;

@Public
@ComponentScan
@RestController
@RequestMapping("/message")
class MessageController {

	private final MessageService messageService;
	
	@Autowired
	MessageController(
		MessageService messageService		
	) {
		this.messageService = messageService;
	}

	@GetMapping(value = "/mobile")
	JsonNode mobile() {
		return messageService.getResource("mobile");
	}
	
	@GetMapping(value = "/portal")
	JsonNode portal() {
		return messageService.getResource("portal");
	}
	
}
