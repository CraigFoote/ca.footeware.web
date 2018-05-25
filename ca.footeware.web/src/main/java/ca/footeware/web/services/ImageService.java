/**
 * 
 */
package ca.footeware.web.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Provides access to images.
 * 
 * @author Footeware.ca
 */
@Service
public class ImageService {

	public static final int MAX_DIMENSION = 150;
	private String imagesPath;
	private ResourceLoader loader;

	/**
	 * Constructor.
	 * 
	 * @param loader
	 *            {@link ResourceLoader} injected.
	 */
	public ImageService(ResourceLoader loader, @Value("${images.path}") String imagesPath) {
		this.loader = loader;
		this.imagesPath = imagesPath;
		ImageIO.setUseCache(false);
	}

	/**
	 * Determine the thumbnail width and height of the received image.
	 * 
	 * @param image
	 *            {@link BufferedImage}
	 * @return {@link Dimension}
	 */
	private Dimension getDimensions(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Dimension dim = new Dimension();
		if (width == height) {
			// square
			dim.width = MAX_DIMENSION;
			dim.height = MAX_DIMENSION;
		} else if (width > height) {
			// landscape
			dim.width = MAX_DIMENSION;
			float ratio = (float) height / width;
			dim.height = Math.round(ratio * MAX_DIMENSION);
		} else {
			// portrait
			dim.height = MAX_DIMENSION;
			float ratio = (float) width / height;
			dim.width = Math.round(ratio * MAX_DIMENSION);
		}
		return dim;
	}

	/**
	 * Get all the files at the configured image path.
	 * 
	 * @return {@link File}[]
	 */
	public File[] getFiles() {
		File folder = new File(imagesPath);
		File[] files = folder.listFiles();
		Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
		return files;
	}

	/**
	 * Get the image by provided name as a byte[].
	 * 
	 * @param name
	 *            {@link String} image file name
	 * @return byte[] may be empty
	 */
	public byte[] getImageAsBytes(String name) {
		for (File file : getFiles()) {
			if (file.getName().equals(name)) {
				Resource resource = loader.getResource("file:" + file.getAbsolutePath());
				try {
					InputStream in = resource.getInputStream();
					return IOUtils.toByteArray(in);
				} catch (IOException e) {
					break;
				}
			}
		}
		return new byte[0];
	}

	/**
	 * Get the image by provided name sized as a thumbnail as a byte[].
	 * 
	 * @param name
	 *            {@link String} image file name
	 * @return byte[] may be empty
	 */
	public byte[] getThumbnailAsBytes(String name) {
		for (File file : getFiles()) {
			if (file.getName().equals(name)) {
				try {
					BufferedImage originalImage = ImageIO.read(file);
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage thumbnail = resizeImage(originalImage, type);
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					ImageIO.write(thumbnail, "jpg", outstream);
					InputStream instream = new ByteArrayInputStream(outstream.toByteArray());
					return IOUtils.toByteArray(instream);
				} catch (IOException e) {
					break;
				}
			}
		}
		return new byte[0];
	}

	/**
	 * Resize the received image.
	 * 
	 * @param originalImage
	 *            {@link BufferedImage}
	 * @param type
	 *            int
	 * @see {@link BufferedImage#TYPE_INT_RGB}, etc.
	 * @return {@link BufferedImage}
	 */
	public BufferedImage resizeImage(BufferedImage originalImage, int type) {
		Dimension dim = getDimensions(originalImage);
		BufferedImage resizedImage = new BufferedImage(dim.width, dim.height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, dim.width, dim.height, null);
		g.dispose();
		return resizedImage;
	}

}
