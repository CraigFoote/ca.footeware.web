/**
 *
 */
package ca.footeware.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Gets the 'Cookbook' page.
 *
 * @author Footeware.ca
 */
@Controller
public class ResourceController {

	/**
	 * Get the Webcam page.
	 *
	 * @return {@link String} the view
	 */
	@GetMapping("/gear")
	public String getGearPage() {
		return "gear";
	}

	/**
	 * Get the web page for logging in.
	 *
	 * @return {@link String}
	 */
	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}

	/**
	 * Get the webcam page.
	 *
	 * @return {@link String} the view
	 */
	@GetMapping("/webcam")
	public String getWebcamPage() {
		return "webcam";
	}
}
