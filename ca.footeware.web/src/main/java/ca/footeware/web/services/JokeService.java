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
import org.springframework.stereotype.Service;

/**
 * Provides access to jokes.
 * 
 * @author Footeware.ca
 */
@Service
public class JokeService {

	public static final String JOKE_TITLE = "Nine o'clock";
	public static final String JOKE_BODY = "A newfie rolls into his factory job at 10:30. The floor manager comes up to him and says, \"You should have been here at nine o'clock,\" to which the newfie responds \"Why, what happened?\"";
	private DB db;
	private ConcurrentMap<String, String> map;

	/**
	 * Get the joke titles.
	 * 
	 * @return {@link Set} of {@link String}
	 */
	public Set<String> getTitles() {
		return getMap().keySet();
	}

	/**
	 * Get a joke by its title.
	 * 
	 * @param title
	 *            {@link String}
	 * @return {@link String} the joke body, may be null
	 */
	public String getJokeByTitle(String title) {
		return getMap().get(title);
	}

	/**
	 * Create a new joke using provided title and body.
	 * 
	 * @param title
	 *            {@link String}
	 * @param body
	 *            {@link String}
	 */
	public void createJoke(String title, String body) {
		getMap().put(title, body);
		getDB().commit();
	}

	/**
	 * Find a joke with provided title and delete it.
	 * 
	 * @param title
	 *            {@link String}
	 */
	public void deleteJoke(String title) {
		getMap().remove(title);
		getDB().commit();
	}

	/**
	 * Initialize the DB.
	 */
	private void init() {
		db = DBMaker.fileDB(new File("file.db")).closeOnJvmShutdown().fileMmapEnable().concurrencyDisable().fileLockDisable().make();
		map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
		map.put(JOKE_TITLE, JOKE_BODY);
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

}
