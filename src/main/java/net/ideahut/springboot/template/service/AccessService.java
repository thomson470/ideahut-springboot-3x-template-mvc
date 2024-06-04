package net.ideahut.springboot.template.service;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiParameter;

public interface AccessService {

	ApiAuth login(HttpServletRequest httpRequest, String username, String password);
	
	void logout();
	
	ApiAccess getApiAccess(ApiParameter apiParameter);
	
}
