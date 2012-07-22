package oerich.nlputils.hyphen;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.davidashen.text.Hyphenator;

/**
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 */
public class TexHyphenator implements IHyphenator {
	private static final String HYPHEN_TABLE_FILEPATH = "oerich/nlputils/resource/dehypha.tex";
	private static final char HYPHEN_CHAR = "­".charAt(0);
	/**
	 *
	 */
	private Hyphenator hyphenator;

	/**
	 * 
	 * @param texHyphenTableFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	TexHyphenator(InputStream texHyphenTable) throws FileNotFoundException,
			IOException, IllegalArgumentException {

		if (texHyphenTable == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter texHyphenTable darf nicht null sein.");

		}

		this.hyphenator = new Hyphenator();
		this.hyphenator.loadTable(new BufferedInputStream(texHyphenTable));

	}

	public TexHyphenator() throws FileNotFoundException,
			IllegalArgumentException, IOException {
		this(TexHyphenator.class.getClassLoader().getResourceAsStream(
				HYPHEN_TABLE_FILEPATH));
	}

	/**
	 * @see IHyphenator
	 */
	public String hyphenate(String word) throws IllegalArgumentException {

		if (word == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter word darf nicht null sein.");

		}

		return this.hyphenator.hyphenate(word);

	}

	/**
	 * @see IHyphenator
	 */
	public int syllableCount(String word) throws IllegalArgumentException {
		if (word == null || word.isEmpty()) {
			return 0;
		}
		String hyphennatedWord = this.hyphenate(word);
		return numHyphen(hyphennatedWord);
	}

	/**
	 * Zählt die Anzahl der Silbenstriche im Wort.
	 * 
	 * @param word
	 * @return
	 */
	private int numHyphen(String word) {
		int numHyphen = 1;
		for (int i = 0; i < word.length(); i++) {
			if (HYPHEN_CHAR == word.charAt(i)) {
				numHyphen++;
			}
		}
		return numHyphen;
	}

}
