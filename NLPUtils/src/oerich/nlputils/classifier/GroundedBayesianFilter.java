package oerich.nlputils.classifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;


/**
 * An implementation of a Bayesian Classifier with a focus on mathematical
 * soundness. We have used this to identify security relevant requirements. This
 * package is in an inconsistent and experimental state.
 * 
 * @author Eric Knauss
 * 
 */
public class GroundedBayesianFilter implements IBayesianFilter {

	private IDataSet dataSet;
	private double proSecBias = 1.0;
	private Map<String, Double> wordSecProbabilities = new HashMap<String, Double>();
	private Map<String, Double> wordNonsecProbabilities = new HashMap<String, Double>();
	private double unknownWordValue = 0.5;
	private IStopWordFilter filter = StopWordFilterFactory.NULL_FILTER;
	private ITokenizer tokenizer = StopWordFilterFactory.createTokenizer();
	private double matchValue = 0.9;

	@Override
	public double computeBayes(String reqText) {
		String[] words = prepareText(reqText);

		double secWi = 1.0;
		double nonsecWi = 1.0;
		for (String word : words) {
			secWi *= getWordProbability(this.wordSecProbabilities, word);
			nonsecWi *= getWordProbability(this.wordNonsecProbabilities, word);
		}

		return secWi / (secWi + nonsecWi);
	}

	private double getWordProbability(Map<String, Double> probabilities,
			String word) {
		if (probabilities.containsKey(word)) {
			double wi = probabilities.get(word);
			if (wi == 1.0)
				return 0.999;
			if (wi == 0.0)
				return 0.001;
			return wi;
		}
		return this.unknownWordValue;
	}

	@Override
	public IDataSet getDataSet() {
		return this.dataSet;
	}

	@Override
	public double getUnknownWordValue() {
		return this.unknownWordValue;
	}

	@Override
	public List<Entry<String, Double>> setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet;
		this.wordSecProbabilities.clear();
		this.wordNonsecProbabilities.clear();

		computeProbablitiesForWords(dataSet);
		// Basically, we are ready now.
		// This helps to give an overview of the dataset:
		LinkedList<Entry<String, Double>> linkedList = getDataSetContents(this.wordSecProbabilities
				.entrySet());
		linkedList.addAll(getDataSetContents(this.wordNonsecProbabilities
				.entrySet()));
		return linkedList;
	}

	private void computeProbablitiesForWords(IDataSet dataSet) {
		int wordsInClass = this.dataSet.getCountSecReq();
		int wordsNotInClass = this.dataSet.getCountNonSecReq();
		// to avoid dividing throw zero
		if (wordsInClass == 0) {
			wordsInClass = 1;
		}
		if (wordsNotInClass == 0) {
			wordsNotInClass = 1;
		}

		for (String word : dataSet.getWords()) {
			double countSec = dataSet.getSecValue(word);
			double countNonSec = dataSet.getNonSecValue(word);
			double probWordSec = (this.proSecBias * countSec) / wordsInClass;
			double probWordNonsec = countNonSec / wordsNotInClass;

			this.wordSecProbabilities.put(word, probWordSec);
			this.wordNonsecProbabilities.put(word, probWordNonsec);
		}
	}

	private LinkedList<Entry<String, Double>> getDataSetContents(
			Set<Entry<String, Double>> s) {
		LinkedList<Entry<String, Double>> linkedList = new LinkedList<Entry<String, Double>>(
				s);
		Collections.sort(linkedList, new Comparator<Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> arg0,
					Entry<String, Double> arg1) {
				return arg0.getValue().compareTo(arg1.getValue());
			}

		});
		// for (Entry<String,Double> e: linkedList) {
		// System.out.println(e);
		// }
		return linkedList;
	}

	@Override
	public void setUnknownWordValue(double d) {
		this.unknownWordValue = d;

	}

	@Override
	public double classify(String text) throws IllegalArgumentException {
		return computeBayes(text);
	}

	@Override
	public double getMatchValue() {
		return this.matchValue;
	}

	@Override
	public boolean isMatch(String text) throws IllegalArgumentException {
		return classify(text) >= getMatchValue();
	}

	@Override
	public void setMatchValue(double value) {
		this.matchValue = value;
	}

	private String[] prepareText(String reqText) {
		String[] words = this.tokenizer.tokenize(reqText);

		return this.filter.filterStopWords(words);
	}

	@Override
	public void init(IDataSet initData) throws Exception {
		setDataSet(initData);
	}

	/**
	 * Debug and user service.
	 * 
	 * @param text
	 *            one piece of text being classified.
	 * @return html table with probabilities of each word.
	 */
	@Override
	public TableModel explainClassification(String text) {

		String[] words = null;
		if (text != null)
			words = prepareText(text);
		else {
			words = this.wordSecProbabilities.keySet().toArray(new String[0]);
		}

		String[] columns = { "Word", "P(sec)", "P(nonsec)" };
		Object[][] data = new Object[words.length][3];
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			data[i][0] = word;
			data[i][1] = getWordProbability(this.wordSecProbabilities, word);
			data[i][2] = getWordProbability(this.wordNonsecProbabilities, word);
		}

		return new DefaultTableModel(data, columns) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				if (column > 0)
					return Double.class;
				return String.class;
			};
		};
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
