package oerich.nlputils.classifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;


/**
 * An implementation of a Bayesian Classifier. We have used this to identify
 * security relevant requirements. This package is in an inconsistent and
 * experimental state.
 *
 * @author Eric Knauss
 *
 */
public final class BayesianFilter implements IBayesianFilter {

	/**
	 * This constant increases the weight of the fact that a word was found in a
	 * security relevant requirement.
	 */
	private static final int PRO_CLASS_BIAS = 2;

	private IStopWordFilter filter = StopWordFilterFactory.NULL_FILTER;
	private ITokenizer tokenizer = StopWordFilterFactory
			.createTokenizer();
	private Comparator<Double> comparator = IBayesianFilter.BEST_VALUES;
	private IDataSet dataSet;
	private HashMap<String, Double> wordBayesValue;
	private double proSecBias = PRO_CLASS_BIAS;
	private double unknownWordValue = 0.5;

	private double matchValue = 0.9;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.unihannover.se.nlputils.datamining.IBaysianFilter#setUnknownWordValue
	 * (double)
	 */
	public void setUnknownWordValue(double d) {
		this.unknownWordValue = d;
	}

	@Override
	public double getUnknownWordValue() {
		return this.unknownWordValue;
	}

	public BayesianFilter() {
		this.wordBayesValue = new HashMap<String, Double>();
	}

	@Override
	public IDataSet getDataSet() {
		return this.dataSet;
	}

	@Override
	public List<Entry<String, Double>> setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet;
		this.wordBayesValue.clear();

		computeProbablitiesForWords(dataSet);
		// Basically, we are ready now.
		// This helps to give an overview of the dataset:
		LinkedList<Entry<String, Double>> linkedList = getDataSetContents();
		return linkedList;
	}

	private void computeProbablitiesForWords(IDataSet dataSet) {
		if (dataSet == null)
			return;
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
			double a = (this.proSecBias * countSec) / wordsInClass;
			// if ("system".equals(word))
			// System.out.println("Word: (" + word + "), a countSec ("
			// + countSec + ") / countSecReq (" + countSecReq + ") = "
			// + a);
			double b = countNonSec / wordsNotInClass;
			// if ("system".equals(word))
			// System.out.println("Word: (" + word + "), b countNonSec ("
			// + countNonSec + ") / countNonSecReq (" + countNonSecReq
			// + ") = " + b);
			double bayesValue = a / (a + b);
			// if (Double.isNaN(bayesValue))
			// System.err.println("P(" + word + ") = NaN; " + a + " / (" + a
			// + "+" + b + ")");
			wordBayesValue.put(word, bayesValue);
		}
	}

	private LinkedList<Entry<String, Double>> getDataSetContents() {
		Set<Entry<String, Double>> s = wordBayesValue.entrySet();
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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.unihannover.se.nlputils.datamining.IBaysianFilter#computeBayes(java
	 * .lang.String)
	 */
	public double computeBayes(String reqText) {
		String[] words = prepareText(reqText);

		// get the calculated values for the words in the requirements
		Double[] values = computeBayesianValues(words);
		// for (int i = 0; i < values.length; i++) {
		//
		// System.out.print(words[i] + "(" + values[i] + ")");
		// }
		// System.out.println();
		Double[] interestingValues = filterInterestingValues(values);
		// Double[] interestingValues = values;

		return calculateBayesianCombination(interestingValues);
	}

	/**
	 * Returns the comparator that selects the 15 best words based on their
	 * probability values.
	 *
	 * @return
	 */
	public Comparator<Double> getComparator() {
		return comparator;
	}

	/**
	 * Sets the comparator that selects the 15 best words based on their
	 *
	 * @param comparator
	 */
	public void setComparator(Comparator<Double> comparator) {
		this.comparator = comparator;
	}

	/**
	 * This sets the bias of probabilities pro sec - probabilities of words that
	 * point towards sec are multiplied with d.
	 *
	 * @param d
	 */
	public void setProSecBias(double d) {
		this.proSecBias = d;
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
			words = this.wordBayesValue.keySet().toArray(new String[0]);
		}

		String[] columns = { "Word", "Probability" };
		Object[][] data = new Object[words.length][2];
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			data[i][0] = word;
			data[i][1] = getBayesValueFor(word);
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

	protected Double getBayesValueFor(String word) {
		Double bayesianVal = this.wordBayesValue.get(word);
		if (bayesianVal == null)
			return this.unknownWordValue;
		if (Double.isNaN(bayesianVal)) {
			// System.err.println("NaN - " + word);
			return this.unknownWordValue;
		}
		return bayesianVal;
	}

	private double calculateBayesianCombination(Double[] interestingValues) {
		// calculate Bayesian Combination
		double product = 1.0;
		double inverseProduct = 1.0;
		for (double value : interestingValues) {
			if (value == 0)
				value = 0.001;
			if (value == 1)
				value = 0.999;
			product *= value;
			inverseProduct *= (1 - value);
			// System.out.println("--> product: " + product +
			// ", inverseProduct: "
			// + inverseProduct);

		}

		// System.out.println("computebayes, product: " + product
		// + ", inverseProduct: " + inverseProduct);
		return product / (product + inverseProduct);
	}

	private String[] prepareText(String reqText) {
		String[] words = this.tokenizer.tokenize(reqText);

		return this.filter.filterStopWords(words);
	}

	private Double[] computeBayesianValues(String[] words) {
		Double[] ret = new Double[words.length];
		for (int i = 0; i < words.length; i++) {
			ret[i] = getBayesValueFor(words[i]);
		}
		return ret;
	}

	private Double[] filterInterestingValues(Double[] values) {
		Arrays.sort(values, this.comparator);

		// get interesting 15 values
		int length = Math.min(values.length, 15);
		Double[] interestingValues = new Double[length];
		for (int i = 0; i < interestingValues.length; i++) {
			interestingValues[i] = values[values.length - 1 - i];
			// System.out.println(interestingValues[i]);
		}
		return interestingValues;
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
		return classify(text) > getMatchValue();
	}

	@Override
	public void setMatchValue(double value) {
		if (value < 0 || value > 1)
			throw new IllegalArgumentException("Value must be between 0 and 1.");
		this.matchValue = value;
	}

	@Override
	public void init(IDataSet initData) throws Exception {
		setDataSet(initData);
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
