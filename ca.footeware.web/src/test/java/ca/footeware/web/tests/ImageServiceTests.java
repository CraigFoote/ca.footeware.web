/**
 * 
 */
package ca.footeware.web.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ResourceLoader loader;

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

	/**
	 * Test method for {@link ca.footeware.web.services.ImageService#getFiles()}.
	 */
	@Test
	public void testGetFiles() {
		File[] files = getService().getFiles();
		Assert.assertEquals("No files found.", 3, files.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getImageAsBytes(java.lang.String)}.
	 */
	@Test
	public void testGetImageAsBytes() {
		byte[] bytes = getService().getImageAsBytes(getImageFile().getName());
		Assert.assertEquals("Image bytes not found.", 1656, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#getThumbnailAsBytes(java.lang.String)}.
	 */
	@Test
	public void testGetThumbnailAsBytes() {
		byte[] bytes = getService().getThumbnailAsBytes(getImageFile().getName());
		Assert.assertEquals("Thumbnail bytes not found.", 2781, bytes.length);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.ImageService#ImageService(org.springframework.core.io.ResourceLoader)}.
	 */
	@Test
	public void testImageService() {
		ImageService service = getService();
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
		BufferedImage originalImage = ImageIO.read(getImageFile());
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = getService().resizeImage(originalImage, type);
		Assert.assertNotNull("Image was null.", image);
		int width = image.getWidth();
		int height = image.getHeight();
		getService();
		Assert.assertTrue("Thumbnail was too wide.", width <= ImageService.MAX_DIMENSION);
		Assert.assertTrue("Thumbnail was too high.", height <= ImageService.MAX_DIMENSION);
	}

}
