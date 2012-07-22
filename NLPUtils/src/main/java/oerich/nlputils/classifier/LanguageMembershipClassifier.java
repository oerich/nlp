package oerich.nlputils.classifier;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.dictionary.Dictionary;
import oerich.nlputils.tokenize.ITokenizer;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

/**
 * Classifies how much words of a text are part of a language.<br>
 * The classification formula is:<br>
 * classification(text) = totalNumberOfCorrectSpelledWords(text) /
 * totalNumberOfWords(text)<br>
 * The classification result is used as heuristic, if a text was written for a
 * given language. The acceptance factor of the heuristic can be calibrated via
 * a value between [0,1].<br>
 * The spell check for a word is done via a hunspell dictionary.
 * 
 * @author Philipp Förmer
 * @author Eric Knauss
 * 
 */
public class LanguageMembershipClassifier implements IClassifier<String> {

	/**
	 * The default value classification(text) must reach to classify text as
	 * being written in the language.
	 */
	public static final double DEFAULT_MATCH_VALUE = 0.90;

	private double matchValue = DEFAULT_MATCH_VALUE;

	private final ITokenizer tokenizer;

	private final Dictionary dictionary;

	public LanguageMembershipClassifier(final ITokenizer tokenizer,
			final Dictionary dictionary) {
		Validate.notNull(tokenizer);
		Validate.notNull(dictionary);

		this.tokenizer = tokenizer;
		this.dictionary = dictionary;
	}

	@Override
	public double classify(final String text) {
		Validate.notNull(text);

		int totalWords = 0;
		int totalWordsMatched = 0;
		tokenizer.setText(text);
		String term;
		while ((term = tokenizer.nextToken()) != null) {
			totalWords++;
			if (dictionary.isCorrectSpelled(term)) {
				totalWordsMatched++;
			}
		}

		if (totalWords > 0) {
			return ((double) totalWordsMatched / (double) totalWords);
		}
		return getMinimalValue();
	}

	@Override
	public double getMatchValue() {
		return matchValue;
	}

	@Override
	public boolean isMatch(final String text) throws IllegalArgumentException {
		return (classify(text) >= getMatchValue());
	}

	@Override
	public void setMatchValue(final double matchValue) {
		Validate.isTrue(matchValue >= getMinimalValue()
				&& matchValue <= getMaximumValue(),
				"value is not in value range [minimum, maximum]");
		this.matchValue = matchValue;
	}

	@Override
	public void init(final String initData) {
		throw new NotImplementedException();
	}

	@Override
	public TableModel explainClassification(final String text) {
		tokenizer.setText(text);
		String[] words = tokenizer.tokenize(text);
		TableModel tm = new DefaultTableModel(
				new String[] { "Word", "Correct?" }, words.length);

		String term;
		for (int i = 0; i < words.length; i++) {
			term = words[i];
			tm.setValueAt(term, i, 0);
			tm.setValueAt(Boolean.valueOf(dictionary.isCorrectSpelled(term)),
					i, 1);
		}
		return tm;
	}

	@Override
	public double getMaximumValue() {
		return 1;
	}

	@Override
	public double getMinimalValue() {
		return 0;
	}

}
