/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Assign view names to URLs.
 *
 * @author Footeware.ca
 *
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/gear").setViewName("gear");
		registry.addViewController("/webcam").setViewName("webcam");
		registry.addViewController("/jokes").setViewName("jokes");
		registry.addViewController("/jokes/joke").setViewName("joke");
		registry.addViewController("/jokes/add").setViewName("addjoke");
		registry.addViewController("/jokes/edit").setViewName("editjoke");
		registry.addViewController("/gallery").setViewName("gallery");
		registry.addViewController("/error").setViewName("error");
	}

}