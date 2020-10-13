/**
 * 
 */
package ca.footeware.web.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
	void TestCreateJokeWithBadTitle(String arg) {
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(arg, TEST_BODY);
		});
		String message = exception.getMessage();
		Assert.assertEquals("Incorrect exception message.", JokeService.TITLE_ERROR, message);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testDeleteJoke() throws JokeException {
		Joke joke = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		jokeService.deleteJoke(joke.getId());
		Joke deleted = jokeService.getById(joke.getId());
		Assert.assertNull("Joke should have been null.", deleted);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 */
	@Test
	public void testDeleteJokeNotExists() {
		try {
			jokeService.deleteJoke("bob");
		} catch (JokeException e) {
			Assert.fail("No exception is expected in this call.");
		}
	}

	@Test
	public void testGetJokeById() throws JokeException {
		Joke joke1 = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		Joke joke2 = jokeService.getById(joke1.getId());
		Assert.assertEquals("IDs should be the same", joke1.getId(), joke2.getId());
		Assert.assertEquals("Titles should be the same", joke1.getTitle(), joke2.getTitle());
		Assert.assertEquals("Bodies should be the same", joke1.getBody(), joke2.getBody());
	}

	/**
	 * Test method for {@link ca.footeware.web.services.JokeService#getJokes()}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testGetJokes() throws JokeException {
		List<Joke> jokes = jokeService.getJokes();
		Assert.assertNotNull(jokes);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(String, String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testSaveJoke() throws JokeException {
		Joke newJoke = jokeService.saveJoke(TEST_TITLE, TEST_BODY);
		Joke savedJoke = jokeService.getById(newJoke.getId());
		Assert.assertEquals("Incorrect joke body.", newJoke.getId(), savedJoke.getId());

		// null title
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(null, TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// empty string title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke("", TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// whitespace title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(null, " ", TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// null body
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(TEST_TITLE, null);
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());

		// empty string title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(TEST_TITLE, "");
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());

		// whitespace body
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(TEST_TITLE, " ");
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());
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
		String message = exception.getMessage();
		Assert.assertEquals("Incorrect exception message.", JokeService.BODY_ERROR, message);
	}

}
