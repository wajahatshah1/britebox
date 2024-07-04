package com.briteboxbackend.briterbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BriterboxApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BriterboxApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BriterboxApplication.class);
	}
}
