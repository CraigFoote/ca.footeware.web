/**
 * 
 */
package ca.footeware.web.tests.it;

import java.util.List;

import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
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
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JokeControllerITTests {

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
	public void tearDown() throws JokeException {
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
	public void testDeleteJoke() throws JokeException {
		// create a joke to delete
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, headers);
		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assert.assertEquals("Incorrect response status.", HttpStatus.OK, status);
		String body = response.getBody();
		Assert.assertTrue("Should have displayed new joke title.", body.contains("testTitle</a>"));
		Assert.assertTrue("Should have displayed new joke body.", body.contains("testBody</div>"));

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assert.assertTrue("Missing response header 'X-Id'.", responseHeaders.containsKey("X-Id"));
		String id = responseHeaders.get("X-Id").get(0);

		// delete it
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity = template.exchange("/jokes/delete/" + id, HttpMethod.GET, requestEntity,
				String.class);
		status = response.getStatusCode();
		Assert.assertEquals("Incorrect response status.", HttpStatus.OK, status);
		String page = responseEntity.getBody();
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertFalse("Incorrect page returned.", page.contains("testTitle</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getAddJokePage(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetAddJokePage() {
		String page = template.getForObject("/jokes/add", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("<form action=\"/jokes/add\" method=\"post\">"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJoke(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetJoke() {
		String page = template.getForObject("/jokes/1", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJoke(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetJokeBadTitle() {
		String page = template.getForObject("/jokes/bad", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", !page.contains("<h3 class=\"title\">Nine o&amp;</h3>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getJokes(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetJokes() {
		String page = template.getForObject("/jokes", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#postJoke(java.lang.String, java.lang.String, org.springframework.ui.Model, HttpServletResponse)}.
	 * 
	 * @throws JokeException            if shit goes south
	 * @throws ServiceNotFoundException if shit goes north again
	 */
	@Test
	public void testPostJoke() throws JokeException, ServiceNotFoundException {
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);

		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assert.assertEquals("Incorrect response status.", HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assert.assertTrue("Missing response header 'X-Id'.", responseHeaders.containsKey("X-Id"));
		String id = responseHeaders.get("X-Id").get(0);

		// fetch new joke
		String page = template.getForObject("/jokes/" + id, String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("<h3 class=\"title\">testTitle</h3>"));

		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		request = new HttpEntity<>(joke, requestHeaders);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#editJoke(String, org.springframework.ui.Model)}.
	 * 
	 * @throws JokeException            if shit goes south
	 * @throws ServiceNotFoundException if shit goes north again
	 */
	@Test
	public void testEditJoke() throws JokeException, ServiceNotFoundException {
		// create joke to edit
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(joke, requestHeaders);
		ResponseEntity<String> response = template.postForEntity("/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assert.assertEquals("Incorrect response status.", HttpStatus.OK, status);

		// find id
		HttpHeaders responseHeaders = response.getHeaders();
		Assert.assertTrue("Missing response header 'X-Id'.", responseHeaders.containsKey("X-Id"));
		String id = responseHeaders.get("X-Id").get(0);

		// fetch joke
		String page = template.getForObject("/jokes/edit/" + id, String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("value=\"testTitle\" placeholder=\"Title\" />"));

		// simulate editing of joke and clicking save
		joke = new LinkedMultiValueMap<>();
		joke.add("id", id);
		joke.add("title", "testTitle2");
		joke.add("body", "testBody2");
		request = new HttpEntity<>(joke, requestHeaders);
		response = template.exchange("/jokes/edit", HttpMethod.POST, request, String.class);
		page = response.getBody();
		Assert.assertFalse("Joke should have updated title.", page.contains("testTitle</a>"));
		Assert.assertTrue("Joke should have updated title.", page.contains("testTitle2</a>"));
		Assert.assertTrue("Joke should have updated body.", page.contains("testBody2</div>"));
	}

}
