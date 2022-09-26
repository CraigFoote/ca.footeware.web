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
class ImageControllerITTests {

	private static final String PASSWORD = "bogie97";
	private static final String USERNAME = "foote";

	@Autowired
	private TestRestTemplate template;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGalleries(org.springframework.ui.Model)}.
	 */
	@Test
	void testGetGalleries() {
		String page = template.getForObject("/gallery", String.class);
		Assertions.assertTrue(page.contains("<h3 class=\"title center\">Galleries</h3>"),
				"Should have been sent to galleries page.");
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1\">gallery1</a>"),
				"Should have listed 'gallery1'.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	void testGetGallery() {
		String page = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/", String.class);
		Assertions.assertTrue(
				page.contains("<a class=\"nav-link active\"\n" + "						href=\"/gallery\">Gallery</a>"),
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
	void testGetImage() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/gallery1/test-image-horizontal.png", byte[].class);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1241, image.getHeight(), "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-vertical.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1241, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD).getForObject("/gallery/gallery1/test-image-square.png",
				byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

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
	void testGetThumbnail() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-horizontal.png", byte[].class);
		Assertions.assertEquals(2781, bytes.length, "Wrong number of bytes for thumbnail.");
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(97, image.getHeight(), "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-vertical.png", byte[].class);
		Assertions.assertEquals(1395, bytes.length, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(97, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-square.png", byte[].class);
		Assertions.assertEquals(1617, bytes.length, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject("/gallery/thumbnails/gallery1/test-image-bad.png", byte[].class);
		Assertions.assertTrue(new String(bytes).contains("gallery1/test-image-bad.png not found."),
				"Should have been an error message for thumbnail with bad name.");
	}

}
