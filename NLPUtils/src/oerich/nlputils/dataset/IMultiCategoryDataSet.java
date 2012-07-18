package oerich.nlputils.dataset;

import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.IStopWordFilter;

/**
 * This package is in an inconsistent and experimental state. It basically
 * supports the management of data for the Bayesian and TFxIDF classifiers.
 *
 * @author Eric Knauss
 *
 */
public interface IMultiCategoryDataSet {
	/**
	 * Learn a new word with a fixed weight. This is used for loading a dataset
	 * from file.
	 */
	public void addValue(String word, int weight, String category);

	public void setStemmer(IStemmer stememr);

	public IStemmer getStemmer();

	public void setStopWordFilter(IStopWordFilter stopWordFilter);

	public IStopWordFilter getStopWordFilter();

	public String[] getWords();

	public int getValue(String word, String category);

	/**
	 * Train dataset with categories.
	 *
	 * @param sentence
	 * @param categoryName
	 */
	public abstract void learn(String[] sentence, String... categoryName);

	public abstract String[] getCategories(String word);

	public abstract void clear();

	public abstract int getNumberOfLearnedItemsInCategory(String category);

	public abstract void setNumberOfLearnedItemsInCategory(String category,
			int number);

	public abstract void setTotalNumberOfLearnedItems(int number);

	public abstract int getTotalNumberOfLearnedItems();
}
