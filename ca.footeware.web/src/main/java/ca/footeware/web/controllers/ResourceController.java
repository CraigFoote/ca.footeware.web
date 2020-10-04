/**
 * 
 */
package ca.footeware.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Footeware.ca
 *
 */
@Controller
public class ResourceController {

	/**
	 * Get the Cookbook page.
	 * 
	 * @return {@link String} the view
	 */
	@GetMapping("/cookbook")
	public String getCookbookPage() {
		return "cookbook";
	}
}
