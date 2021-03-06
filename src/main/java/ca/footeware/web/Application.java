/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point to application.
 * 
 * @author Footeware.ca
 */
@SpringBootApplication
public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	/**
	 * @param args {@link String} array
	 */
	public static void main(String[] args) {
		LOG.info("Application starting...");
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	protected void postConstruct() {
		LOG.info("Application started");
	}

	@PreDestroy
	protected void preDestroy() {
		LOG.info("Application stopped.");
	}
}
