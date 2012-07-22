package oerich.nlputils.classifier;

import java.io.FileNotFoundException;

import java.io.UnsupportedEncodingException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.tokenize.ITokenizer;
import oerich.nlputils.tokenize.StandardGermanWordTokenizer;

import com.stibocatalog.hunspell.Hunspell;


/**
 * Klasse kann Texte auf deutsche Sprache hin klassifizieren. Zur
 * Wortklassifizierung benutzt die Klasse Hunspell.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * 
 */
public class GermanHunspellClassifier implements IClassifier<String> {

	/**
	 * Standard Matchwert
	 */
	private static final double DEFAULT_MATCH_VALUE = 0.90;

	/**
	 * Gesetzter Matchwert
	 */
	private double matchValue;

	/**
	 * Hunspell Manager Instanz
	 */
	private Hunspell hunspell;

	/**
	 * Geladenes Wörterbuch
	 */
	private Hunspell.Dictionary dictionary;

	/**
	 * 
	 * @param dictionaryBaseName
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public GermanHunspellClassifier(String dictionaryBaseName)
			throws FileNotFoundException, UnsupportedEncodingException {

		this.hunspell = Hunspell.getInstance();
		init(dictionaryBaseName);
		this.setMatchValue(DEFAULT_MATCH_VALUE);

	}

	/**
	 * @see IClassifier
	 */
	public double classify(String text) throws IllegalArgumentException {

		if (text == null) {

			throw new IllegalArgumentException(
					"Parameter text darf nicht null sein.");

		}

		int totalWords = 0, totalMatches = 0;
		ITokenizer tokenizer = new StandardGermanWordTokenizer();
		tokenizer.setText(text);
		String term;

		while ((term = tokenizer.nextToken()) != null) {

			totalWords++;

			if (!this.dictionary.misspelled(term)) {

				totalMatches++;

			}

		}

		if (totalWords > 0) {

			return ((double) totalMatches / (double) totalWords);

		}

		return getMinimalValue();

	}

	/**
	 * @see IClassifier
	 */
	public double getMatchValue() {

		return this.matchValue;

	}

	/**
	 * @see IClassifier
	 */
	public boolean isMatch(String text) throws IllegalArgumentException {

		return (this.classify(text) >= this.getMatchValue());

	}

	/**
	 * @see IClassifier
	 */
	public void setMatchValue(double value) {

		if (value >= getMinimalValue() && value <= getMaximumValue()) {

			this.matchValue = value;

		}

	}

	@Override
	public void init(String initData) throws FileNotFoundException,
			UnsupportedEncodingException {
		this.dictionary = this.hunspell.getDictionary(initData);
	}

	@Override
	public TableModel explainClassification(String text) {
		ITokenizer tokenizer = new StandardGermanWordTokenizer();
		tokenizer.setText(text);
		String[] words = tokenizer.tokenize(text);
		
		TableModel tm = new DefaultTableModel(new String[]{"Word","Correct?"},words.length);

		String term;
		for (int i = 0; i < words.length; i++) {
			term = words[i];
			tm.setValueAt(term, i, 0);
			tm.setValueAt(Boolean.valueOf(!this.dictionary.misspelled(term)), i, 1);
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
