/**
 * 
 */
package ca.footeware.web.tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.exceptions.ImageException;
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
	private static final String GALLERY_NAME = "gallery1";

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
		Assert.assertEquals("Wrong number of files found.", 1, files.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getGalleries()}.
	 * 
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testGetGalleries() throws ImageException {
		File[] galleries = service.getGalleries();
		Assert.assertEquals("Should have been one gallery.", 1, galleries.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getImageAsBytes(java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testGetImageAsBytes() throws IOException, ImageException {
		byte[] bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 1241, image.getHeight());
		Assert.assertEquals("Image wrong width.", 1920, image.getWidth());

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 1920, image.getHeight());
		Assert.assertEquals("Image wrong width.", 1241, image.getWidth());

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 1920, image.getHeight());
		Assert.assertEquals("Image wrong width.", 1920, image.getWidth());
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getThumbnailAsBytes(java.lang.String, java.lang.String)}.
	 * 
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testGetThumbnailAsBytes() throws IOException, ImageException {
		byte[] bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 97, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 97, image.getWidth());

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assert.assertEquals("Image wrong height.", 150, image.getHeight());
		Assert.assertEquals("Image wrong width.", 150, image.getWidth());
	}

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#ImageService}.
	 * 
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testImageService() throws ImageException {
		ImageService service = new ImageService(imagesPath);
		Assert.assertNotNull(ImageService.class.getName() + " was null.", service);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#resize(BufferedImage, int, java.awt.Dimension)}
	 * 
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testResizeImage() throws IOException, ImageException {
		byte[] bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = service.resize(originalImage, type,
				service.getDimensions(originalImage, ImageService.MAX_TN_DIMENSION));
		Assert.assertNotNull("Image was null.", image);
		int width = image.getWidth();
		int height = image.getHeight();
		Assert.assertEquals("Thumbnail was not the correct width.", ImageService.MAX_TN_DIMENSION, width);
		Assert.assertTrue("Thumbnail was too tall.", height <= ImageService.MAX_TN_DIMENSION);

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type,
				service.getDimensions(originalImage, ImageService.MAX_TN_DIMENSION));
		Assert.assertNotNull("Image was null.", image);
		width = image.getWidth();
		height = image.getHeight();
		Assert.assertTrue("Thumbnail was too wide.", width <= ImageService.MAX_TN_DIMENSION);
		Assert.assertEquals("Thumbnail was not the correct height.", ImageService.MAX_TN_DIMENSION, height);

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type,
				service.getDimensions(originalImage, ImageService.MAX_TN_DIMENSION));
		Assert.assertNotNull("Image was null.", image);
		width = image.getWidth();
		height = image.getHeight();
		Assert.assertEquals("Thumbnail was not the correct width.", ImageService.MAX_TN_DIMENSION, width);
		Assert.assertEquals("Thumbnail was not the correct height.", ImageService.MAX_TN_DIMENSION, height);
	}

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getExif(File)}
	 * @throws ImageException when shit goes south
	 * @throws IOException when shit goes south
	 */
	@Test
	public void testExif() throws ImageException, IOException {
		File gallery = service.getGalleries()[0];
		File[] imageFiles = service.getFiles(gallery.getName());
		Map<String, String> exif = service.getExif(imageFiles[0]);
		Assert.assertTrue("Missing 'Model' entry.", exif.containsKey("Model"));
	}

}
