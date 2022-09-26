/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.web.controllers.ImageController;

/**
 * @author Footeware.ca
 *
 */
@SpringBootTest
class ApplicationTests {

	@Autowired
	private ImageController imageController;

	/**
	 *
	 */
	@Test
	void imageControllerLoads() {
		Assertions.assertNotNull(imageController);
	}

}
