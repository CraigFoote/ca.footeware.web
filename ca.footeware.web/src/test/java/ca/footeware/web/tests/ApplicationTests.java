/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web.tests;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.controllers.ImageController;
import ca.footeware.web.controllers.JokeController;

/**
 * @author craig
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private ImageController imageController;

	@Autowired
	private JokeController jokeController;

	/**
	 * 
	 */
	@Test
	public void imageControllerLoads() {
		Assertions.assertThat(imageController).isNotNull();
	}

	/**
	 * 
	 */
	@Test
	public void jokeControllerLoads() {
		Assertions.assertThat(jokeController).isNotNull();
	}

}
