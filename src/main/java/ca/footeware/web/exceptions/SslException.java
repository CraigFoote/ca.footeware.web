/**
 *
 */
package ca.footeware.web.exceptions;

/**
 * @author Footeware.ca
 *
 */
public class SslException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public SslException() {
	}

	/**
	 * @param message {@link String}
	 */
	public SslException(String message) {
		super(message);
	}

	/**
	 * @param cause {@link Throwable}
	 */
	public SslException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message {@link String}
	 * @param cause {@link Throwable}
	 */
	public SslException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message {@link String}
	 * @param cause {@link Throwable}
	 * @param enableSuppression boolean
	 * @param writableStackTrace boolean
	 */
	public SslException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
