package net.ideahut.springboot.template.service;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiParameter;
import net.ideahut.springboot.api.ApiRequest;

public interface AuthService {
	
	ApiAuth login(ApiRequest apiRequest, String username, String password) throws Exception;
	ApiAccess logout(ApiRequest apiRequest);
	ApiAccess info(ApiRequest apiRequest);
	
	ApiAccess getApiAccessForExternal(ApiRequest apiRequest);
	ApiAccess getApiAccessForInternal(ApiParameter apiParameter);
	
	String createConsumerToken(ApiRequest apiRequest);
	
}
