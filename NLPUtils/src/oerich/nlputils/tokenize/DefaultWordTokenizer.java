package oerich.nlputils.tokenize;

/**
 * A standard tokenizer that splits a text into its words. The main feature is
 * that you can set a list of <i>stopsigns</i> that will be removed from the
 * text before tokenization.
 *
 * @author Eric Knauss
 *
 */
public class DefaultWordTokenizer extends AbstractTokenizerAdapter {

	private String[] stopsigns;
	private String[] whitespace = { "\n", "\t", "  " };

	/**
	 * Creates the DefaultWordTokenizer without any <i>stopsigns</i>.
	 */
	public DefaultWordTokenizer() {
		this.stopsigns = new String[0];
	}

	/**
	 * Creates the DefaultWordTokenizer with <i>stopsigns</i> that will be
	 * filtered out before the tokenization.
	 *
	 * @param stopsigns
	 *            - <i>stopsigns</i> that will be filtered out before the
	 *            tokenization
	 */
	public DefaultWordTokenizer(String[] stopsigns) {
		this.stopsigns = stopsigns;
	}

	@Override
	public String[] tokenize(String text) {
		text = text.toLowerCase();
		// remove stop sign
		text = removeSigns(text, this.stopsigns);

		text = removeSigns(text, whitespace);
		return text.split(" ");
	}

	private String removeSigns(String text, String[] signs) {
		for (String sign : signs) {
			while (text.indexOf(sign) >= 0)
				text = text.replace(sign, " ");
		}
		return text;
	}

	/**
	 * Sets the <i>stopsigns</i> that will be filtered out before the
	 * tokenization
	 *
	 * @param array
	 *            - <i>stopsigns</i> that will be filtered out before the
	 *            tokenization
	 */
	public void setStopSigns(String[] array) {
		this.stopsigns = array;
	}

	/**
	 * Returns the <i>stopsigns</i> that will be filtered out before the
	 * tokenization.
	 *
	 * @return <i>stopsigns</i> that will be filtered out before the
	 *         tokenization. This is an empty String-Array by default.
	 */
	public String[] getStopSigns() {
		return this.stopsigns;
	}
}
