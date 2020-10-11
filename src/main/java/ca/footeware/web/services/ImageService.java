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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.footeware.web.exceptions.ImageException;
import ca.footeware.web.models.Gallery;

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
	 * @throws ImageException if an images-related exception occurs.
	 */
	public ImageService(@Value("${images.path}") String imagesPath) throws ImageException {
		if (imagesPath == null || imagesPath.isEmpty()) {
			throw new ImageException("Images path cannot be empty.");
		}
		this.imagesPath = imagesPath;
		ImageIO.setUseCache(true);
	}

	/**
	 * Convert provided image to byte array.
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
	 * @throws ImageException        if an image-related exception occurs.
	 */
	private File getFileByName(String galleryName, String imageName) throws FileNotFoundException, ImageException {
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
	 * @throws ImageException if an image-related exception occurs.
	 */
	public File[] getFiles(String galleryName) throws ImageException {
		// Restrict the galleryName to letters and digits only
		if (!galleryName.matches("[\\sa-zA-Z0-9_-]++")) {
			throw new ImageException(
					"Invalid gallery name: " + galleryName + ". Must be spaces, a-z, A-Z, 0-9, underscores or dashes.");
		}
		File folder = new File(imagesPath + File.separator + galleryName);
		if (!folder.isDirectory()) {
			throw new ImageException(
					"Expected a folder at " + imagesPath + File.separator + galleryName + " but it wasn't one.");
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
	 * @throws ImageException if an image-related exception occurs.
	 */
	public List<Gallery> getGalleries() throws ImageException {
		File folder = new File(imagesPath);
		if (!folder.exists()) {
			throw new ImageException("Image path not found: " + imagesPath);
		}
		if (!folder.canRead()) {
			throw new ImageException("Image path cannot be read: " + imagesPath);
		}
		File[] files = folder.listFiles();
		List<Gallery> galleries = new ArrayList<>();
		for (File file : files) {
			Gallery gallery = new Gallery();
			gallery.setName(file.getName());
			if (file.isDirectory()) {
				gallery.setFolder(file);
				String folderName = file.getName();
				if (!folderName.matches("[\\sa-zA-Z0-9_-]++")) {
					throw new ImageException("Invalid gallery name: " + folderName
							+ ". Must be spaces, a-z, A-Z, 0-9, underscores or dashes.");
				}
				gallery.setSecret(checkSecret(file));
				galleries.add(gallery);
			}
		}
		Collections.sort(galleries, (o1, o2) -> o1.getName().compareTo(o2.getName()));

		return galleries;
	}

	/**
	 * Determines if the provided folder should be kept secret.
	 * 
	 * @param folder {@link File}
	 * @return boolean true if secret
	 */
	private boolean checkSecret(File folder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if ("secret".equals(file.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get an image as bytes from provided gallery and image file names.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String}
	 * @return byte[] may be empty
	 * @throws ImageException if an image-related exception occurs.
	 */
	public byte[] getImageAsBytes(String galleryName, String imageName) throws ImageException {
		try {
			File file = getFileByName(galleryName, imageName);
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage image = resize(originalImage, type, getDimensions(originalImage, MAX_IMG_DIMENSION));
			return convertToBytes(image);
		} catch (IOException e) {
			throw new ImageException(e.getLocalizedMessage());
		}
	}

	/**
	 * Get an image thumbnail as bytes from provided gallery and image file name.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String} image file name
	 * @return byte[] may be empty
	 * @throws ImageException if an image-related exception occurs.
	 */
	public byte[] getThumbnailAsBytes(String galleryName, String imageName) throws ImageException {
		try {
			File file = getFileByName(galleryName, imageName);
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage image = resize(originalImage, type, getDimensions(originalImage, MAX_TN_DIMENSION));
			return convertToBytes(image);
		} catch (IOException e) {
			throw new ImageException(e.getLocalizedMessage());
		}
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

	/**
	 * Get the EXIF from the provided file and convert it to a map of EXIF item
	 * labels and values.
	 * 
	 * @param file {@link File}
	 * @return {@link Map} of {@link String} label to {@link String} value
	 * @throws ImageException if shit goes south
	 */
	public Map<String, String> getExif(File file) throws ImageException {
		Map<String, String> map = new LinkedHashMap<>();
		if (!"secret".equals(file.getName())) {
			try {
				ImageMetadata metadata = Imaging.getMetadata(file);
				if (metadata != null) {
					List<? extends ImageMetadataItem> items = metadata.getItems();
					for (ImageMetadataItem item : items) {
						String[] split = item.toString().split(": ");
						map.put(split[0], split[1]);
					}
				}
			} catch (ImageReadException | IOException e) {
				throw new ImageException(e.getLocalizedMessage());
			}
		}
		return map;
	}

}
