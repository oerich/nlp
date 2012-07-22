package oerich.nlputils.classifier;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

import oerich.nlputils.dictionary.Dictionary;
import oerich.nlputils.tokenize.ITokenizer;

/**
 * Classifies how much words of a text are part of a language.<br>
 * The classification formula is:<br>
 * classification(text) = totalNumberOfCorrectSpelledWords(text) / totalNumberOfWords(text)<br>
 * The classification result is used as heuristic, if a text was written for a given language.
 * The acceptance factor of the heuristic can be calibrated via a value between [0,1].<br>
 * The spell check for a word is done via a hunspell dictionary.
 * 
 * @author Philipp Förmer
 * @author Eric Knauss
 * 
 */
public class LanguageMembershipClassifier implements IClassifier<String> {

	/**
	 * The default value classification(text) must reach to classify text as being written in the language.
	 */
	public static final double DEFAULT_MATCH_VALUE = 0.90;

	private double matchValue = DEFAULT_MATCH_VALUE;

	private ITokenizer tokenizer;
	
	private Dictionary dictionary;

	public LanguageMembershipClassifier(ITokenizer tokenizer, Dictionary dictionary) {
		Validate.notNull(tokenizer);
		Validate.notNull(dictionary);
		
		this.tokenizer = tokenizer;
		this.dictionary = dictionary;
	}

	@Override
	public double classify(String text) {
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
		return this.matchValue;
	}

	@Override
	public boolean isMatch(String text) throws IllegalArgumentException {
		return (this.classify(text) >= this.getMatchValue());
	}

	@Override
	public void setMatchValue(double value) {
		if (value >= getMinimalValue() && value <= getMaximumValue()) {
			this.matchValue = value;
		}
	}

	@Override
	public void init(String initData) {
		throw new NotImplementedException();
	}

	@Override
	public TableModel explainClassification(String text) {
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
