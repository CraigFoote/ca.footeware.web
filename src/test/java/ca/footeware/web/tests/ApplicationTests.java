/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.web.controllers.ImageController;
import ca.footeware.web.controllers.JokeController;

/**
 * @author craig
 *
 */
@SpringBootTest
class ApplicationTests {

	@Autowired
	private ImageController imageController;

	@Autowired
	private JokeController jokeController;

	/**
	 * 
	 */
	@Test
	void imageControllerLoads() {
		Assertions.assertNotNull(imageController);
	}

	/**
	 * 
	 */
	@Test
	void jokeControllerLoads() {
		Assertions.assertNotNull(jokeController);
	}

}
