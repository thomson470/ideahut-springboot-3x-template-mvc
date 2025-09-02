package net.ideahut.springboot.template.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.definition.AdminDefinition;
import net.ideahut.springboot.definition.ApiDefinition;
import net.ideahut.springboot.definition.CacheGroupDefinition;
import net.ideahut.springboot.definition.CrudDefinition;
import net.ideahut.springboot.definition.DatabaseAuditDefinition;
import net.ideahut.springboot.definition.FilterDefinition;
import net.ideahut.springboot.definition.GridDefinition;
import net.ideahut.springboot.definition.KafkaDefinition;
import net.ideahut.springboot.definition.RestDefinition;
import net.ideahut.springboot.entity.EntityForeignKeyParam;
import net.ideahut.springboot.mail.MailProperties;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.task.TaskProperties;

/*
 * Class properties yang definisinya sama dengan application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "config")
@Setter
@Getter
public class AppProperties {
	
	// Base URL untuk diakses public
	private String publicBaseUrl;
	
	// Tunggu semua bean selesai saat reconfigure (service status menjadi ready)
	private Boolean waitAllBeanConfigured;
	
	// Log semua error yang terjadi
	private Boolean logAllError;
	
	// Start scheduler pada saat startup
	private Boolean autoStartScheduler;
	
	// Direktori file message berdasarkan bahasa
	private String messagePath;
	
	// Lokasi file report (jrxml / jasper)
	private String reportPath;
	
	// Parameter untuk menghandle anotasi @ForeignKeyEntity
	// Ini solusi jika terjadi error saat membuat native image dimana entity memiliki @ManyToOne & @OneToMany
	// tapi package-nya berbeda dengan package project (error ByteCodeProvider saat runtime)
	private EntityForeignKeyParam foreignKey;
	
	private MailProperties mail;
	private Task task;
	private Redis redis;
	
	private FilterDefinition filter;
	private RestDefinition rest;
	private DatabaseAuditDefinition audit;
	private CacheGroupDefinition cache;
	private AdminDefinition admin;
	private ApiDefinition api;
	private GridDefinition grid;
	private CrudDefinition crud;
	private KafkaDefinition kafka;
	
	@Setter
	@Getter
	public static class Redis {
		private RedisProperties.Connection common 	= new RedisProperties.Connection();
	}
	
	@Setter
	@Getter
	public static class Task {
		private TaskProperties common 	= new TaskProperties();
		private TaskProperties audit 	= new TaskProperties();
		private TaskProperties rest 	= new TaskProperties();
		private TaskProperties webAsync = new TaskProperties();
	}
	
}
