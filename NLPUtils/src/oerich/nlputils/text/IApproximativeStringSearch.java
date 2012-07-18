package oerich.nlputils.text;

public interface IApproximativeStringSearch {

	/**
	 * Computes the distance between the two Strings a and b. Similar Strings
	 * have a low distance.
	 * 
	 * @param a
	 * @param b
	 * @return the distance between a and b
	 */
	public abstract int getDistance(String a, String b);
}
