package oerich.nlputils.tokenize;

/**
 * Interface of the String tokenizer. Basically, you can use this interface in
 * two ways:
 * <ol>
 * <li>Set the String with <code>setText(String)</code> and go through the
 * tokens with something like
 *
 * <pre>
 * ITokenizer tokenizer = new ...
 * tokenizer.setText(myText);
 * String t = tokenizer.nextToken();
 * while (t != null) {
 *   // do something with t
 *   t = tokenizer.nextToken();
 * }
 * </pre>
 *
 * </li>
 * <li>Tokenize the String directly:
 *
 * <pre>
 * ITokenizer tokenizer = new ...
 * for (String t : tokenizer.tokenize(myText)){
 *   // do something with t
 * }
 * </pre>
 *
 * </li>
 * </ol>
 *
 * @author Philipp Förmer, Eric Knauss
 *
 */
public interface ITokenizer {

	/**
	 * Gibt das nächste Token zurück, das gefunden wurde.
	 *
	 * @return Null, falls am Ende der Eingabe. Das Token sonst.
	 */
	public String nextToken();

	/**
	 * Extracts relevant tokens from the text.
	 *
	 * @param text
	 * @return
	 */
	public abstract String[] tokenize(String text);

	/**
	 * Set the text that should be tokenized.
	 *
	 * @param text
	 *            the text to be tokenized.
	 */
	public void setText(String text);
}
