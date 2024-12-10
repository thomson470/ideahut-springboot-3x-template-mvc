package net.ideahut.springboot.template.controller;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;

@ComponentScan
@RestController
@RequestMapping("/")
class RootController {

	@Public
	@GetMapping("")
	void get() {
		/**/
	}
	
	@Public
	@GetMapping("favicon.ico")
	void favicon() {
		/**/
	}
	
}
