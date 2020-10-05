/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
/**
 * 
 */
package ca.footeware.web.controllers;

import java.util.List;

import javax.management.ServiceNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.services.JokeService;
import ca.footeware.web.services.NextSequenceService;

/**
 * Exposes joke-related endpoints.
 * 
 * @author Footeware.ca
 */
@Controller
public class JokeController {

	private static final String JOKE = "joke";
	private static final String JOKES = "jokes";
	private JokeService jokeService;
	private NextSequenceService seqService;

	/**
	 * Constructor.
	 * 
	 * @param jokeService {@link JokeService} injected
	 * @param seqService  {@link NextSequenceService}
	 * @throws JokeException if shit goes south
	 */
	public JokeController(JokeService jokeService, NextSequenceService seqService) throws JokeException {
		if (jokeService == null) {
			throw new JokeException("Joke service not found");
		}
		if (seqService == null) {
			throw new JokeException("Sequence service not found");
		}
		this.jokeService = jokeService;
		this.seqService = seqService;
	}

	/**
	 * Delete a joke by title.
	 * 
	 * @param title {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/jokes/delete/{title}")
	public String deleteJoke(@PathVariable String title, Model model) throws JokeException {
		jokeService.deleteJoke(title);
		List<Joke> jokes = jokeService.getJokes();
		model.addAttribute(JOKES, jokes);
		return JOKES;
	}

	/**
	 * Get a joke by its id and forward it to the 'edit' page.
	 * 
	 * @param id    {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/jokes/edit/{id}")
	public String editJoke(@PathVariable String id, Model model) throws JokeException {
		Joke joke = jokeService.getById(id);
		model.addAttribute(JOKE, joke);
		return "editjoke";
	}

	/**
	 * Forward user to page that allows them to add a joke.
	 * 
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/jokes/add")
	public String getAddJokePage(Model model) {
		return "addjoke";
	}

	/**
	 * Get a joke by its ID.
	 * 
	 * @param id    {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@GetMapping("/jokes/{id}")
	public String getJoke(@PathVariable String id, Model model) throws JokeException {
		model.addAttribute(JOKE, jokeService.getById(id));
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
	public String getJokes(Model model) throws JokeException {
		model.addAttribute(JOKES, jokeService.getJokes());
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
	 * @throws ServiceNotFoundException if shit goes back north
	 */
	@PostMapping("/jokes/add")
	public String postJoke(@RequestParam String title, @RequestParam String body, Model model) throws JokeException, ServiceNotFoundException {
		jokeService.saveJoke(seqService.getNextSequence("customSequences"), title, body);
		model.addAttribute(JOKES, jokeService.getJokes());
		return JOKES;
	}

	/**
	 * Edit a joke.
	 * 
	 * @param id    {@link String}
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @param model {@link Model}
	 * @return {@link String} UI view
	 * @throws JokeException if shit goes south
	 */
	@PostMapping("/jokes/edit")
	public String postEditedJoke(@RequestParam String id, @RequestParam String title, @RequestParam String body,
			Model model) throws JokeException {
		jokeService.saveJoke(id, title, body);
		model.addAttribute(JOKES, jokeService.getJokes());
		return JOKES;
	}
}
