/**
 * 
 */
package ca.footeware.web.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ImageService service;

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
		List<String> imageNames = new ArrayList<>();
		for (File file : service.getFiles()) {
			imageNames.add(file.getName());
		}
		model.addAttribute("images", imageNames);
		return "gallery";
	}

	/**
	 * Get the thumbnail version from the received image name.
	 * 
	 * @param name
	 *            {@link String} image name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 */
	@GetMapping(value = "/gallery/thumbnails/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getThumbnail(@PathVariable String name) {
		return service.getThumbnailAsBytes(name);
	}

	/**
	 * Get the full-size version from the received image name.
	 * 
	 * @param name
	 *            {@link String} image file name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 */
	@GetMapping(value = "/gallery/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable String name) {
		return service.getImageAsBytes(name);
	}

}
