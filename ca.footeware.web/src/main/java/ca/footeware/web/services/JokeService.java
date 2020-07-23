/**
 * 
 */
package ca.footeware.web.services;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import ca.footeware.web.exceptions.JokeException;

/**
 * Provides access to jokes.
 * 
 * @author Footeware.ca
 */
@Service
public class JokeService {

	/**
	 * 
	 */
	public static final String JOKE_BODY = "A newfie rolls into his factory job at 10:30. The floor manager comes up to him and says, \"You should have been here at nine o'clock,\" to which the newfie responds \"Why, what happened?\"";
	/**
	 * 
	 */
	public static final String JOKE_TITLE = "Nine o'clock";
	private DB db;
	private ConcurrentMap<String, String> map;

	/**
	 * Create a new joke using provided title and body.
	 * 
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @throws JokeException if shit goes south
	 */
	public void createJoke(String title, String body) throws JokeException {
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException("Title cannot be empty.");
		}
		if (body == null || body.isBlank() || body.isEmpty()) {
			throw new JokeException("Body cannot be empty.");
		}
		if (map == null) {
			throw new JokeException("Unknown error, map not found.");
		}
		map.put(title, body);
		if (db == null) {
			throw new JokeException("Unknown error, database not found.");
		}
		db.commit();
	}

	/**
	 * Find a joke with provided title and delete it.
	 * 
	 * @param title {@link String}
	 * @throws JokeException if shit goes south
	 */
	public void deleteJoke(String title) throws JokeException {
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException("Title cannot be empty.");
		}
		if (map == null) {
			throw new JokeException("Unknown error, map not found.");
		}
		map.remove(title);
		if (db == null) {
			throw new JokeException("Unknown error, database not found.");
		}
		db.commit();
	}

	/**
	 * Get a joke by its title.
	 * 
	 * @param title {@link String}
	 * @return {@link String} the joke body, may be null
	 * @throws JokeException if shit goes south
	 */
	public String getJokeByTitle(String title) throws JokeException {
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException("Title cannot be empty.");
		}
		if (map == null) {
			throw new JokeException("Unknown error, map not found.");
		}
		return map.get(title);
	}

	/**
	 * Get the joke titles.
	 * 
	 * @return {@link Set} of {@link String}
	 * @throws JokeException if shit goes south
	 */
	public Set<String> getTitles() throws JokeException {
		if (map == null) {
			throw new JokeException("Unknown error, map not found.");
		}
		return map.keySet();
	}

	/**
	 * Initializes the DB.
	 * 
	 * @author Footeware.ca
	 */
	@Bean
	CommandLineRunner init() {
		return args -> {
			db = DBMaker.fileDB(new File("file.db")).closeOnJvmShutdown().fileMmapEnable().concurrencyDisable()
					.fileLockDisable().make();
			map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
			map.put(JOKE_TITLE, JOKE_BODY);
			db.commit();
		};
	}

}
