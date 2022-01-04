/**
 *
 */
package ca.footeware.web.exceptions;

/**
 * @author craig
 *
 */
public class ImageException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An image-related exception.
	 *
	 * @param message {@link String}
	 */
	public ImageException(String message) {
		super(message);
	}
}
