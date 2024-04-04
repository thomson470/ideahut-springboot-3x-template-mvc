package net.ideahut.springboot.sample.generator;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import net.ideahut.springboot.sample.Application;
import net.ideahut.springboot.tool.GridTool;

public class GridGenerator {

	public static void main(String[] args) throws Exception {
		String output = System.getProperty("user.dir") + File.separator + "grids";
		File directory = new File(output);
		directory.mkdirs();
		System.setProperty("server.port", "0");
		SpringApplication application = new SpringApplication(Application.class);
		application.setLazyInitialization(false);
		application.setLogStartupInfo(true);
		ConfigurableApplicationContext context = application.run(args);
		GridTool.generate(context, directory, 2000L);
	}

}
