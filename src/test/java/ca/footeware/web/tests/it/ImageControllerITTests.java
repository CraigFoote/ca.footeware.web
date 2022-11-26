/**
 *
 */
package ca.footeware.web.tests.it;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
	private WebApplicationContext context;
	private MockMvc mvc;
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate template;

	@BeforeEach
	void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

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
	 *
	 * @throws Exception if shit goes south
	 *
	 */
	@WithMockUser("USER")
	@Test
	void testGetGallery() throws Exception {
		mvc.perform(get("/gallery/gallery1")).andExpect(status().isOk())
				.andExpect(content().string(containsString("<h3 class=\"title center\">gallery1</h3>")))
				.andExpect(content().string(containsString("test-image-horizontal.png")))
				.andExpect(content().string(containsString("test-image-vertical.png")))
				.andExpect(content().string(containsString("test-image-square.png")));
	}

	@WithMockUser("USER")
	@Test
	void testGetImage() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/gallery/gallery1/test-image-horizontal.png")).andExpect(status().isOk())
				.andReturn();
		byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1241, image.getHeight(), "Image wrong height.");

		mvcResult = mvc.perform(get("/gallery/gallery1/test-image-vertical.png")).andExpect(status().isOk())
				.andReturn();
		bytes = mvcResult.getResponse().getContentAsByteArray();
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1241, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		mvcResult = mvc.perform(get("/gallery/gallery1/test-image-square.png")).andExpect(status().isOk()).andReturn();
		bytes = mvcResult.getResponse().getContentAsByteArray();
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		mvcResult = mvc.perform(get("/gallery/gallery1/test-image-bad.png")).andExpect(status().isOk()).andReturn();
		bytes = mvcResult.getResponse().getContentAsByteArray();
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
