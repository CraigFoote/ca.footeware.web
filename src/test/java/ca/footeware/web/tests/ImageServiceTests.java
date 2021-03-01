/**
 * 
 */
package ca.footeware.web.tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.web.exceptions.ImageException;
import ca.footeware.web.models.Gallery;
import ca.footeware.web.services.ImageService;

/**
 * Tests {@link ImageService}.
 * 
 * @author Footeware.ca
 */
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

	@Value("${images.max.dimension}")
	private Integer maxImgDim;

	@Value("${images.thumbnails.max.dimension}")
	private Integer maxTnDim;

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getExif(File)}
	 * 
	 * @throws ImageException when shit goes south
	 * @throws IOException    when shit goes south
	 */
	@Test
	public void testExif() throws ImageException, IOException {
		Gallery gallery = service.getGalleries().get(0);
		File[] imageFiles = service.getFiles(gallery.getName());
		Map<String, String> exif = service.getExif(imageFiles[0]);
		Assertions.assertTrue(exif.containsKey("Model"), "Missing 'Model' entry.");
	}

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getFiles()}.
	 */
	@Test
	public void testGetFiles() {
		File[] files = service.getFiles();
		Assertions.assertEquals(files.length, 1, "Wrong number of files found.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getGalleries()}.
	 * 
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testGetGalleries() throws ImageException {
		List<Gallery> galleries = service.getGalleries();
		Assertions.assertEquals(galleries.size(), 1, "Should have been one gallery.");
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
		Assertions.assertEquals(image.getHeight(), 1241, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 1920, "Image wrong width.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getHeight(), 1920, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 1241, "Image wrong width.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getHeight(), 1920, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 1920, "Image wrong width.");
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
		Assertions.assertEquals(image.getHeight(), 97, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 150, "Image wrong width.");

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getHeight(), 150, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 97, "Image wrong width.");

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(image.getHeight(), 150, "Image wrong height.");
		Assertions.assertEquals(image.getWidth(), 150, "Image wrong width.");
	}

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#ImageService}.
	 * 
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testImageService() throws ImageException {
		ImageService service = new ImageService(imagesPath);
		Assertions.assertNotEquals(ImageService.class.getName() + " was null.", service);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#resize(BufferedImage, int, java.awt.Dimension)}
	 * 
	 * @throws IOException when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	public void testResizeImage() throws IOException, ImageException {
		byte[] bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		Assertions.assertNotNull(image);
		int width = image.getWidth();
		int height = image.getHeight();
		Assertions.assertEquals(width, maxTnDim.intValue(), "Thumbnail was not the correct width.");
		Assertions.assertTrue(height <= maxTnDim, "Thumbnail was too tall.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		Assertions.assertNotNull(image);
		width = image.getWidth();
		height = image.getHeight();
		Assertions.assertTrue(width <= maxTnDim, "Thumbnail was too wide.");
		Assertions.assertEquals(height, maxTnDim.intValue(), "Thumbnail was not the correct height.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		Assertions.assertNotNull(image);
		width = image.getWidth();
		height = image.getHeight();
		Assertions.assertEquals(width, maxTnDim.intValue(), "Thumbnail was not the correct width.");
		Assertions.assertEquals(height, maxTnDim.intValue(), "Thumbnail was not the correct height.");
	}

}
