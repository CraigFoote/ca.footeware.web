/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
/**
 * 
 */
package ca.footeware.web.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.footeware.web.services.JokeService;

/**
 * Exposes joke-related endpoints.
 * 
 * @author Footeware.ca
 */
@Controller
public class JokeController {

	private static final String TITLES = "titles";
	private static final String JOKES = "jokes";

	@Autowired
	private JokeService service;

	/**
	 * Get the titles of all the jokes.
	 * 
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/jokes")
	public String getTitles(Model model) {
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}

	/**
	 * Get a joke by it's title.
	 * 
	 * @param title
	 *            {@link String}
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/jokes/{title}")
	public String getJoke(@PathVariable String title, Model model) {
		String body = service.getJokeByTitle(title);
		model.addAttribute("title", title);
		model.addAttribute("body", body);
		return "joke";
	}

	/**
	 * Forward user to page that allows them to add a joke.
	 * 
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/addjoke")
	public String getAddJokePage(Model model) {
		return "addjoke";
	}

	/**
	 * Add a joke.
	 * 
	 * @param title
	 *            {@link String}
	 * @param body
	 *            {@link String}
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@PostMapping("/jokes/add")
	public String postJoke(@RequestParam String title, @RequestParam String body, Model model) {
		String existing = service.getJokeByTitle(title);
		if (existing != null) {
			model.addAttribute("error", "A joke by that title exists. Please choose another.");
			model.addAttribute("title", title);
			model.addAttribute("body", body);
			return "addjoke";
		}
		service.createJoke(title, body);
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}

	/**
	 * Delete a joke by title.
	 * 
	 * @param title
	 *            {@link String}
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/deletejoke/{title}")
	public String deleteJoke(@PathVariable String title, Model model) {
		service.deleteJoke(title);
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}
}
