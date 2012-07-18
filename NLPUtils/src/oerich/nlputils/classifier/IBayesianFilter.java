package oerich.nlputils.classifier;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import oerich.nlputils.dataset.IDataSet;


/**
 * Bayesian filters use statistical methods to classify objects. They need a
 * training set of classified objects. This implementation works with text and
 * is intended to classify requirements.
 * <p>
 * <b>Preparation phase:</b> For each word of the requirements in the training
 * set the probability is computed that the requirement should be classified if
 * it contains this word. These probabilities are stored in a map
 * </p>
 * <p>
 * <b>Classification phase:</b>
 * <ol>
 * <li>When a requirement shall be classified, it is tokenized into its words.</li>
 * <li>For each word the pre-computed probability is fetched from the map.</li>
 * <li>The 15 best probabilities are selected. This trick is taken from
 * Spam-Filtering, where it helps to classify very long mails.</li>
 * <li>The bayesian combination is computed from the list of 15 best
 * probabilities: (P1 * P2 * ... * P15) / (P1 * P2 * ... * P15 + (1-P1) * (1-P2)
 * * ... * (1-P15))</li>
 * <li>The result is the probability that the requirement belongs in the class
 * represented by this filter. It is a double value between 0 and 1, but tends
 * to be either very close to 0 or 1. It is recommended to classify requirements
 * with a probability >0.9.</li>
 * </p>
 * 
 * @author Eric Knauss
 * 
 */
public interface IBayesianFilter extends IClassifier<IDataSet> {

	/** sort the values by there distance to 0.5 */
	public static final Comparator<Double> BEST_VALUES = new Comparator<Double>() {
		@Override
		public int compare(Double o1, Double o2) {
			double a1 = o1 - 0.5;
			double a2 = o2 - 0.5;

			return Double.compare(a1 * a1, a2 * a2);
		}

	};
	/** sort the values (ascending) */
	public static final Comparator<Double> HIGHEST_VALUES = new Comparator<Double>() {
		@Override
		public int compare(Double o1, Double o2) {
			return Double.compare(o1, o2);
		}
	};

	/**
	 * Returns the probability that the given requirement belongs to the class
	 * represented by this filter.
	 * 
	 * @param reqText
	 * @return
	 */
	public abstract double computeBayes(String reqText);

	/**
	 * Sets the probability for unknown words that a requirement that contains
	 * them belongs to the class represented by this filter. Normally, this
	 * value should be close to 0.5, because unknown words to not carry any
	 * knowledge. Spam-Filters usually use 0.4 to reduce false positives. Our
	 * experiments do not suggest that this makes any difference.
	 * 
	 * @param d
	 */
	public abstract void setUnknownWordValue(double d);

	public abstract double getUnknownWordValue();

	/**
	 * The data set is the representation of the training set. This method
	 * computes the probabilities for all words and returns them.
	 * 
	 * @param dataSet
	 * @return
	 */
	public abstract List<Entry<String, Double>> setDataSet(IDataSet dataSet);

	public abstract IDataSet getDataSet();
}