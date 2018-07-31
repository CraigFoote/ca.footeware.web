/**
 * 
 */
package ca.footeware.web.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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
	 * Get the gallery page with names of the images to be dynamically obtained from
	 * images in images.path. The names are used by thymeleaf to construct the image
	 * links to the thumbnails and their full size versions.
	 * 
	 * @param model {@link Model}
	 * @return {@link String} name of thymeleaf template to pass model to for
	 *         rendering
	 */
	@GetMapping("/gallery")
	public String getGalleries(Model model) {
		List<String> galleries = new ArrayList<>();
		for (File file : service.getGalleries()) {
			galleries.add(file.getName());
		}
		model.addAttribute("galleries", galleries);
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
	 */
	@GetMapping("/gallery/{galleryName}")
	public String getGallery(@PathVariable String galleryName, Model model) {
		List<String> thumbs = new ArrayList<>();
		for (File file : service.getFiles(galleryName)) {
			thumbs.add(file.getName());
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
	 */
	@GetMapping(value = "/gallery/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable String galleryName, @PathVariable String imageName) {
		// Restrict the galleryName to letters and digits only
		if (!galleryName.matches("[\\sa-zA-Z0-9_-]++")) {
			return new byte[0];
		}
		return service.getImageAsBytes(galleryName, imageName);
	}

	/**
	 * Get the thumbnail version from the received gallery and image name.
	 * 
	 * @param galleryName {@link String} gallery name
	 * @param imageName        {@link String} image name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 */
	@GetMapping(value = "/gallery/thumbnails/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getThumbnail(@PathVariable String galleryName, @PathVariable String imageName) {
		return service.getThumbnailAsBytes(galleryName, imageName);
	}

}
