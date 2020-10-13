/**
 * 
 */
package ca.footeware.web.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;

/**
 * @author Footeware.ca
 *
 */
public class Joke {

	@Id
	private String id = UUID.randomUUID().toString();
	private String title;
	private String body;

	/**
	 * Constructor.
	 * 
	 * @param title {@link String}
	 * @param body  {@link String}
	 */
	public Joke(String title, String body) {
		this.title = title;
		this.body = body;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
