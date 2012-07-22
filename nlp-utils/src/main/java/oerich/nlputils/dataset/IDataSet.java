package oerich.nlputils.dataset;

/**
 * This package is in an inconsistent and experimental state. It basically
 * supports the management of data for the Bayesian and TFxIDF classifiers.
 *
 * @author Eric Knauss
 *
 */
public interface IDataSet extends IMultiCategoryDataSet {

	static final String KEY_NON_SEC = "nonsec";
	static final String KEY_SEC = "sec";
	static final String STEREOTYPE_NOT_KNOWN = "not known";

	/**
	 * Learn a new word with a fixed weight. This is used for loading a dataset
	 * from file.
	 */
	public abstract void addSecValue(String word, int countSec);

	/**
	 * Learn a new word with a fixed weight. This is used for loading a dataset
	 * from file.
	 */
	public abstract void addNonSecValue(String word, int countNonSec);

	public abstract int getSecValue(String word);

	public abstract int getNonSecValue(String word);

	public abstract int getCountSecReq();

	public abstract int getCountNonSecReq();

	/**
	 * Get the category with the highest weight that is not Sec or NonSec.
	 *
	 * @param word
	 * @return
	 */
	public abstract String getMostLikelyStereotype(String word);

	public abstract void setCountSecReq(int numberOfSecReq);

	public abstract void setCountNonSecReq(int numberOfNonSecReq);
}