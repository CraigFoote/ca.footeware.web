/**
 * 
 */
package ca.footeware.web.controllers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Footeware.ca
 *
 */
@Controller
public class ImageController {

	@Value("${images.path}")
	private String imagesPath;

	@Autowired
	private ResourceLoader loader;

	private static final int MAX_DIMENSION = 250;

	/**
	 * Get the gallery page with names of the images to be dynamically obtained from
	 * images in images.path. The names are used by thymeleaf to construct the image
	 * links to the thumbnails and their full size versions.
	 * 
	 * @param model
	 *            {@link Model}
	 * @return {@link String} name of thymeleaf template to pass model to for
	 *         rendering
	 */
	@GetMapping("/gallery")
	public String getGallery(Model model) {
		File folder = new File(imagesPath);
		File[] files = folder.listFiles();
		List<String> imageNames = new ArrayList<>();
		ImageIO.setUseCache(false);
		for (File file : files) {
			imageNames.add(file.getName());
		}
		model.addAttribute("images", imageNames);
		return "gallery";
	}

	/**
	 * Get the thumbnail version from the received image name.
	 * 
	 * @param imageName
	 *            {@link String} image name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 * @throws IOException
	 */
	@GetMapping(value = "/gallery/thumbnails/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getThumbnail(@PathVariable("imageName") String imageName) {
		File folder = new File(imagesPath);
		File[] files = folder.listFiles();
		fileLoop: for (File file : files) {
			if (file.getName().equals(imageName)) {
				try {
					BufferedImage originalImage = ImageIO.read(file);
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage thumbnail = resizeImage(originalImage, type);
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					ImageIO.write(thumbnail, "png", outstream);
					InputStream instream = new ByteArrayInputStream(outstream.toByteArray());
					return IOUtils.toByteArray(instream);
				} catch (IOException e) {
					break fileLoop;
				}
			}
		}
		return "not found".getBytes();
	}

	/**
	 * Get the full-size version from the received image name.
	 * 
	 * @param imageName
	 *            {@link String} image name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 */
	@GetMapping(value = "/gallery/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable("imageName") String imageName) {
		File folder = new File(imagesPath);
		File[] files = folder.listFiles();
		fileLoop: for (File file : files) {
			if (file.getName().equals(imageName)) {
				try {
					Resource resource = loader.getResource("file:" + file.getAbsolutePath());
					InputStream in = resource.getInputStream();
					return IOUtils.toByteArray(in);
				} catch (IOException e) {
					break fileLoop;
				}
			}
		}
		return "not found".getBytes();
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
	private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
		Dimension dim = getDimensions(originalImage);
		BufferedImage resizedImage = new BufferedImage(dim.width, dim.height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, dim.width, dim.height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Determine the thumbnail width and height of the received image.
	 * 
	 * @param image
	 *            {@link BufferedImage}
	 * @return {@link Dimension}
	 */
	private static Dimension getDimensions(BufferedImage image) {
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

}
