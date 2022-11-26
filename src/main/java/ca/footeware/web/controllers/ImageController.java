/**
 *
 */
package ca.footeware.web.controllers;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.footeware.web.exceptions.ImageException;
import ca.footeware.web.services.ImageService;

/**
 * Exposes image-related endpoints.
 *
 * @author Footeware.ca
 */
@Controller
public class ImageController {

	private ImageService service;

	/**
	 * Constructor.
	 *
	 * @param service {@link ImageService} injected
	 */
	public ImageController(ImageService service) {
		this.service = service;
	}

	/**
	 * Create a line-feed-delimited String of specific EXIF item labels and values.
	 *
	 * @param name
	 * @param exif
	 * @return {@link String}
	 */
	private String compileExifString(String name, Map<String, String> exif) {
		var b = new StringBuilder();
		b.append("Name: " + name + "\n");
		b.append("Model: " + exif.get("Model") + "\n");
		b.append("ProcessingSoftware: " + exif.get("ProcessingSoftware") + "\n");
		b.append("DateTime: " + exif.get("DateTime") + "\n");
		b.append("ExposureTime: " + exif.get("ExposureTime") + "\n");
		b.append("FNumber: " + exif.get("FNumber") + "\n");
		b.append("PhotographicSensitivity: " + exif.get("PhotographicSensitivity") + "\n");
		b.append("ExposureCompensation: " + exif.get("ExposureCompensation") + "\n");
		b.append("FocalLength: " + exif.get("FocalLength") + "\n");
		b.append("FocalLengthIn35mmFormat: " + exif.get("FocalLengthIn35mmFormat"));
		return b.toString();
	}

	/**
	 * Get the gallery page with names of the images to be dynamically obtained from
	 * images in images.path. The names are used by thymeleaf to construct the image
	 * links to the thumbnails and their full size versions.
	 *
	 * @param model {@link Model}
	 * @return {@link String} name of thymeleaf template to pass model to for
	 *         rendering
	 * @throws ImageException if an image-related exception occurs.
	 */
	@GetMapping("/gallery")
	public String getGalleries(Model model) throws ImageException {
		model.addAttribute("galleries", service.getGalleries());
		return "gallery";
	}

	/**
	 * Get the gallery page by name with names of the images to be dynamically
	 * obtained from images in images.path. The names are used by thymeleaf to
	 * construct the image links to the thumbnails and their full size versions.
	 *
	 * @param galleryName {@link String}
	 * @param model       {@link Model}
	 * @return {@link String} UI template name
	 * @throws ImageException if an image-related exception occurs.
	 */
	@GetMapping("/gallery/{galleryName}")
	public String getGallery(@PathVariable String galleryName, Model model) throws ImageException {
		Map<String, String> thumbs = new LinkedHashMap<>();
		for (File file : service.getFiles(galleryName)) {
			Map<String, String> exif = service.getExif(file);
			String name = file.getName();
			if ("secret".equals(name)) {
				break;
			} else if (exif != null) {
				thumbs.put(name, compileExifString(name, exif));
			} else {
				thumbs.put(name, "");
			}
		}
		model.addAttribute("thumbs", thumbs);
		model.addAttribute("galleryName", galleryName);
		return "gallery";
	}

	/**
	 * Get the full-size version from the received gallery and image name.
	 *
	 * @param galleryName {@link String} gallery name
	 * @param imageName   {@link String} image file name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 * @throws ImageException when an image-related exception occurs
	 */
	@GetMapping(value = "/gallery/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable String galleryName, @PathVariable String imageName) throws ImageException {
		// Restrict the galleryName to letters and digits only
		if (!galleryName.matches("[\\sa-zA-Z0-9_-]++")) {
			throw new ImageException(
					"Invalid gallery name:" + galleryName + ". Must be spaces, a-z, A-Z, 0-9, underscore or dashes.");
		}
		return service.getImageAsBytes(galleryName, imageName);
	}

	/**
	 * Get the thumbnail version from the received gallery and image name.
	 *
	 * @param galleryName {@link String} gallery name
	 * @param imageName   {@link String} image name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 * @throws ImageException if an image-related exception occurs.
	 */
	@GetMapping(value = "/gallery/thumbnails/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getThumbnail(@PathVariable String galleryName, @PathVariable String imageName) throws ImageException {
		return service.getThumbnailAsBytes(galleryName, imageName);
	}
}
