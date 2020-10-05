/**
 * 
 */
package ca.footeware.web.tests;

import java.util.List;

import javax.management.ServiceNotFoundException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.services.JokeService;
import ca.footeware.web.services.NextSequenceService;

/**
 * Tests {@link JokeService}.
 * 
 * @author Footeware.ca
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class JokeServiceTests {

	private static final String TEST_BODY = "test body";
	private static final String TEST_TITLE = "test title?";

	@Autowired
	private JokeService jokeService;

	@Autowired
	private NextSequenceService seqService;

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#saveJoke(String, String, String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testSaveJoke() throws JokeException {
		String id = null;
		try {
			id = seqService.getNextSequence("customSequences");
		} catch (ServiceNotFoundException e) {
			Assert.fail("The call should have worked.");
		}
		final String finalId = id;
		jokeService.saveJoke(id, TEST_TITLE, TEST_BODY);
		Joke joke = jokeService.getById(id);
		Assert.assertEquals("Incorrect joke body.", TEST_BODY, joke.getBody());

		// null id
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(null, TEST_TITLE, TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "ID cannot be empty.", exception.getMessage());

		// empty string id
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke("", TEST_TITLE, TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "ID cannot be empty.", exception.getMessage());

		// whitespace id
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(" ", TEST_TITLE, TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "ID cannot be empty.", exception.getMessage());

		// null title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, null, TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// empty string title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, "", TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// whitespace title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, " ", TEST_BODY);
		});
		Assert.assertEquals("Incorrect exception.", "Title cannot be empty.", exception.getMessage());

		// null body
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, TEST_TITLE, null);
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());

		// empty string title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, TEST_TITLE, "");
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());

		// whitespace title
		exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, TEST_TITLE, " ");
		});
		Assert.assertEquals("Incorrect exception.", "Body cannot be empty.", exception.getMessage());
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
		String id = null;
		try {
			id = seqService.getNextSequence("customSequences");
		} catch (ServiceNotFoundException e) {
			Assert.fail("The call should have worked.");
		}
		final String finalId = id;
		JokeException exception = Assertions.assertThrows(JokeException.class, () -> {
			jokeService.saveJoke(finalId, arg, TEST_BODY);
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
		String id = null;
		try {
			id = seqService.getNextSequence("customSequences");
		} catch (ServiceNotFoundException e) {
			Assert.fail("The call should have worked.");
		}
		final String finalId = id;
		jokeService.saveJoke(finalId, TEST_TITLE, TEST_BODY);
		jokeService.deleteJoke(finalId);
		Joke deleted = jokeService.getById(finalId);
		Assert.assertNull("Joke body should have been null.", deleted);
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

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#getById(java.lang.String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testGetJokeById() throws JokeException {
		String id = null;
		try {
			id = seqService.getNextSequence("customSequences");
		} catch (ServiceNotFoundException e) {
			Assert.fail("The call should have worked.");
		}
		final String finalId = id;
		Joke joke = jokeService.getById(finalId);
		Assert.assertNull("Joke should have been deleted.", joke);
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

}
