package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.template.service.AccessService;

@ComponentScan
@RestController
@RequestMapping("/access")
class AccessController {
	
	private final AccessService accessService;
	
	@Autowired
	AccessController(
		AccessService accessService
	) {
		this.accessService = accessService;
	}

	@Public
	@PostMapping("/login")
	ApiAuth login(
		HttpServletRequest request,
		@RequestParam("username") String username,
		@RequestParam("password") String password
	) throws Exception {
		ApiAuth apiAuth = accessService.login(request, username, password);
		return apiAuth != null ? apiAuth.setApiAccess(null).setApiKey(null) : null;
	}
	
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess logout() {
		return accessService.logout();
	}
	
	@RequestMapping(value = "/info", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess info() {
		return accessService.info(null);
	}
	
}
