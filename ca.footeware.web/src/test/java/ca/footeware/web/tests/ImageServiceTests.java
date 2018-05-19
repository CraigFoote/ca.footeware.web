/**
 * 
 */
package ca.footeware.web.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import ca.footeware.web.services.ImageService;

/**
 * Tests {@link ImageService}.
 * 
 * @author Footeware.ca
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageServiceTests {

	@Autowired
	private ResourceLoader loader;

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#ImageService(org.springframework.core.io.ResourceLoader)}.
	 */
	@Test
	public void testImageService() {
		ImageService service = getService();
		Assert.notNull(service, ImageService.class.getName() + " was null.");
	}

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getFiles()}.
	 */
	@Test
	public void testGetFiles() {
		File[] files = getService().getFiles();
		Assert.notEmpty(files, "No files found.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#resizeImage(java.awt.image.BufferedImage, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testResizeImage() throws IOException {
		BufferedImage originalImage = ImageIO.read(getImageFile());
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = getService().resizeImage(originalImage, type);
		Assert.notNull(image, "Image was null.");
		int width = image.getWidth();
		int height = image.getHeight();
		getService();
		Assert.isTrue(width <= ImageService.MAX_DIMENSION, "Thumbnail was too wide.");
		Assert.isTrue(height <= ImageService.MAX_DIMENSION, "Thumbnail was too high.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getImageAsBytes(java.lang.String)}.
	 */
	@Test
	public void testGetImageAsBytes() {
		byte[] bytes = getService().getImageAsBytes(getImageFile().getName());
		Assert.isTrue(bytes.length == 1656, "Image bytes not found.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getThumbnailAsBytes(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailAsBytes() {
		byte[] bytes = getService().getThumbnailAsBytes(getImageFile().getName());
		Assert.isTrue(bytes.length == 2781, "Thumbnail bytes not found.");
	}

	/**
	 * Gets the file of the image under test.
	 * 
	 * @return {@link File}
	 */
	private File getImageFile() {
		return getService().getFiles()[0];
	}

	/**
	 * Gets the ImageService instance.
	 * 
	 * @return {@link ImageService}
	 */
	private ImageService getService() {
		File resources = new File("src/test/resources/images");
		return new ImageService(loader, resources.getAbsolutePath());
	}

}
