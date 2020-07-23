/**
 * 
 */
package ca.footeware.web.tests.it;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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

	private static final String PASSWORD = "bogie97";
	private static final String USERNAME = "foote";

	@Autowired
	private TestRestTemplate template;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGalleries(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetGalleries() {
		String page = template.getForObject("/gallery/", String.class);
		Assert.assertTrue("Should have been sent to login page.",
				page.contains("<form action=\"/login\" method=\"post\">"));
		page = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/", String.class);
		Assert.assertTrue("Should have listed 'gallery1'.", page.contains("<a href=\"/gallery/gallery1\">gallery1</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetGallery() {
		String page = template.getForObject("/gallery/gallery1/", String.class);
		Assert.assertTrue("Should have been sent to login page.",
				page.contains("<form action=\"/login\" method=\"post\">"));
		page = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/", String.class);
		Assert.assertTrue("Should have been sent to gallery1 page.",
				page.contains("<li class=\"active\"><a href=\"/gallery\">Gallery</a></li>"));
		Assert.assertTrue("Horizontal thumbnail not displayed.",
				page.contains("href=\"/gallery/gallery1/test-image-horizontal.png\""));
		Assert.assertTrue("Vertical thumbnail not displayed.",
				page.contains("href=\"/gallery/gallery1/test-image-vertical.png\""));
		Assert.assertTrue("Square thumbnail not displayed.",
				page.contains("href=\"/gallery/gallery1/test-image-square.png\""));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException when shit goes south
	 */
	@Test
	public void testGetImage() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/gallery1/test-image-horizontal.png", byte[].class);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 1920, image.getWidth());
		Assert.assertEquals("Image wrong height.", 1241, image.getHeight());

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-vertical.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 1241, image.getWidth());
		Assert.assertEquals("Image wrong height.", 1920, image.getHeight());

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-square.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 1920, image.getWidth());
		Assert.assertEquals("Image wrong height.", 1920, image.getHeight());

//		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-bad.png",
//				byte[].class);
//		Assert.assertNull("Should have been no bytes for image with bad name.", bytes);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException when shit goes south
	 */
	@Test
	public void testGetThumbnail() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-horizontal.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 2781, bytes.length);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 97, image.getHeight());

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-vertical.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 1395, bytes.length);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 97, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-square.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 1617, bytes.length);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());

//		bytes = template.withBasicAuth(USERNAME, PASSWORD)
//				.getForObject("/gallery/thumbnails/gallery1/test-image-bad.png", byte[].class);
//		Assert.assertNull("Should have been no bytes for thumbnail with bad name.", bytes);
	}

}
