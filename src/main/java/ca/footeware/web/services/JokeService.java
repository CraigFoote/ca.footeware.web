/**
 * 
 */
package ca.footeware.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	public static final String BODY_ERROR = "Body cannot be empty.";
	/**
	 * 
	 */
	public static final String ID_ERROR = "Id cannot be empty.";

	/**
	 * Create a new joke using provided id, title and body.
	 * 
	 * @param id    {@link String}
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @return {@link Joke}
	 * @throws JokeException if shit goes south
	 */
	public Joke saveJoke(String id, String title, String body) throws JokeException {
		if (id == null || id.isBlank() || id.isEmpty()) {
			throw new JokeException(ID_ERROR);
		}
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException(TITLE_ERROR);
		}
		if (body == null || body.isBlank() || body.isEmpty()) {
			throw new JokeException(BODY_ERROR);
		}
		Joke joke = id == null ? new Joke(title, body) : jokeRepository.getById(id);
		joke.setTitle(title);
		joke.setBody(body);
		return jokeRepository.save(joke);
	}

	/**
	 * Save joke using provided title and body. This is for updating the joke while
	 * maintaining ID.
	 * 
	 * @param title {@link String}
	 * @param body  {@link String}
	 * @return {@link Joke}
	 * @throws JokeException if shit goes south
	 */
	public Joke saveJoke(String title, String body) throws JokeException {
		if (title == null || title.isBlank() || title.isEmpty()) {
			throw new JokeException(TITLE_ERROR);
		}
		if (body == null || body.isBlank() || body.isEmpty()) {
			throw new JokeException("Body cannot be empty.");
		}
		Joke joke = new Joke(title, body);
		joke.setTitle(title);
		joke.setBody(body);
		return jokeRepository.save(joke);
	}

	/**
	 * Find a joke with provided id and delete it.
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
	 * Get the joke titles.
	 * 
	 * @return {@link List} of {@link Joke}
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

}
