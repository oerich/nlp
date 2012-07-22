package oerich.nlputils.readingease;

import oerich.nlputils.hyphen.IHyphenator;
import oerich.nlputils.tokenize.ITokenizer;
import oerich.nlputils.tokenize.StandardGermanWordTokenizer;


/**
 * Klasse implementiert die deutsche Flesch-Lesbarkeitsmetrik. Die Messung mit
 * dieser beginnt jedoch erst, wenn bestimmte Grenzwerte überschritten worden
 * sind. Unterhalb der Grenzwerte wird stets ein maximaler Lesbarkeitsindex
 * zurück gegeben.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * @see http://de.wikipedia.org/wiki/Lesbarkeitsindex
 * @see http://www.mang.canterbury.ac.nz/writing_guide/writing/flesch.shtml
 */
public class GermanFleshReadingEase implements IReadingEase {

	/**
	 * Mindestens-Anzahl von Worten die ein Abschnitt haben muss damit eine
	 * Messung statt findet.
	 */
	private static final int WORD_THRESHOLD = 16;

	private static final double MAX_READING_EASE = 100;

	/**
	 * 
	 */
	private IHyphenator hyphenator;

	/**
	 * 
	 * @param hyphenator
	 * @throws IllegalArgumentException
	 */
	public GermanFleshReadingEase(IHyphenator hyphenator)
			throws IllegalArgumentException {

		if (hyphenator == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter hyphenator darf nicht null sein.");

		}

		this.hyphenator = hyphenator;

	}

	/**
	 * @see IReadingEase
	 */
	public Number calculateReadingEaseForSentence(String sentence)
			throws IllegalArgumentException {

		if (sentence == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter sentence darf nicht null sein.");

		}

		return this.calculateReadingEaseForParagraph(new String[] { sentence });

	}

	/**
	 * @see IReadingEase
	 */
	public Number calculateReadingEaseForParagraph(String sentences[])
			throws IllegalArgumentException {

		if (sentences == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter sentences darf nicht null sein.");

		}

		int wordCount = 0, syllableCount = 0, sentenceCount = 0;

		ITokenizer tokenizer = new StandardGermanWordTokenizer();
		for (int i = 0; i < sentences.length; i++) {

			if (sentences[i].isEmpty()) {

				continue;

			}

			sentenceCount++;

			tokenizer.setText(sentences[i]);
			String term;

			while ((term = tokenizer.nextToken()) != null) {

				wordCount++;
				syllableCount += this.hyphenator.syllableCount(term);

				System.out.println(term + "(words:"+wordCount + ",syllables:"+syllableCount);
			}

		}

		if (wordCount == 0 || wordCount < WORD_THRESHOLD) {

			return new Double(MAX_READING_EASE);

		}

		double val = 180 - ((double) wordCount / (double) sentenceCount) - 58.5
				* ((double) syllableCount / (double) wordCount);
		return new Double(val);

	}

}
