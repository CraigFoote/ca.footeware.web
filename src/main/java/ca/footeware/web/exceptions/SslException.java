/**
 * 
 */
package ca.footeware.web.exceptions;

/**
 * @author craig
 *
 */
public class SslException extends Exception {

	/**
	 * 
	 */
	public SslException() {
	}

	/**
	 * @param message
	 */
	public SslException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SslException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SslException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SslException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
