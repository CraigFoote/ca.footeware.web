/**
 * 
 */
package ca.footeware.web.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.repositories.JokeRepository;

/**
 * Provides access to jokes.
 * 
 * @author Footeware.ca
 */
@Service
public class JokeService {

	@Autowired
	private JokeRepository jokeRepository;

	@Autowired
	private NextSequenceService seqService;

	/**
	 * 
	 */
	public static final String JOKE_BODY = "A newfie rolls into his factory job at 10:30. The floor manager comes up to him and says, \"You should have been here at nine o'clock,\" to which the newfie responds \"Why, what happened?\"";
	/**
	 * 
	 */
	public static final String JOKE_TITLE = "Nine o'clock";
	/**
	 * 
	 */
	public static final String TITLE_ERROR = "Title cannot be empty.";
	/**
	 * 
	 */
	public static final String MAP_ERROR = "Unknown error, map not found.";

	/**
	 * Create a new joke using provided id, title and body.
	 * 
	 * @param id    {@link String}
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @throws JokeException if shit goes south
	 */
	public void saveJoke(String id, String title, String body) throws JokeException {
		if (id == null || id.isBlank() || id.isEmpty()) {
			throw new JokeException("ID cannot be empty.");
		}
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException(TITLE_ERROR);
		}
		if (body == null || body.isBlank() || body.isEmpty()) {
			throw new JokeException("Body cannot be empty.");
		}
		jokeRepository.save(new Joke(id, title, body));
	}

	/**
	 * Find a joke with provided title and delete it.
	 * 
	 * @param id {@link String}
	 * @throws JokeException if shit goes south
	 */
	public void deleteJoke(String id) throws JokeException {
		if (id == null || id.isBlank() || id.isEmpty()) {
			throw new JokeException("ID cannot be empty.");
		}
		jokeRepository.deleteById(id);
	}

	/**
	 * Get a joke by its title.
	 * 
	 * @param title {@link String}
	 * @return {@link String} the joke body, may be null
	 * @throws JokeException if shit goes south
	 */
	public Set<Joke> getJokesByTitle(String title) throws JokeException {
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException(TITLE_ERROR);
		}
		Set<Joke> jokes = jokeRepository.getByTitle(title);
		return jokes;
	}

	/**
	 * Get the joke titles.
	 * 
	 * @return {@link Set} of {@link String}
	 * @throws JokeException if shit goes south
	 */
	public List<Joke> getJokes() throws JokeException {
		return jokeRepository.findAll();
	}

	/**
	 * Get a joke matching provided ID.
	 * 
	 * @param id {@link String}
	 * @return {@link Joke}
	 */
	public Joke getById(String id) {
		return jokeRepository.getById(id);
	}

	/**
	 * Initializes the DB.
	 * 
	 * @author Footeware.ca
	 */
	@Bean
	CommandLineRunner init() {
		return args -> {
			jokeRepository.save(new Joke(seqService.getNextSequence("customSequences"), JOKE_TITLE, JOKE_BODY));
		};
	}

}
