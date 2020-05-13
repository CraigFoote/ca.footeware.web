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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Provides access to images.
 * 
 * @author Footeware.ca
 */
@Service
public class ImageService {

	/**
	 * 
	 */
	public static final int MAX_TN_DIMENSION = 150;
	/**
	 * 
	 */
	public static final int MAX_IMG_DIMENSION = 1920;
	private String imagesPath;
//	private ResourceLoader loader;

	/**
	 * Constructor
	 * 
	 * @param loader     {@link ResourceLoader} injected
	 * @param imagesPath {@link String} location on disk of images
	 */
	public ImageService(ResourceLoader loader, @Value("${images.path}") String imagesPath) {
//		this.loader = loader;
		this.imagesPath = imagesPath;
		ImageIO.setUseCache(true);
	}

	/**
	 * Determine the thumbnail width and height of the received image.
	 * 
	 * @param image {@link BufferedImage}
	 * @return {@link Dimension}
	 */
	private Dimension getTNDimensions(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Dimension dim = new Dimension();
		if (width == height) {
			// square
			dim.width = MAX_TN_DIMENSION;
			dim.height = MAX_TN_DIMENSION;
		} else if (width > height) {
			// landscape
			dim.width = MAX_TN_DIMENSION;
			float ratio = (float) height / width;
			dim.height = Math.round(ratio * MAX_TN_DIMENSION);
		} else {
			// portrait
			dim.height = MAX_TN_DIMENSION;
			float ratio = (float) width / height;
			dim.width = Math.round(ratio * MAX_TN_DIMENSION);
		}
		return dim;
	}
	
	/**
	 * Determine the display-size image width and height of the received image.
	 * 
	 * @param image {@link BufferedImage}
	 * @return {@link Dimension}
	 */
	private Dimension getImgDimensions(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Dimension dim = new Dimension();
		if (width == height) {
			// square
			dim.width = MAX_IMG_DIMENSION;
			dim.height = MAX_IMG_DIMENSION;
		} else if (width > height) {
			// landscape
			dim.width = MAX_IMG_DIMENSION;
			float ratio = (float) height / width;
			dim.height = Math.round(ratio * MAX_IMG_DIMENSION);
		} else {
			// portrait
			dim.height = MAX_IMG_DIMENSION;
			float ratio = (float) width / height;
			dim.width = Math.round(ratio * MAX_IMG_DIMENSION);
		}
		return dim;
	}

	/**
	 * Get all the files at the configured image path. These should be the gallery
	 * folders, each with image files.
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
	 * Get all the image files in the provided gallery by name.
	 * 
	 * @param galleryName {@link String}
	 * @return {@link File} array
	 */
	public File[] getFiles(String galleryName) {
		// Restrict the galleryName to letters and digits only
		if (!galleryName.matches("[\\sa-zA-Z0-9_-]++")) {
			return new File[0];
		}
		File folder = new File(imagesPath + File.separator + galleryName);
		if (!folder.isDirectory()) {
			return new File[0];
		}
		File[] files = folder.listFiles();
		List<File> imageFiles = new ArrayList<>();
		for (File file : files) {
			if (!file.isDirectory()) {
				imageFiles.add(file);
			}
		}
		Collections.sort(imageFiles, NameFileComparator.NAME_COMPARATOR);
		return imageFiles.toArray(new File[imageFiles.size()]);
	}

	/**
	 * Get the list of Gallery folders.
	 * 
	 * @return {@link File} array
	 */
	public File[] getGalleries() {
		File folder = new File(imagesPath);
		File[] files = folder.listFiles();
		List<File> galleries = new ArrayList<>();
		for (File file : files) {
			if (file.isDirectory()) {
				galleries.add(file);
			}
		}
		Collections.sort(galleries, NameFileComparator.NAME_COMPARATOR);
		return galleries.toArray(new File[galleries.size()]);
	}

	/**
	 * Get an image as bytes from provided gallery and image file name.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String}
	 * @return byte[] may be empty
	 */
	public byte[] getImageAsBytes(String galleryName, String imageName) {
		for (File file : getFiles(galleryName)) {
			if (file.getName().equals(imageName)) {
				try {
					BufferedImage originalImage = ImageIO.read(file);
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage thumbnail = resizeForDisplay(originalImage, type);
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
	 * Get the image by provided name sized as a thumbnail as a byte[].
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String} image file name
	 * @return byte[] may be empty
	 */
	public byte[] getThumbnailAsBytes(String galleryName, String imageName) {
		for (File file : getFiles(galleryName)) {
			if (file.getName().equals(imageName)) {
				try {
					BufferedImage originalImage = ImageIO.read(file);
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage thumbnail = resizeForThumbnail(originalImage, type);
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
	 * Resize the received image to its thumbnail size.
	 * 
	 * @param originalImage {@link BufferedImage}
	 * @param type          int
	 * See {@link BufferedImage#TYPE_INT_RGB}, etc.
	 * @return {@link BufferedImage}
	 */
	public BufferedImage resizeForThumbnail(BufferedImage originalImage, int type) {
		Dimension dim = getTNDimensions(originalImage);
		BufferedImage resizedImage = new BufferedImage(dim.width, dim.height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, dim.width, dim.height, null);
		g.dispose();
		return resizedImage;
	}
	
	/**
	 * Resize the received image to its display size.
	 * 
	 * @param originalImage {@link BufferedImage}
	 * @param type          int
	 * See {@link BufferedImage#TYPE_INT_RGB}, etc.
	 * @return {@link BufferedImage}
	 */
	public BufferedImage resizeForDisplay(BufferedImage originalImage, int type) {
		Dimension dim = getImgDimensions(originalImage);
		BufferedImage resizedImage = new BufferedImage(dim.width, dim.height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, dim.width, dim.height, null);
		g.dispose();
		return resizedImage;
	}

}
