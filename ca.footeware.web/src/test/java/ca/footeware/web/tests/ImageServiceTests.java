/**
 * 
 */
package ca.footeware.web.tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.services.ImageService;

/**
 * Tests {@link ImageService}.
 * 
 * @author Footeware.ca
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageServiceTests {

	private static final String IMAGE_SQUARE = "test-image-square.png";
	private static final String IMAGE_VERTICAL = "test-image-vertical.png";
	private static final String IMAGE_HORIZONTAL = "test-image-horizontal.png";

	@Autowired
	private ResourceLoader loader;

	@Autowired
	private ImageService service;

	@Value("${images.path}")
	String imagesPath;

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getFiles()}.
	 */
	@Test
	public void testGetFiles() {
		File[] files = service.getFiles();
		Assert.assertEquals("Wrong number of files found.", 3, files.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getImageAsBytes(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetImageAsBytes() throws IOException {
		byte[] bytes = service.getImageAsBytes(IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 232, image.getWidth());

		bytes = service.getImageAsBytes(IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 232, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());

		bytes = service.getImageAsBytes(IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getThumbnailAsBytes(java.lang.String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetThumbnailAsBytes() throws IOException {
		byte[] bytes = service.getThumbnailAsBytes(IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 97, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());

		bytes = service.getThumbnailAsBytes(IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 97, image.getWidth());

		bytes = service.getThumbnailAsBytes(IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#ImageService(org.springframework.core.io.ResourceLoader)}.
	 */
	@Test
	public void testImageService() {
		ImageService service = new ImageService(loader, imagesPath);
		Assert.assertNotNull(ImageService.class.getName() + " was null.", service);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#resizeImage(java.awt.image.BufferedImage, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testResizeImage() throws IOException {
		byte[] bytes = service.getImageAsBytes(IMAGE_HORIZONTAL);
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = service.resizeImage(originalImage, type);
		Assert.assertNotNull("Image was null.", image);
		int width = image.getWidth();
		int height = image.getHeight();
		Assert.assertEquals("Thumbnail was not the correct width.", width, ImageService.MAX_DIMENSION);
		Assert.assertTrue("Thumbnail was too high.", height <= ImageService.MAX_DIMENSION);

		bytes = service.getImageAsBytes(IMAGE_VERTICAL);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resizeImage(originalImage, type);
		Assert.assertNotNull("Image was null.", image);
		width = image.getWidth();
		height = image.getHeight();
		Assert.assertTrue("Thumbnail was too wide.", width <= ImageService.MAX_DIMENSION);
		Assert.assertEquals("Thumbnail was not the correct height.", height, ImageService.MAX_DIMENSION);

		bytes = service.getImageAsBytes(IMAGE_SQUARE);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resizeImage(originalImage, type);
		Assert.assertNotNull("Image was null.", image);
		width = image.getWidth();
		height = image.getHeight();
		Assert.assertEquals("Thumbnail was not the correct width.", width, ImageService.MAX_DIMENSION);
		Assert.assertEquals("Thumbnail was not the correct height.", height, ImageService.MAX_DIMENSION);
	}

}
