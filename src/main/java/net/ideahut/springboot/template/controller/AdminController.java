package net.ideahut.springboot.template.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.WebMvcAdminController;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.security.WebMvcSecurity;

@ApiExclude
@ComponentScan
@RestController
@RequestMapping("/_/api")
class AdminController extends WebMvcAdminController {
	
	private final DataMapper dataMapper;
	private final AdminHandler adminHandler;
	private final WebMvcSecurity webMvcSecurity;
	
	@Autowired
	AdminController(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		WebMvcSecurity webMvcSecurity
	) {
		this.dataMapper = dataMapper;
		this.adminHandler = adminHandler;
		this.webMvcSecurity = webMvcSecurity;
	}
	
	@Override
	protected DataMapper dataMapper() {
		return dataMapper;
	}
	
	@Override
	protected AdminHandler adminHandler() {
		return adminHandler;
	}

	@Override
	protected WebMvcSecurity webMvcSecurity() {
		return webMvcSecurity;
	}

}
