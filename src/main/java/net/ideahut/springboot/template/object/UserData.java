package net.ideahut.springboot.template.object;

import lombok.Getter;

@Getter
public class UserData {

	private String userId;
	private String username;
	private String password;
	private String roleCode;
	
	public UserData setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public UserData setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public UserData setPassword(String password) {
		this.password = password;
		return this;
	}

	public UserData setRoleCode(String roleCode) {
		this.roleCode = roleCode;
		return this;
	}
	
}
