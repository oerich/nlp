package oerich.nlputils.tokenize;

import java.util.StringTokenizer;

/**
 * Benutzt den Java StringTokenizer, um nur Tokens zurück zu geben, die keinen
 * weißen Raum darstellen. Der Scannertyp sollte benutzt werden, wenn weißer
 * Raum irrelevanr ist, alle anderen Eingabezeichen jedoch schon.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * @see java.util.StringTokenizer
 */
public class WhitespaceTokenizer extends AbstractTokenizerAdapter {

	/**
	 * Tokenizer.
	 */
	private StringTokenizer tokenizer;

	/**
	 * Whitespace Character.
	 */
	private static final String DELIMS = "\t\n\f\r ";

	/**
	 * 
	 * @param text
	 * @throws IllegalArgumentException
	 *             Falls text null ist.
	 */
	public WhitespaceTokenizer(String text) throws IllegalArgumentException {
		setText(text);
	}

	/**
	 * @see ITokenizer
	 */
	public String nextToken() {

		if (this.tokenizer.hasMoreElements()) {

			return this.tokenizer.nextToken();

		}

		return null;

	}

	@Override
	public void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException(
					"Übergabeparameter text darf nicht null sein.");
		}

		this.tokenizer = new StringTokenizer(text, DELIMS);

	}

}
