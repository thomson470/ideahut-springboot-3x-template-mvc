package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.ideahut.springboot.entity.EntityAnnotationIntrospector;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.EntityTrxManagerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.mapper.DataMapperImpl;

@Configuration
class CommonConfig {

	@Bean
	protected DataMapper dataMapper() {
		return new DataMapperImpl()
		.setIntrospector(new EntityAnnotationIntrospector());
	}
	
	@Bean
	protected EntityTrxManager entityTrxManager() {
		return new EntityTrxManagerImpl();
	}
	
}
