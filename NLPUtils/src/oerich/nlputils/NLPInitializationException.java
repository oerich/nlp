package oerich.nlputils;

import oerich.nlputils.i18n.NLPUtili18n;

/**
 * Thrown, when the initialization of a feature failed.
 *
 * @author Eric Knauss
 *
 */
public class NLPInitializationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the Exception with a meaningful message and (if exists) a cause.
	 *
	 * @param message
	 *            - a meaningful message that will be shown to the end-user.
	 *            Consider the use of the internationalization interface.
	 * @param cause
	 *            - the exception that caused the initialization to fail.
	 * @see NLPUtili18n
	 */
	public NLPInitializationException(String message, Exception cause) {
		super(message, cause);
	}
}
