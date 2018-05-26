/**
 * 
 */
package ca.footeware.web.tests.it;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

	private static final String PASSWORD = "bogie97";
	private static final String USERNAME = "foote";
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
		String page = template.getForObject(baseURL + port + "/gallery", String.class);
		Assert.assertTrue("Should have been sent to login page.",
				page.contains("<form action=\"/login\" method=\"post\">"));
		page = template.withBasicAuth(USERNAME, PASSWORD).getForObject(baseURL + port + "/gallery", String.class);
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
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetImageHorizontal() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/test-image-horizontal.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 1656, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 232, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetImageVertical() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/test-image-vertical.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 4037, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 232, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetImageSquare() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/test-image-square.png", byte[].class);
		Assert.assertEquals("Wrong bytes for image.", 4735, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getImage(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetImageBadName() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/test-image-bad.png", byte[].class);
		Assert.assertNull("Should have been no bytes for image with bad name.", bytes);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetThumbnailHorizontal() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/thumbnails/test-image-horizontal.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 2781, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 97, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetThumbnailVertical() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/thumbnails/test-image-vertical.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 1395, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 97, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetThumbnailSquare() throws IOException {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/thumbnails/test-image-square.png", byte[].class);
		Assert.assertEquals("Wrong number of bytes for thumbnail.", 1617, bytes.length);
		BufferedImage image = createImage(bytes);
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getThumbnail(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailBadName() {
		byte[] bytes = template.withBasicAuth(USERNAME, PASSWORD)
				.getForObject(baseURL + port + "/gallery/thumbnails/test-image-bad.png", byte[].class);
		Assert.assertNull("Should have been no bytes for thumbnail with bad name.", bytes);
	}

	/**
	 * Create a {@link BufferedImage} from a byte[].
	 * 
	 * @param bytes
	 *            byte[]
	 * @return {@link BufferedImage}
	 * @throws IOException
	 */
	private BufferedImage createImage(byte[] bytes) throws IOException {
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(in);
		return image;
	}

}
