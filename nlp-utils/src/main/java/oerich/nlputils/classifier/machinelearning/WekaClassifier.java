package oerich.nlputils.classifier.machinelearning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.NLPInitializationException;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class WekaClassifier implements ILearningClassifier {

	private static final String IN_CLASS = "inClass";
	private static final String NOT_IN_CLASS = "notInClass";
	private Classifier delegate;
	private StringToWordVector filter;
	private Instances data;
	private boolean isUpToDate;
	private int thingsInClass;
	private int thingsNotInClass;

	public WekaClassifier(Classifier delegate) {
		this.delegate = delegate;

		this.filter = new StringToWordVector();
		this.filter.setIDFTransform(true);
		this.filter.setTFTransform(true);
		this.filter.setLowerCaseTokens(true);
		this.filter.setOutputWordCounts(true);

		String nameOfDataset = "MessageClassificationProblem";

		// Create vector of attributes.
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);

		// Add attribute for holding messages.
		attributes.add(new Attribute("Message"));

		// Add class attribute.
		List<String> classValues = new ArrayList<String>(2);
		classValues.add(IN_CLASS);
		classValues.add(NOT_IN_CLASS);
		attributes.add(new Attribute("Class", classValues));

		// Create dataset with initial capacity of 100, and set index of class.
		this.data = new Instances(nameOfDataset, attributes, 100);
		this.data.setClassIndex(this.data.numAttributes() - 1);
	}

	public WekaClassifier() {
		this(new SMO());

		RBFKernel kernel = new RBFKernel();
		kernel.setGamma(2E-5);
		((SMO) this.delegate).setC(2E3);
		((SMO) this.delegate).setKernel(kernel);
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
	public void init(File initData) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public double classify(String text) throws NLPInitializationException {
		if (isMatch(text))
			return 1;
		return 0;
	}

	@Override
	public boolean isMatch(String text) throws NLPInitializationException {
		try {
			Instance filteredInstance = prepareClassification(text);

			// Get index of predicted class value.
			double predicted = this.delegate.classifyInstance(filteredInstance);

			if (this.data.classAttribute().value((int) predicted)
					.equals(IN_CLASS)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new NLPInitializationException("Classification failed.", e);
		}
	}

	private Instance prepareClassification(String text) throws Exception {
		// Check whether there is training data for the classifier.
		if (this.data.numInstances() == 0)
			throw new Exception("No training data available.");

		// Check whether classifier and filter are up to date.
		if (!this.isUpToDate) {
			System.out.print("Starting training...");
			// Initialize filter and tell it about the input format.
			this.filter.setInputFormat(this.data);

			// Generate word counts from the training data.
			Instances filteredData = Filter.useFilter(this.data, this.filter);

			// Rebuild classifier.
			this.delegate.buildClassifier(filteredData);

			this.isUpToDate = true;
			System.out.println(" done.");
		}
		// Make separate little test set so that message
		// does not get added to string attribute in m_Data.
		Instances testset = this.data.stringFreeStructure();

		// Make message into test instance.
		Instance instance = makeInstance(text, testset);

		// Filter instance.
		this.filter.input(instance);
		Instance filteredInstance = this.filter.output();
		return filteredInstance;
	}

	@Override
	public void setMatchValue(double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getMatchValue() {
		return 0.5;
	}

	@Override
	public TableModel explainClassification(String text) {
		DefaultTableModel ret = new DefaultTableModel();
		ret.setDataVector(
				new String[][] { { "Support Vector Machine has a incomprehensible model." } },
				new String[] { "Error" });
		return ret;
	}

	@Override
	public void learnInClass(String text) {
		learn(text, IN_CLASS);
		this.thingsInClass++;
	}

	private void learn(String text, String classValue) {
		// Make message into instance.
		Instance instance = makeInstance(text, this.data);

		// Set class value for instance.
		instance.setClassValue(classValue);

		// Add instance to training data.
		this.data.add(instance);
		this.isUpToDate = false;
	}

	@Override
	public void learnNotInClass(String text) {
		learn(text, NOT_IN_CLASS);
		this.thingsNotInClass++;
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
	public void clear() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * Method that converts a text message into an instance.
	 * 
	 * @param text
	 *            the message content to convert
	 * @param data
	 *            the header information
	 * @return the generated Instance
	 */
	private Instance makeInstance(String text, Instances data) {
		// Create instance of length two.
		Instance instance = new DenseInstance(2);

		// Set value for message attribute
		Attribute messageAtt = data.attribute("Message");
		instance.setValue(messageAtt, messageAtt.addStringValue(text));

		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);

		return instance;
	}

}
