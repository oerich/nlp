package oerich.nlputils.dataset.impl;

import java.util.HashMap;
import java.util.Map;

import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.ILearnedWord;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;


class DefaultDataSet implements IDataSet {

	private Map<String, ILearnedWord> learnedCategories = new HashMap<String, ILearnedWord>();

	private int countSecReq;
	private int countNonSecReq;
	private IStemmer stemmer;
	private IStopWordFilter stopWordFilter;

	public DefaultDataSet() {
	}

	@Override
	public String getMostLikelyStereotype(String word) {
		ILearnedWord lw = this.learnedCategories.get(word);
		int max = 0;
		String ret = STEREOTYPE_NOT_KNOWN;

		if (lw == null || lw.getCategories() == null)
			return ret;

		for (String s : lw.getCategories()) {
			// Categories go beyond sec or non_sec!
			if (!KEY_NON_SEC.equals(s) && !KEY_SEC.equals(s)) {
				if (lw.getWeight(s) > max) {
					max = lw.getWeight(s);
					ret = s;
				}
			}
		}
		return ret;
	}

	@Override
	public int getCountNonSecReq() {
		return countNonSecReq;
	}

	@Override
	public int getCountSecReq() {
		return countSecReq;
	}

	@Override
	public int getNonSecValue(String word) {
		return getValue(word, KEY_NON_SEC);
	}

	@Override
	public int getSecValue(String word) {
		return getValue(word, KEY_SEC);
	}

	@Override
	public void addNonSecValue(String word, int countNonSec) {
		addValue(word, countNonSec, KEY_NON_SEC);
	}

	@Override
	public void addSecValue(String word, int countSec) {
		addValue(word, countSec, KEY_SEC);
	}

	@Override
	public int getValue(String word, String category) {
		ILearnedWord learnedWord = this.learnedCategories.get(word);
		if (learnedWord == null)
			return 0;
		return learnedWord.getWeight(category);
	}

	@Override
	public void addValue(String word, int weight, String category) {
		ILearnedWord learnedWord = this.learnedCategories.get(word);
		if (learnedWord == null) {
			learnedWord = new DefaultLearnedWord(word);
			this.learnedCategories.put(word, learnedWord);
		}
		learnedWord.setWeight(category, (int) weight);
	}

	private void learnWord(String word, String category) {
		if (word == null || "".equals(word))
			return;
		ILearnedWord learnedWord = this.learnedCategories.get(word);
		if (learnedWord == null) {
			learnedWord = new DefaultLearnedWord(word);
			this.learnedCategories.put(word, learnedWord);
		}
		learnedWord.setWeight(category, learnedWord.getWeight(category) + 1);
	}

	@Override
	public void setCountNonSecReq(int numberOfNonSecReq) {
		this.countNonSecReq = numberOfNonSecReq;
	}

	@Override
	public void setCountSecReq(int i) {
		this.countSecReq = i;
	}

	@Override
	public void learn(String[] sentence, String... categoryNames) {
		if (sentence.length == 0)
			return;

		// Stem words, if stemmer is set:
		String[] words = getStemmer().stemmWords(sentence);
		for (String s : categoryNames) {
			if (KEY_SEC.equals(s))
				countSecReq++;
			if (KEY_NON_SEC.equals(s))
				countNonSecReq++;
			for (String word : words) {
				learnWord(word, s);
			}
		}
	}

	@Override
	public IStemmer getStemmer() {
		if (this.stemmer == null)
			return IStemmer.NULL_STEMMER;
		return this.stemmer;
	}

	@Override
	public IStopWordFilter getStopWordFilter() {
		// TODO Eric: introduce Null-Filter / Default-Filter and Interface
		if (this.stopWordFilter == null)
			return StopWordFilterFactory.getInstance();
		return this.stopWordFilter;
	}

	@Override
	public void setStemmer(IStemmer stemmer) {
		this.stemmer = stemmer;
	}

	@Override
	public void setStopWordFilter(IStopWordFilter stopWordFilter) {
		this.stopWordFilter = stopWordFilter;
	}

	@Override
	public String[] getWords() {
		return this.learnedCategories.keySet().toArray(new String[0]);
	}

	@Override
	public String[] getCategories(String word) {
		return this.learnedCategories.get(word).getCategories();
	}

	@Override
	public void clear() {
		this.countNonSecReq = 0;
		this.countSecReq = 0;
		this.learnedCategories.clear();
	}

	@Override
	public int getNumberOfLearnedItemsInCategory(String category) {
		throw new RuntimeException("Operation not supported.");
		// TODO do something reasonable here
	}

	@Override
	public int getTotalNumberOfLearnedItems() {
		throw new RuntimeException("Operation not supported.");
		// TODO do something reasonable here
	}

	@Override
	public void setNumberOfLearnedItemsInCategory(String category, int number) {
		throw new RuntimeException("Operation not supported.");
		// TODO do something reasonable here
	}

	@Override
	public void setTotalNumberOfLearnedItems(int number) {
		throw new RuntimeException("Operation not supported.");
		// TODO do something reasonable here
	}

}
