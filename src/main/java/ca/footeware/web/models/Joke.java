/**
 * 
 */
package ca.footeware.web.models;

import org.springframework.data.annotation.Id;

/**
 * @author Footeware.ca
 *
 */
public class Joke {

	@Id
	private String id;
	private String title;
	private String body;

	/**
	 * Constructor.
	 * 
	 * @param id    {@link String}
	 * @param title {@link String}
	 * @param body  {@link String}
	 */
	public Joke(String id, String title, String body) {
		this.id = id;
		this.title = title;
		this.body = body;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

}
