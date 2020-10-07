/**
 * 
 */
package ca.footeware.web.exceptions;

/**
 * @author Footeware.ca
 *
 */
public class JokeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message {@link String}
	 */
	public JokeException(String message) {
		super(message);
	}

}
