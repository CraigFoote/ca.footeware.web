/**
 * 
 */
package ca.footeware.web.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.web.controllers.ResourceController;

/**
 * @author Footeware.ca
 *
 */
@SpringBootTest
class ResourceControllerTests {

	@Autowired
	private ResourceController controller;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ResourceController#getGearPage()}.
	 */
	@Test
	void testGetGearPage() {
		String page = controller.getGearPage();
		Assertions.assertEquals("gear", page);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ResourceController#getLoginPage()}.
	 */
	@Test
	void testGetLoginPage() {
		String page = controller.getLoginPage();
		Assertions.assertEquals("login", page);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ResourceController#getWebcamPage()}.
	 */
	@Test
	void testGetWebcamPage() {
		String page = controller.getWebcamPage();
		Assertions.assertEquals("webcam", page);
	}

}
