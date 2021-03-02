/**
 * 
 */
package ca.footeware.web.tests.it;

import java.util.List;

import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ca.footeware.web.controllers.JokeController;
import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.services.JokeService;

/**
 * @author Footeware.ca
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class JokeControllerITTests {

	@Autowired
	private TestRestTemplate template;
	@Autowired
	private JokeService jokeService;

	/**
	 * Empty DB after tests.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@AfterEach
	void tearDown() throws JokeException {
		List<Joke> jokes = jokeService.getJokes();
		for (Joke joke : jokes) {
			jokeService.deleteJoke(joke.getId());
		}
	}

	/**
	 * Test method for
	 * {@link JokeController#deleteJoke(java.lang.String, org.springframework.ui.Model)}.
	 * 
	 * @throws JokeException if shit goes south
	 */
	@Test
	void testDeleteJoke() throws JokeException {
		// create a joke to delete
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, headers);
		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);
		String body = response.getBody();
		Assertions.assertTrue(body.contains("testTitle</a>"), "Should have displayed new joke title.");
		Assertions.assertTrue(body.contains("testBody</div>"), "Should have displayed new joke body.");

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");
		String id = responseHeaders.get("X-Id").get(0);

		// delete it
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity = template.exchange("/jokes/delete/" + id, HttpMethod.GET, requestEntity,
				String.class);
		status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);
		String page = responseEntity.getBody();
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assertions.assertFalse(page.contains("testTitle</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#editJoke(String, org.springframework.ui.Model)}.
	 * 
	 * @throws JokeException            if shit goes south
	 * @throws ServiceNotFoundException if shit goes north again
	 */
	@Test
	void testEditJoke() throws JokeException, ServiceNotFoundException {
		// create joke to edit
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);
		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");
		String id = responseHeaders.get("X-Id").get(0);

		// fetch joke
		String page = template.getForObject("/jokes/edit/" + id, String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("value=\"testTitle\" placeholder=\"Title\" />"),
				"Incorrect page returned.");

		// simulate editing of joke and clicking save
		joke = new LinkedMultiValueMap<>();
		joke.add("id", id);
		joke.add("title", "testTitle2");
		joke.add("body", "testBody2");
		request = new HttpEntity<>(joke, requestHeaders);
		response = template.exchange("/jokes/edit", HttpMethod.POST, request, String.class);
		page = response.getBody();
		Assertions.assertFalse(page.contains("testTitle</a>"), "Joke should have updated title.");
		Assertions.assertTrue(page.contains("testTitle2</a>"), "Joke should have updated title.");
		Assertions.assertTrue(page.contains("testBody2</div>"), "Joke should have updated body.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getAddJokePage(org.springframework.ui.Model)}.
	 */
	@Test
	void testGetAddJokePage() {
		String page = template.getForObject("/jokes/add", String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("<form action=\"/jokes/add\" method=\"post\">"),
				"Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJoke(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	void testGetJoke() {
		String page = template.getForObject("/jokes/1", String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJoke(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	void testGetJokeBadTitle() {
		String page = template.getForObject("/jokes/bad", String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(!page.contains("<h3 class=\"title\">Nine o&amp;</h3>"), "Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJokes(org.springframework.ui.Model)}.
	 */
	@Test
	void testGetJokes() {
		String page = template.getForObject("/jokes", String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#postJoke(java.lang.String, java.lang.String, org.springframework.ui.Model, HttpServletResponse)}.
	 * 
	 * @throws JokeException            if shit goes south
	 * @throws ServiceNotFoundException if shit goes north again
	 */
	@Test
	void testPostJoke() throws JokeException, ServiceNotFoundException {
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);

		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");
		String id = responseHeaders.get("X-Id").get(0);

		// fetch new joke
		String page = template.getForObject("/jokes/" + id, String.class);
		Assertions.assertTrue(page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("<h3 class=\"title\">testTitle</h3>"), "Incorrect page returned.");

		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		request = new HttpEntity<>(joke, requestHeaders);
	}

}
