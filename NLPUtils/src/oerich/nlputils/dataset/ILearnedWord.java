package oerich.nlputils.dataset;

/**
 * This package is in an inconsistent and experimental state. It basically
 * supports the management of data for the Bayesian and TFxIDF classifiers.
 */
public interface ILearnedWord {

	public String getName();

	public int getWeight(String category);

	public void setWeight(String category, int i);

	public String[] getCategories();

}
