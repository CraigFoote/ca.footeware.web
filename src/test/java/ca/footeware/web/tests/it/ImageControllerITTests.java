/**
 * 
 */
package ca.footeware.web.tests.it;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import ca.footeware.web.controllers.ImageController;

/**
 * Test {@link ImageController}.
 * 
 * @author Footeware.ca
 */
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
		Assertions.assertTrue(page.contains("<form action=\"/login\" method=\"post\">"),
				"Should have been sent to login page.");
		page = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/", String.class);
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1\">gallery1</a>"),
				"Should have listed 'gallery1'.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetGallery() {
		String page = template.getForObject("/gallery/gallery1/", String.class);
		Assertions.assertTrue(page.contains("<form action=\"/login\" method=\"post\">"),
				"Should have been sent to login page.");
		page = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/", String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/gallery\">Gallery</a></li>"),
				"Should have been sent to gallery1 page.");
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1/test-image-horizontal.png\""),
				"Horizontal thumbnail not displayed.");
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1/test-image-vertical.png\""),
				"Vertical thumbnail not displayed.");
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1/test-image-square.png\""),
				"Square thumbnail not displayed.");
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
		Assertions.assertEquals(image.getWidth(), 1920, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 1241, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-vertical.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getWidth(), 1241, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 1920, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-square.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getWidth(), 1920, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 1920, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-bad.png",
				byte[].class);
		Assertions.assertTrue(new String(bytes).contains("gallery1/test-image-bad.png not found."),
				"Should have been an error message for image with bad name.");
	}

	/**
	 * Test method for
	 * {@link ImageController#getThumbnail(java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException when shit goes south
	 */
	@Test
	public void testGetThumbnail() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-horizontal.png", byte[].class);
		Assertions.assertEquals(bytes.length, 2781, "Wrong number of bytes for thumbnail.");
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getWidth(), 150, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 97, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-vertical.png", byte[].class);
		Assertions.assertEquals(bytes.length, 1395, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getWidth(), 97, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 150, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-square.png", byte[].class);
		Assertions.assertEquals(bytes.length, 1617, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getWidth(), 150, "Image wrong width.");
		Assertions.assertEquals(image.getHeight(), 150, "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-bad.png", byte[].class);
		Assertions.assertTrue(new String(bytes).contains("gallery1/test-image-bad.png not found."),
				"Should have been an error message for thumbnail with bad name.");
	}

}
