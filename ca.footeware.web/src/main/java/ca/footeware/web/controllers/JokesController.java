/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
/**
 * 
 */
package ca.footeware.web.controllers;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Exposes joke-related endpoints.
 * 
 * @author Footeware.ca
 */
@Controller
public class JokesController {

	private DB db;
	private ConcurrentMap<String, String> map;

	/**
	 * Initialize the DB.
	 */
	private void init() {
		db = DBMaker.fileDB(new File("file.db")).closeOnJvmShutdown().fileMmapEnable().make();
		map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
		map.put("Nine o'clock",
				"A newfie rolls into his factory job at 10:30. The floor manager comes up to him and says, \"You should have been here at nine o'clock,\" to which the newfie responds \"Why, what happened?\"");
		db.commit();
	}

	/**
	 * Get an initialized map from the database.
	 * 
	 * @return {@link ConcurrentMap} of {@link String} to {@link String}
	 */
	private ConcurrentMap<String, String> getMap() {
		if (map == null) {
			init();
		}
		return map;
	}

	/**
	 * Get an initialized DB.
	 * 
	 * @return {@link DB}
	 */
	private DB getDB() {
		if (db == null) {
			init();
		}
		return db;
	}

	/**
	 * Get the titles of all the jokes.
	 * 
	 * @param model
	 *            {@link Model}
	 * @return {@link String} UI view
	 */
	@GetMapping("/jokes")
	public String getTitles(Model model) {
		Set<String> titles = getMap().keySet();
		model.addAttribute("titles", titles);
		return "jokes";
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
		String body = getMap().get(title);
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
		String existing = getMap().get(title);
		if (existing != null) {
			model.addAttribute("error", "A joke by that title exists. Please choose another.");
			model.addAttribute("title", title);
			model.addAttribute("body", body);
			return "addjoke";
		}
		getMap().put(title, body);
		getDB().commit();
		Set<String> titles = getMap().keySet();
		model.addAttribute("titles", titles);
		return "jokes";
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
		getMap().remove(title);
		getDB().commit();
		Set<String> titles = getMap().keySet();
		model.addAttribute("titles", titles);
		return "jokes";
	}
}
