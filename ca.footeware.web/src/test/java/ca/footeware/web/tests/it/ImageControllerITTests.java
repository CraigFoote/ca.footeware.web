/**
 * 
 */
package ca.footeware.web.tests.it;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.web.server.LocalServerPort;
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

	private static TestRestTemplate template;
	private String baseURL = "http://localhost:";

	@LocalServerPort
	int port;

	@BeforeClass
	public static void setup() {
		template = new TestRestTemplate(HttpClientOption.ENABLE_REDIRECTS, HttpClientOption.ENABLE_COOKIES);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetGallery() {
		String page = template.withBasicAuth("foote", "bogie97").getForObject(baseURL + port + "/gallery",
				String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/gallery\">Gallery</a></li>"));
		Assert.assertTrue("Horizontal image not displayed.",
				page.contains("href=\"/gallery/test-image-horizontal.png\""));
		Assert.assertTrue("Vertical image not displayed.", page.contains("href=\"/gallery/test-image-vertical.png\""));
		Assert.assertTrue("Square image not displayed.", page.contains("href=\"/gallery/test-image-square.png\""));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 */
	@Test
	public void testGetImageHorizontal() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/test-image-horizonal.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 148, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 */
	@Test
	public void testGetImageVertical() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/test-image-vertical.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 147, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 */
	@Test
	public void testGetImageSquare() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/test-image-square.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 145, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 */
	@Test
	public void testGetImageBadName() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/test-image-bad.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 142, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailHorizontal() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/thumbnails/test-image-horizonal.png",
				byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 159, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailVertical() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/thumbnails/test-image-vertical.png",
				byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 158, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailSquare() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/thumbnails/test-image-square.png",
				byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 156, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailBadName() {
		byte[] bytes = template.getForObject(baseURL + port + "/gallery/thumbnails/test-image-bad.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 153, bytes.length);
	}

}
