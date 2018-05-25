/**
 * 
 */
package ca.footeware.web.tests.it;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Footeware.ca
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JokeControllerITTests {

	private static TestRestTemplate template;

	@BeforeClass
	public static void setup() {
		template = new TestRestTemplate(HttpClientOption.ENABLE_REDIRECTS, HttpClientOption.ENABLE_COOKIES);
	}
	private String baseURL = "http://localhost:";

	@LocalServerPort
	int port;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#deleteJoke(java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testDeleteJoke() {
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(joke,
				requestHeaders);

		ResponseEntity<String> response = template.postForEntity(baseURL + port + "/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assert.assertEquals("Incorrect response status.", HttpStatus.OK, status);

		String page = template.getForObject(baseURL + port + "/jokes/testTitle", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("<h3 class=\"title\">testTitle</h3>"));

		page = template.getForObject(baseURL + port + "/deletejoke/testTitle", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.",
				page.contains("href=\"/jokes/Nine o&#39;clock\">Nine o&#39;clock</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getAddJokePage(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetAddJokePage() {
		String page = template.getForObject(baseURL + port + "/addjoke", String.class);
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
		String page = template.getForObject(baseURL + port + "/jokes/Nine o&#39;clock", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("<h3 class=\"title\">Nine o&amp;</h3>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#getTitles(org.springframework.ui.Model)}.
	 */
	@Test
	public void testGetTitles() {
		String page = template.getForObject(baseURL + port + "/jokes", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.",
				page.contains("href=\"/jokes/Nine o&#39;clock\">Nine o&#39;clock</a>"));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.JokeController#postJoke(java.lang.String, java.lang.String, org.springframework.ui.Model)}.
	 */
	@Test
	public void testPostJoke() {
		MultiValueMap<String, String> joke = new LinkedMultiValueMap<>();
		joke.add("title", "testTitle");
		joke.add("body", "testBody");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(joke,
				requestHeaders);

		ResponseEntity<String> response = template.postForEntity(baseURL + port + "/jokes/add", request, String.class);
		HttpStatus status = response.getStatusCode();
		Assert.assertEquals("Incorrect reposnse status.", HttpStatus.OK, status);

		String page = template.getForObject(baseURL + port + "/jokes/testTitle", String.class);
		Assert.assertTrue("Incorrect page returned.",
				page.contains("<li class=\"active\"><a href=\"/jokes\">Jokes</a></li>"));
		Assert.assertTrue("Incorrect page returned.", page.contains("<h3 class=\"title\">testTitle</h3>"));

		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		request = new HttpEntity<MultiValueMap<String, String>>(joke, requestHeaders);

		// duplicate title?
		response = template.postForEntity(baseURL + port + "/jokes/add", request, String.class);
		Assert.assertTrue("Duplicate title error message not displayed.",
				response.getBody().contains("A joke by that title exists. Please choose another."));

		// cleanup
		template.getForObject(baseURL + port + "/deletejoke/testTitle", String.class);
	}

}
