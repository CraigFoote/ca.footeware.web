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
import java.io.FileNotFoundException;
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
	public static final int MAX_IMG_DIMENSION = 1920;
	/**
	 * 
	 */
	public static final int MAX_TN_DIMENSION = 150;
	private String imagesPath;

	/**
	 * Constructor
	 * 
	 * @param imagesPath {@link String} location on disk of images, injected
	 */
	public ImageService(@Value("${images.path}") String imagesPath) {
		this.imagesPath = imagesPath;
		ImageIO.setUseCache(true);
	}

	/**
	 * COnvert provided image to byte array.
	 * 
	 * @param image {@link BufferedImage}
	 * @return byte array
	 * @throws IOException
	 */
	private byte[] convertToBytes(BufferedImage image) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", outstream);
		InputStream instream = new ByteArrayInputStream(outstream.toByteArray());
		return IOUtils.toByteArray(instream);
	}

	/**
	 * Determine the width and height of the received image.
	 * 
	 * @param image {@link BufferedImage}
	 * @param max   int maximum dimension, width or height
	 * @return {@link Dimension}
	 */
	public Dimension getDimensions(BufferedImage image, int max) {
		int width = image.getWidth();
		int height = image.getHeight();
		Dimension dim = new Dimension();
		if (width == height) {
			// square
			dim.width = max;
			dim.height = max;
		} else if (width > height) {
			// landscape
			dim.width = max;
			float ratio = (float) height / width;
			dim.height = Math.round(ratio * max);
		} else {
			// portrait
			dim.height = max;
			float ratio = (float) width / height;
			dim.width = Math.round(ratio * max);
		}
		return dim;
	}

	/**
	 * Get a file by provided name in provided gallery.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String}
	 * @return {@link File}
	 * @throws FileNotFoundException if no file is found by provided name in
	 *                               provided gallery
	 */
	private File getFileByName(String galleryName, String imageName) throws FileNotFoundException {
		for (File file : getFiles(galleryName)) {
			if (file.getName().equals(imageName)) {
				return file;
			}
		}
		throw new FileNotFoundException(galleryName + File.separator + imageName + " not found.");
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
	 * Get an image as bytes from provided gallery and image file names.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String}
	 * @return byte[] may be empty
	 */
	public byte[] getImageAsBytes(String galleryName, String imageName) {
		try {
			File file = getFileByName(galleryName, imageName);
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage image = resize(originalImage, type, getDimensions(originalImage, MAX_IMG_DIMENSION));
			return convertToBytes(image);
		} catch (IOException e) {
			// log
		}
		return new byte[0];
	}

	/**
	 * Get an image thumbnail as bytes from provided gallery and image file name.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String} image file name
	 * @return byte[] may be empty
	 */
	public byte[] getThumbnailAsBytes(String galleryName, String imageName) {
		try {
			File file = getFileByName(galleryName, imageName);
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage image = resize(originalImage, type, getDimensions(originalImage, MAX_TN_DIMENSION));
			return convertToBytes(image);
		} catch (IOException e) {
			// log
		}
		return new byte[0];
	}

	/**
	 * Resize the received image to provided size.
	 * 
	 * @param originalImage {@link BufferedImage}
	 * @param type          int See {@link BufferedImage#TYPE_INT_RGB}, etc.
	 * @param dim           {@link Dimension} height and width to resize to
	 * @return {@link BufferedImage}
	 */
	public BufferedImage resize(BufferedImage originalImage, int type, Dimension dim) {
		BufferedImage resizedImage = new BufferedImage(dim.width, dim.height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, dim.width, dim.height, null);
		g.dispose();
		return resizedImage;
	}

}
