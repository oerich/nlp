package oerich.nlputils.classifier.machinelearning;

import java.io.File;

import oerich.nlputils.classifier.IClassifier;


public interface ILearningClassifier extends IClassifier<File> {

	/**
	 * Train the classifier: <code>text</code> is a thing in this class, the
	 * classifier should give a match.
	 * 
	 * @param text
	 */
	void learnInClass(String text);

	/**
	 * Train the classifier: <code>text</code> is a thing not in this class, the
	 * classifier should give no match.
	 * 
	 * @param text
	 */
	void learnNotInClass(String text);

	/**
	 * Return the number of things learned to be in class.
	 * 
	 * @return the number of things learned to be in class.
	 */
	int thingsInClass();

	/**
	 * Return the number of things learned to be not in class.
	 * 
	 * @return the number of things learned to be not in class.
	 */
	int thingsNotInClass();

	/**
	 * Makes this classifier forgot everything it learned. You might want to
	 * backup training data first.
	 */
	void clear() throws Exception;
}
