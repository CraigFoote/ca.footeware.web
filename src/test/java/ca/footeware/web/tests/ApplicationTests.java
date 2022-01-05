/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import ca.footeware.web.SslRestTemplate;
import ca.footeware.web.controllers.ImageController;
import ca.footeware.web.controllers.JokeController;
import ca.footeware.web.exceptions.SslException;

/**
 * @author Footeware.ca
 *
 */
@SpringBootTest
class ApplicationTests {

	@Autowired
	private ImageController imageController;

	@Autowired
	private JokeController jokeController;

	@Autowired
	private SslRestTemplate sslRestTemplate;

	@Value("${server.ssl.trust-store}")
	private Resource trustStore;

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

	/**
	 *
	 */
	@Test
	void restTemplateLoads() {
		Assertions.assertNotNull(sslRestTemplate);
	}

	@Test
	void restTemplateThrowsSslException() {
		try {
			trustStore = null;
			SslRestTemplate rt = new SslRestTemplate();
			rt.restTemplate();
		} catch (Exception e) {
			Assertions.assertTrue(e instanceof SslException);
		}
	}

	@Test
	void testSslException() {
		Exception ex = new SslException();
		Assertions.assertNull(ex.getCause());
		Assertions.assertNull(ex.getMessage());

		ex = new SslException("test");
		Assertions.assertEquals("test", ex.getMessage());

		ex = new SslException("test", new Exception());
		Assertions.assertEquals("test", ex.getMessage());

		ex = new SslException(new Exception());
		Assertions.assertNotNull(ex.getCause());
	}

}
