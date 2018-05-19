/**
 * 
 */
package ca.footeware.web.tests.it;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.controllers.ImageController;

/**
 * Test {@link ImageController}.
 * 
 * @author Footeware.ca
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImageControllerITTests {

	@Autowired
	private TestRestTemplate template;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetGallery() {
		String page = template.getForObject("/gallery", String.class);
		Assert.assertTrue("Incorrect page returned.", page.contains("<form action=\"/login\" method=\"post\">"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnail() {
		byte[] image = template.getForObject("/gallery/thumbnails/test-image.png", byte[].class);
		Assert.assertTrue("Wrong bytes for image.", image.length == 4314);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 */
	@Test
	public void testGetImage() {
		byte[] image = template.getForObject("/gallery/test-image.png", byte[].class);
		Assert.assertTrue("Wrong bytes for image.", image.length == 4314);
	}

}
