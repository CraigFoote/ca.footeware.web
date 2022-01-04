/**
 *
 */
package ca.footeware.web.tests.it;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import ca.footeware.web.controllers.ImageController;

/**
 * Test {@link ImageController}.
 *
 * @author Footeware.ca
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class ImageControllerITTests extends ItTests {

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGalleries(org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetGalleries() throws RestClientException, Exception {
		ResponseEntity<String> response = restTemplate().getForEntity(HOST + "/gallery", String.class,
				Collections.emptyMap());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertTrue(response.getBody().contains("<h3 class=\"title center\">Galleries</h3>"),
				"Should have found Galleries page.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetGallery() throws RestClientException, Exception {
		String page = restTemplate().getForObject(HOST + "/gallery/gallery1/", String.class);
		Assertions.assertTrue(page.contains("<form action=\"/login\" method=\"post\">"),
				"Should have been sent to login page.");
		page = restTemplate().getForObject(HOST + "/gallery/gallery1/", String.class);
		Assertions.assertTrue(page.contains("<form action=\"/login\" method=\"post\">"),
				"Should be prompted to login.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetGalleryLogin() throws RestClientException, Exception {
		ResponseEntity<String> response = restTemplate().exchange(HOST + "/gallery/gallery1/", HttpMethod.GET,
				new HttpEntity<String>(createAuthHeaders()), String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		String page = response.getBody();
		Assertions.assertTrue(page.contains("<h3 class=\"title center\">gallery1</h3>"));
		Assertions.assertTrue(page.contains("href=\"/gallery/gallery1/test-image-horizontal.png\">"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String, java.lang.String)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetImage() throws RestClientException, Exception {
		ResponseEntity<byte[]> response = restTemplate().exchange(HOST + "/gallery/gallery1/test-image-horizontal.png",
				HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), byte[].class);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1241, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/gallery1/test-image-vertical.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(1241, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/gallery1/test-image-square.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/gallery1/test-image-bad.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertTrue(new String(response.getBody()).contains("gallery1/test-image-bad.png not found."),
				"Should have been an error message for image with bad name.");
	}

	/**
	 * Test method for
	 * {@link ImageController#getThumbnail(java.lang.String, java.lang.String)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetThumbnail() throws RestClientException, Exception {
		ResponseEntity<byte[]> response = restTemplate().exchange(
				HOST + "/gallery/thumbnails/gallery1/test-image-horizontal.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(97, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/thumbnails/gallery1/test-image-vertical.png",
				HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(97, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/thumbnails/gallery1/test-image-square.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		response = restTemplate().exchange(HOST + "/gallery/thumbnails/gallery1/test-image-bad.png", HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders()), byte[].class);
		image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
		Assertions.assertTrue(new String(response.getBody()).contains("gallery1/test-image-bad.png not found."),
				"Should have been an error message for image with bad name.");
	}

}
