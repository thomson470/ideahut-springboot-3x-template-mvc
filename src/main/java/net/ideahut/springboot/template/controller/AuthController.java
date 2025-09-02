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
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.template.service.AuthService;

@ComponentScan
@RestController
@RequestMapping("/auth")
class AuthController {
	
	private final AuthService authService;
	private final WebMvcApiService apiService;
	
	@Autowired
	AuthController(
		AuthService authService,
		WebMvcApiService apiService
	) {
		this.authService = authService;
		this.apiService = apiService;
	}
	
	@Public
	@PostMapping("/login")
	ApiAuth login(
		HttpServletRequest httpRequest,
		@RequestParam("username") String username,
		@RequestParam("password") String password
	) throws Exception {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		ApiAuth apiAuth = authService.login(apiRequest, username, password);
		return apiAuth != null ? apiAuth.setApiAccess(null).setApiKey(null) : null;
	}
	
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess logout(
		HttpServletRequest httpRequest
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.logout(apiRequest);
	}
	
	@RequestMapping(value = "/info", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess info(
		HttpServletRequest httpRequest	
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.info(apiRequest);
	}
	
}
