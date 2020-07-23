/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
/**
 * 
 */
package ca.footeware.web.controllers;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.services.JokeService;

/**
 * Exposes joke-related endpoints.
 * 
 * @author Footeware.ca
 */
@Controller
public class JokeController {

	private static final String JOKES = "jokes";
	private static final String TITLES = "titles";
	private static final String TITLE = "title";
	private JokeService service;

	/**
	 * Constructor.
	 * 
	 * @param service {@link JokeService} injected
	 * @throws JokeException if shit goes south
	 */
	public JokeController(JokeService service) throws JokeException {
		if (service == null) {
			throw new JokeException("Joke service not found");
		}
		this.service = service;
	}

	/**
	 * Delete a joke by title.
	 * 
	 * @param title {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/deletejoke/{title}")
	public String deleteJoke(@PathVariable String title, Model model) throws JokeException {
		service.deleteJoke(title);
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}

	/**
	 * Forward user to page that allows them to add a joke.
	 * 
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/addjoke")
	public String getAddJokePage(Model model) {
		return "addjoke";
	}

	/**
	 * Get a joke by it's title.
	 * 
	 * @param title {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/jokes/{title}")
	public String getJoke(@PathVariable String title, Model model) throws JokeException {
		String body = service.getJokeByTitle(title);
		if (body == null) {
			return getTitles(model);
		}
		model.addAttribute(TITLE, title);
		model.addAttribute("body", body);
		return "joke";
	}

	/**
	 * Get the titles of all the jokes.
	 * 
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/jokes")
	public String getTitles(Model model) throws JokeException {
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}

	/**
	 * Add a joke.
	 * 
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@PostMapping("/jokes/add")
	public String postJoke(@RequestParam String title, @RequestParam String body, Model model) throws JokeException {
		String existing = service.getJokeByTitle(title);
		if (existing != null) {
			model.addAttribute("error", "A joke by that title exists. Please choose another.");
			model.addAttribute(TITLE, title);
			model.addAttribute("body", body);
			return "addjoke";
		}
		service.createJoke(title, body);
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}

	/**
	 * Get a joke by it's title and forward it to the 'edit' page.
	 * 
	 * @param title {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/editjoke/{title}")
	public String editJoke(@PathVariable String title, Model model) throws JokeException {
		String existing = service.getJokeByTitle(title);
		model.addAttribute(TITLE, title);
		model.addAttribute("body", existing);
		return "editjoke";
	}

	/**
	 * Edit a joke.
	 * 
	 * @param title         {@link String}
	 * @param originalTitle {@link String}
	 * @param body          {@link String}
	 * @param model         {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@PostMapping("/jokes/edit")
	public String postJoke(@RequestParam String title, @RequestParam String originalTitle, @RequestParam String body,
			Model model) throws JokeException {
		service.deleteJoke(originalTitle);
		service.createJoke(title, body);
		Set<String> titles = service.getTitles();
		model.addAttribute(TITLES, titles);
		return JOKES;
	}
}
