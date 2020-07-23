/**
 * 
 */
package ca.footeware.web.tests;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.services.JokeService;

/**
 * Tests {@link JokeService}.
 * 
 * @author Footeware.ca
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JokeServiceTests {

	private static final String TEST_BODY = "test body";
	private static final String TEST_TITLE = "test title?";

	@Autowired
	private JokeService service;

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#createJoke(java.lang.String, java.lang.String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testCreateJoke() throws JokeException {
		service.createJoke(TEST_TITLE, TEST_BODY);
		String body = service.getJokeByTitle(TEST_TITLE);
		Assert.assertEquals("Incorrect joke body.", TEST_BODY, body);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#createJoke(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateJokeWithBlankTitle() {
		Assertions.assertThrows(JokeException.class, () -> {
			service.createJoke(" ", TEST_BODY);
		});
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#createJoke(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateJokeWithEmptyTitle() {
		Assertions.assertThrows(JokeException.class, () -> {
			service.createJoke("", TEST_BODY);
		});
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#createJoke(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateJokeWithNUllTitle() {
		Assertions.assertThrows(JokeException.class, () -> {
			service.createJoke(null, TEST_BODY);
		});
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testDeleteJoke() throws JokeException {
		service.createJoke(TEST_TITLE, TEST_BODY);
		service.deleteJoke(TEST_TITLE);
		String body = service.getJokeByTitle(TEST_TITLE);
		Assert.assertNull("Joke body should have been null.", body);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#deleteJoke(java.lang.String)}.
	 */
	@Test
	public void testDeleteJokeNotExists() {
		Assertions.assertThrows(JokeException.class, () -> {
			service.deleteJoke("bob");
		});
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.JokeService#getJokeByTitle(java.lang.String)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testGetJokeByTitle() throws JokeException {
		String body = service.getJokeByTitle(JokeService.JOKE_TITLE);
		Assert.assertEquals("Incorrect joke body.", JokeService.JOKE_BODY, body);

		body = service.getJokeByTitle("bad title");
		Assert.assertEquals("Joke body should have been null.", null, body);
		
		Assertions.assertThrows(JokeException.class, () -> {
			service.getJokeByTitle(null);
		});
		
		Assertions.assertThrows(JokeException.class, () -> {
			service.getJokeByTitle("");
		});
		
		Assertions.assertThrows(JokeException.class, () -> {
			service.getJokeByTitle(" ");
		});
	}

	/**
	 * Test method for {@link ca.footeware.web.services.JokeService#getTitles()}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	public void testGetTitles() throws JokeException {
		Set<String> titles = service.getTitles();
		Assert.assertTrue(titles.size() > 0);
	}

}
