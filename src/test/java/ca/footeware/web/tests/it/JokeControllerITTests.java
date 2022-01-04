/**
 *
 */
package ca.footeware.web.tests.it;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import ca.footeware.web.controllers.JokeController;
import ca.footeware.web.exceptions.JokeException;
import ca.footeware.web.models.Joke;
import ca.footeware.web.services.JokeService;

/**
 * @author Footeware.ca
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class JokeControllerITTests extends ItTests {

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
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testDeleteJoke() throws RestClientException, Exception {
		// create a joke to delete
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, headers);
		ResponseEntity<String> response = restTemplate().postForEntity(HOST + "/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);
		String body = response.getBody();
		Assertions.assertTrue(body.contains("testTitle</button>"), "Should have displayed new joke title.");
		Assertions.assertTrue(body.contains("testBody</div>"), "Should have displayed new joke body.");

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");
		String id = responseHeaders.get("X-Id").get(0);

		// delete it
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate().exchange(HOST + "/jokes/delete/" + id, HttpMethod.GET,
				requestEntity, String.class);
		status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);
		String page = responseEntity.getBody();
		Assertions.assertTrue(page.contains("<a class=\"nav-link active\" href=\"/jokes\">Jokes</a>"));
		Assertions.assertFalse(page.contains("testTitle</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#editJoke(String, org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testEditJoke() throws RestClientException, Exception {
		// create joke to edit
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);
		ResponseEntity<String> response = restTemplate().postForEntity(HOST + "/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");
		String id = responseHeaders.get("X-Id").get(0);

		// fetch joke
		String page = restTemplate().getForObject(HOST + "/jokes/edit/" + id, String.class);
		Assertions.assertTrue(page.contains("Edit Joke</h3>"),
				"Incorrect page returned.");
		Assertions.assertTrue(page.contains("value=\"testTitle\" placeholder=\"Title\" />"),
				"Incorrect page returned.");

		// simulate editing of joke and clicking save
		joke = new LinkedMultiValueMap<>();
		joke.add("id", id);
		joke.add("title", "testTitle2");
		joke.add("body", "testBody2");
		request = new HttpEntity<>(joke, requestHeaders);
		response = restTemplate().exchange(HOST + "/jokes/edit", HttpMethod.POST, request, String.class);
		page = response.getBody();
		Assertions.assertFalse(page.contains("testTitle</button>"), "Joke should have updated title.");
		Assertions.assertTrue(page.contains("testTitle2</button>"), "Joke should have updated title.");
		Assertions.assertTrue(page.contains("testBody2</div>"), "Joke should have updated body.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getAddJokePage(org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetAddJokePage() throws RestClientException, Exception {
		ResponseEntity<String> response = restTemplate().getForEntity(HOST + "/jokes/add", String.class,
				Collections.emptyMap());
		String body = response.getBody();
		Assertions.assertTrue(body.contains("<a class=\"nav-link active\" href=\"/jokes\">Jokes</a>"),
				"Incorrect page returned.");
		Assertions.assertTrue(body.contains("<form action=\"/jokes/add\" method=\"post\">"),
				"Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJokes(org.springframework.ui.Model)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testGetJokes() throws RestClientException, Exception {
		ResponseEntity<String> response = restTemplate().getForEntity(HOST + "/jokes", String.class,
				Collections.emptyMap());
		String body = response.getBody();
		Assertions.assertTrue(body.contains("<a class=\"nav-link active\" href=\"/jokes\">Jokes</a>"),
				"Incorrect page returned.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#postJoke(java.lang.String, java.lang.String, org.springframework.ui.Model, HttpServletResponse)}.
	 *
	 * @throws Exception
	 * @throws RestClientException
	 */
	@Test
	void testPostJoke() throws RestClientException, Exception {
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);

		ResponseEntity<String> response = restTemplate().postForEntity(HOST + "/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assertions.assertEquals(HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assertions.assertTrue(responseHeaders.containsKey("X-Id"), "Missing response header 'X-Id'.");

		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		request = new HttpEntity<>(joke, requestHeaders);
	}

}
