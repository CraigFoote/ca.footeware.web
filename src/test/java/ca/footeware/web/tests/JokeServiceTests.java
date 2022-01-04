/**
 *
 */
package ca.footeware.web.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.services.JokeService;

/**
 * Tests {@link JokeService}.
 *
 * @author Footeware.ca
 */
@SpringBootTest
class JokeServiceTests {

	private static final String TEST_BODY = "test body";
	private static final String TEST_TITLE = "test title?";

	@Autowired
	private JokeService jokeService;

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @param arg {@link String}
	 */
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void TestCreateJokeWithBadBody(String arg) {
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(TEST_TITLE, arg);
		});
		String message = exception.getMessage();
		Assertions.assertEquals(JokeService.BODY_ERROR, message, "Incorrect exception message.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @param arg {@link String}
	 */
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void TestCreateJokeWithBadTitle(String arg) {
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(arg, TEST_BODY);
		});
		String message = exception.getMessage();
		Assertions.assertEquals(JokeService.TITLE_ERROR, message, "Incorrect exception message.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 *
	 * @throws JokeException if shit goes south
	 */
	@Test
	void testDeleteJoke() throws JokeException {
		Joke joke = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		jokeService.deleteJoke(joke.getId());
		Joke deleted = jokeService.getById(joke.getId());
		Assertions.assertNull(deleted);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 */
	@Test
	void testDeleteJokeNotExists() {
		try {
			jokeService.deleteJoke("bob");
		} catch (JokeException e) {
			Assertions.fail("No exception is expected in this call. " + e.getMessage());
		}
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void testDeleteWithBadId(String arg) {
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.deleteJoke(arg);
		});
		Assertions.assertEquals(JokeService.ID_ERROR, exception.getMessage(), "Wrong exception message.");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void testGetJokeByIdWithBadId(String arg) {
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.getById(arg);
		});
		Assertions.assertEquals(JokeService.ID_ERROR, exception.getMessage(), "Wrong exception message.");
	}

	@Test
	void testGetJokeById() throws JokeException {
		Joke joke1 = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		Joke joke2 = jokeService.getById(joke1.getId());
		Assertions.assertEquals(joke1.getId(), joke2.getId(), "IDs should be the same");
		Assertions.assertEquals(joke1.getTitle(), joke2.getTitle(), "Titles should be the same");
		Assertions.assertEquals(joke1.getBody(), joke2.getBody(), "Bodies should be the same");
	}

	/**
	 * Test method for {@link ca.footeware.web.services.JokeService#getJokes()}.
	 *
	 * @throws JokeException if shit goes south
	 */
	@Test
	void testGetJokes() throws JokeException {
		List<Joke> jokes = jokeService.getJokes();
		Assertions.assertNotNull(jokes);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(String, String)}.
	 *
	 * @param arg {@link String}
	 *
	 * @throws JokeException if shit goes south
	 */
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void testSaveJoke(String arg) throws JokeException {
		Joke newJoke = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		Joke savedJoke = jokeService.getById(newJoke.getId());
		Assertions.assertEquals(newJoke.getId(), savedJoke.getId(), "Incorrect joke id.");

		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(arg, TEST_BODY);
		});
		Assertions.assertFalse(exception.getMessage().isEmpty(), "Incorrect exception.");

		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(TEST_TITLE, arg);
		});
		Assertions.assertFalse(exception.getMessage().isEmpty(), "Incorrect exception.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(String, String, String)}
	 *
	 * @throws JokeException if shit goes south
	 */
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\n", "\t" })
	void testSaveWithIdWithBadBody(String arg) throws JokeException {
		Joke savedJoke = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		String id = savedJoke.getId();

		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(id, TEST_TITLE, arg);
		});
		Assertions.assertEquals(JokeService.BODY_ERROR, exception.getMessage(), "Incorrect exception message.");
	}

}
