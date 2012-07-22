package oerich.nlputils.dataset.impl;

import java.util.HashMap;
import java.util.Map;

import oerich.nlputils.dataset.ILearnedWord;
import oerich.nlputils.dataset.IMultiCategoryDataSet;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;


public class MultiCatDataSet implements IMultiCategoryDataSet {

	private Map<String, ILearnedWord> learnedWords = new HashMap<String, ILearnedWord>();
	private Map<String, Integer> categories = new HashMap<String, Integer>();
	private IStemmer stemmer;
	private IStopWordFilter stopWordFilter;
	private int totalNumberOfLearnedItems;

	@Override
	public void addValue(String word, int weight, String category) {
		ILearnedWord learnedWord = this.learnedWords.get(word);
		if (learnedWord == null) {
			learnedWord = new DefaultLearnedWord("word");
			this.learnedWords.put(word, learnedWord);
		}
		learnedWord.setWeight(category, weight);
	}

	@Override
	public void clear() {
		this.learnedWords.clear();
	}

	@Override
	public String[] getCategories(String word) {
		ILearnedWord iLearnedWord = this.learnedWords.get(word);
		if (iLearnedWord == null)
			return new String[0];
		return iLearnedWord.getCategories();
	}

	@Override
	public int getNumberOfLearnedItemsInCategory(String category) {
		Integer ret = this.categories.get(category);
		if (ret == null)
			return 0;
		return ret;
	}

	@Override
	public IStemmer getStemmer() {
		if (this.stemmer == null) {
			this.stemmer = IStemmer.NULL_STEMMER;
		}
		return this.stemmer;
	}

	@Override
	public IStopWordFilter getStopWordFilter() {
		if (this.stopWordFilter == null)
			return StopWordFilterFactory.NULL_FILTER;
		return this.stopWordFilter;
	}

	@Override
	public int getTotalNumberOfLearnedItems() {
		return this.totalNumberOfLearnedItems;
	}

	@Override
	public int getValue(String word, String category) {
		ILearnedWord iLearnedWord = this.learnedWords.get(word);
		if (iLearnedWord == null)
			return 0;
		return iLearnedWord.getWeight(category);
	}

	@Override
	public String[] getWords() {
		return this.learnedWords.keySet().toArray(new String[0]);
	}

	@Override
	public void learn(String[] sentence, String... categoryName) {
		if (sentence.length == 0)
			return;

		// Stem words, if stemmer is set:
		String[] words = getStemmer().stemmWords(sentence);
	
		for (String s : categoryName) {
			Integer i = this.categories.get(s);
			if ( i == null )
				this.categories.put(s, 1);
			else
				this.categories.put(s, i + 1);
			for (String word : words) {
				learnWord(word, s);
			}
		}

		this.totalNumberOfLearnedItems++;
	}
	
	private void learnWord(String word, String category) {
		if (word == null || "".equals(word))
			return;
		ILearnedWord learnedWord = this.learnedWords.get(word);
		if (learnedWord == null) {
			learnedWord = new DefaultLearnedWord(word);
			this.learnedWords.put(word, learnedWord);
		}
		learnedWord.setWeight(category, learnedWord.getWeight(category) + 1);
	}


	@Override
	public void setNumberOfLearnedItemsInCategory(String category, int number) {
		this.categories.put(category, number);
	}

	@Override
	public void setStemmer(IStemmer stemmer) {
		this.stemmer = stemmer;
		if (this.stemmer == null) {
			this.stemmer = IStemmer.NULL_STEMMER;
		}
	}

	@Override
	public void setStopWordFilter(IStopWordFilter stopWordFilter) {
		this.stopWordFilter = stopWordFilter;
	}

	@Override
	public void setTotalNumberOfLearnedItems(int number) {
		this.totalNumberOfLearnedItems = number;
	}

}
