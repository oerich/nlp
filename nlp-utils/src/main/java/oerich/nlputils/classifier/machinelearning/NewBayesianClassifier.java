package oerich.nlputils.classifier.machinelearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.classifier.IBayesianFilter;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.DefaultWordTokenizer;
import oerich.nlputils.tokenize.ITokenizer;

public class NewBayesianClassifier implements ILearningClassifier {

	/**
	 * This constant increases the weight of the fact that a word was found in a
	 * security relevant requirement.
	 */
	private static final int PRO_CLASS_BIAS = 2;

	// Utilities for processing text:
	private ITokenizer tokenizer = StopWordFilterFactory.createTokenizer();
	private IStopWordFilter filter = StopWordFilterFactory.NULL_FILTER;
	private Comparator<Double> comparator = IBayesianFilter.BEST_VALUES;
	private IStemmer stemmer;

	// Some parameters that influence recall and precision:
	private double unknownWordValue = 0.5;
	private double proClassBias = PRO_CLASS_BIAS;
	private double matchValue = 0.9;

	// Paydata:
	private int thingsInClass;
	private int thingsNotInClass;
	private Map<String, Integer> wordsInClass = new HashMap<String, Integer>();
	private Map<String, Integer> wordsNotInClass = new HashMap<String, Integer>();

	// Derived values:
	private Map<String, Double> wordBayesValue = new HashMap<String, Double>();

	private File file;

	private boolean isAutosave = true;

	public NewBayesianClassifier() throws IOException {
	}

	public void setStopWordFilter(IStopWordFilter filter) {
		if (filter == null)
			this.filter = StopWordFilterFactory.NULL_FILTER;
		else
			this.filter = filter;
	}

	private void loadFromFile(File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
			return;
		}

		BufferedReader r = new BufferedReader(new FileReader(f));
		String l = r.readLine();

		DefaultWordTokenizer t = new DefaultWordTokenizer();
		t.setStopSigns(new String[] { ";" });

		while (l != null) {
			// System.out.println(l);
			if (l.startsWith("thingsInClass=")) {
				thingsInClass = Integer.parseInt(l.substring("thingsInClass="
						.length()));
			} else if (l.startsWith("thingsNotInClass=")) {
				thingsNotInClass = Integer.parseInt(l
						.substring("thingsNotInClass=".length()));
			} else {
				t.setText(l);
				String word = t.nextToken();
				this.wordsInClass.put(word, Integer.parseInt(t.nextToken()));
				this.wordsNotInClass.put(word, Integer.parseInt(t.nextToken()));
			}
			l = r.readLine();
		}

		r.close();
		recalculateBayesValues();
	}

	public void storeToFile() throws IOException {
		BufferedWriter w = new BufferedWriter(new FileWriter(this.file));

		w.write("thingsInClass=" + this.thingsInClass);
		w.newLine();
		w.write("thingsNotInClass=" + this.thingsNotInClass);
		w.newLine();

		for (String word : getCombinedWordList()) {
			w.write(word + ";" + getValue(this.wordsInClass, word) + ";"
					+ getValue(this.wordsNotInClass, word));
			w.newLine();
		}
		w.flush();
		w.close();
	}

	@Override
	public double getMinimalValue() {
		return 0;
	}

	@Override
	public double getMaximumValue() {
		return 1.0;
	}

	@Override
	public double classify(String text) throws IllegalArgumentException {
		String[] words = prepareText(text);

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

	private Double[] computeBayesianValues(String[] words) {
		Double[] ret = new Double[words.length];
		for (int i = 0; i < words.length; i++) {
			ret[i] = getBayesValueFor(words[i]);
		}
		return ret;
	}

	private Double getBayesValueFor(String word) {
		Double bayesianVal = this.wordBayesValue.get(word);
		if (bayesianVal == null)
			return this.unknownWordValue;
		if (Double.isNaN(bayesianVal)) {
			// System.err.println("NaN - " + word);
			return this.unknownWordValue;
		}
		return bayesianVal;
	}

	private String[] prepareText(String text) {
		String[] words = this.tokenizer.tokenize(getStemmer().stemmText(text));

		return this.filter.filterStopWords(words);
	}

	@Override
	public boolean isMatch(String text) throws IllegalArgumentException {
		return classify(text) > getMatchValue();
	}

	@Override
	public void setMatchValue(double value) {
		this.matchValue = value;
	}

	@Override
	public double getMatchValue() {
		return this.matchValue;
	}

	@Override
	public TableModel explainClassification(String text) {
		String[] words = null;
		if (text != null)
			words = prepareText(text);
		else {
			words = this.wordBayesValue.keySet().toArray(new String[0]);
		}

		String[] columns = { "Word", "F_in", "F_nin", "Probability" };
		Object[][] data = new Object[words.length][4];
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			data[i][0] = word;
			data[i][1] = this.wordsInClass.get(word);
			data[i][2] = this.wordsNotInClass.get(word);
			data[i][3] = getBayesValueFor(word);
		}

		return new DefaultTableModel(data, columns) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				if (column == 1 || column == 2)
					return Integer.class;
				if (column == 3)
					return Double.class;
				return String.class;
			};
		};
	}

	public String explainClassificationString(String text) {
		String[] words = null;
		if (text != null)
			words = prepareText(text);
		else {
			words = this.wordBayesValue.keySet().toArray(new String[0]);
		}

		StringBuffer sb = new StringBuffer();
		for (String w : words) {
			sb.append(w + ": " + getBayesValueFor(w) + "\n");
		}
		return sb.toString();
	}

	@Override
	public void learnInClass(String text) {
		// remember the amount of things in class:
		this.thingsInClass++;
		// increase the frequency of being in class for each word
		increaseWordFrequency(text, this.wordsInClass);

		// System.out.println(this);
	}

	private void increaseWordFrequency(String text,
			Map<String, Integer> wordFrequencyMap) {
		String[] words = prepareText(text);
		for (String w : words) {
			Integer i = wordFrequencyMap.get(w);
			if (i == null)
				i = 0;
			i++;
			wordFrequencyMap.put(w, i);
		}
		recalculateBayesValues();
	}

	public IStemmer getStemmer() {
		if (this.stemmer == null)
			this.stemmer = IStemmer.NULL_STEMMER;
		return this.stemmer;
	}

	public void setStemmer(IStemmer stemmer) {
		this.stemmer = stemmer;
	}

	@Override
	public void learnNotInClass(String text) {
		// remember the amount of things in class:
		this.thingsNotInClass++;
		// increase the frequency of being in class for each word
		increaseWordFrequency(text, this.wordsNotInClass);
	}

	private void recalculateBayesValues() {
		for (String word : getCombinedWordList()) {
			double wordInClass = getValue(this.wordsInClass, word);
			double wordNotInClass = getValue(this.wordsNotInClass, word);

			// Avoid division by 0:
			int tmpInClass = this.thingsInClass;
			int tmpNotInClass = this.thingsNotInClass;

			if (tmpInClass == 0)
				tmpInClass++;
			if (tmpNotInClass == 0)
				tmpNotInClass++;

			double a = (this.proClassBias * wordInClass) / tmpInClass;
			double b = wordNotInClass / tmpNotInClass;

			double bayesValue = a / (a + b);

			// System.out.println(word + ": " + bayesValue);
			wordBayesValue.put(word, bayesValue);
		}
		try {
			if (this.isAutosave)
				storeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getValue(Map<String, Integer> map, String key) {
		if (map.containsKey(key))
			return map.get(key);
		return 0;
	}

	private List<String> getCombinedWordList() {
		Vector<String> ret = new Vector<String>(this.wordsInClass.keySet());
		for (String w : this.wordsNotInClass.keySet())
			if (!ret.contains(w))
				ret.add(w);
		return ret;
	}

	@Override
	public void init(File initData) throws Exception {
		this.file = initData;
		loadFromFile(this.file);
	}

	@Override
	public String toString() {
		String[] words = null;
		words = this.wordBayesValue.keySet().toArray(new String[0]);

		StringBuffer sb = new StringBuffer();
		for (String w : words) {
			sb.append(w + ": " + getBayesValueFor(w) + "\n");
		}
		return sb.toString();
	}

	@Override
	public int thingsInClass() {
		return this.thingsInClass;
	}

	@Override
	public int thingsNotInClass() {
		return this.thingsNotInClass;
	}

	@Override
	public void clear() throws IOException {
		this.thingsInClass = 0;
		this.thingsNotInClass = 0;
		this.wordsInClass.clear();
		this.wordsNotInClass.clear();
		this.wordBayesValue.clear();
		storeToFile();
	}

	public void setProClassBias(double i) {
		this.proClassBias = i;
	}

	public double getProClassBias() {
		return this.proClassBias;
	}

	public double getUnknownWordValue() {
		return unknownWordValue;
	}

	public void setUnknownWordValue(double unknownWordValue) {
		this.unknownWordValue = unknownWordValue;
	}

	public void setAutosave(boolean isAutosave) {
		this.isAutosave = isAutosave;
	}

}
