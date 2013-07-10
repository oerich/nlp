package oerich.nlputils.classifier.machinelearning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.table.TableModel;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NewBayesianClassifierTest {

	private ILearningClassifier classifier;
	private File tempFile;

	@Before
	public void setUp() throws Exception {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
		tempFile = new File("TEST-NewBayesianClassifierTest.tmp");
		this.classifier = new NewBayesianClassifier();
		this.classifier.init(tempFile);
	}

	@After
	public void tearDown() {
		assertTrue(tempFile.delete());
		NLPProperties.getInstance().resetToDefault();
	}

	@Test
	public void testBasics() {
		assertEquals(1.0, classifier.getMaximumValue(), 0.001);
		assertEquals(0.0, classifier.getMinimalValue(), 0.001);
	}

	@Test
	public void testWithEmtpyDataSet() throws NLPInitializationException {
		assertEquals(0.5,
				classifier.classify("Use logs in with username and password."),
				0.001);
	}

	@Test
	public void seriousTest() throws NLPInitializationException {

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");
		classifier.learnNotInClass("User writes comments.");
		classifier.learnNotInClass("System displays current time.");

		assertEquals(0.5, classifier.classify("Dies ist einfach nur ein Text"),
				0.01);
		assertEquals(0.99,
				classifier.classify("User registers username and password"),
				0.01);
		assertEquals(0.01,
				classifier.classify("System shows message of the day"), 0.01);
		assertEquals(1.0,
				classifier.classify("User logs in with username and password"),
				0.01);
	}

	@Test
	public void testOnesidedTraining() throws NLPInitializationException {

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");

		assertEquals(0.5, classifier.classify("Dies ist einfach nur ein Text"),
				0.01);
		assertEquals(0.99,
				classifier.classify("User registers username and password"),
				0.01);
		assertEquals(0.5,
				classifier.classify("System shows message of the day"), 0.01);
		assertEquals(1.0,
				classifier.classify("User logs in with username and password"),
				0.01);
	}

	@Test
	public void testIsMatch() throws IllegalArgumentException, Exception {
		assertEquals(classifier.getMatchValue(), 0.9, 0.01);

		classifier.setMatchValue(0.8);
		assertEquals(classifier.getMatchValue(), 0.8, 0.01);

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");

		assertFalse(classifier.isMatch("Dies ist einfach nur ein Text"));
		assertTrue(classifier.isMatch("User registers username and password"));

	}

	@Test
	public void testExplanation() {
		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");
		classifier.learnNotInClass("User writes comments.");
		classifier.learnNotInClass("System displays current time.");

		TableModel tm = classifier
				.explainClassification("User writes creditcard number and current time");

		assertEquals("Word", tm.getColumnName(0));
	}

	@Test
	public void testClear() throws Exception {
		assertTrue(this.tempFile.exists());

		BufferedReader r = new BufferedReader(new FileReader(this.tempFile));

		String line = r.readLine();
		int lines = 0;

		while (line != null) {
			lines++;
			line = r.readLine();
		}
		r.close();

		assertEquals(0, lines);

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");
		classifier.learnNotInClass("User writes comments.");
		classifier.learnNotInClass("System displays current time.");

		r = new BufferedReader(new FileReader(this.tempFile));

		line = r.readLine();
		lines = 0;

		while (line != null) {
			lines++;
			line = r.readLine();
		}
		r.close();

		assertEquals(25, lines);

		classifier.clear();

		r = new BufferedReader(new FileReader(this.tempFile));

		line = r.readLine();
		lines = 0;

		while (line != null) {
			lines++;
			// System.out.println(line);
			line = r.readLine();
		}
		r.close();
		assertEquals(2, lines);
	}

	@Test
	public void testSecreqCPN() throws Exception {
		NewBayesianClassifier classifier = new NewBayesianClassifier();
		File dataset = new File(FileUtils.getTempDirectory(), "CPN-dataset.txt");
		dataset.delete();
		classifier.init(dataset);

		// Load the requirements from file
		BufferedReader r = new BufferedReader(new FileReader(
				TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME + "CPN.csv"));
		String line = r.readLine();

		// Train the classifier
		while (line != null) {
			String[] vals = line.split(";");
			if ("sec".equals(vals[1])) {
				classifier.learnInClass(vals[0]);
			} else {
				classifier.learnNotInClass(vals[0]);
			}
			line = r.readLine();
		}
		r.close();

		// test it
		assertFalse(classifier
				.isMatch("A CND may use the CNG to access NGN services."));
		assertTrue(classifier
				.isMatch("A CPN-user or external NGN user attempting to invoke a CNG-mediated service, including transparent routing, shall be identified and authenticated by the CNG before being granted access to the service."));
	}

	@Test
	public void testSecReqHomeNetwork() throws Exception {
		NewBayesianClassifier classifier = new NewBayesianClassifier();
		File dataset = new File(FileUtils.getTempDirectory(),
				"homeNetwork-dataset.txt");
		dataset.delete();
		classifier.init(dataset);

		// Load the requirements from file
		BufferedReader r = new BufferedReader(new FileReader(
				TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "homeNetwork-complete.csv"));
		String line = r.readLine();

		// Train the classifier
		while (line != null) {
			String[] vals = line.split(";");
			if ("sec".equals(vals[2])) {
				classifier.learnInClass(vals[1]);
			} else {
				classifier.learnNotInClass(vals[1]);
			}
			line = r.readLine();
		}
		r.close();

		// test it
		assertFalse(classifier
				.isMatch("A CND may use the CNG to access NGN services."));
		assertTrue(classifier
				.isMatch("A CPN-user or external NGN user attempting to invoke a CNG-mediated service, including transparent routing, shall be identified and authenticated by the CNG before being granted access to the service."));
	}
	
	@Test
	public void testBayesianRule() throws IOException {
		NewBayesianClassifier classifier = new NewBayesianClassifier();
		classifier.setAutosave(false);
		classifier.setProClassBias(1);
		classifier.learnInClass("a b b");
		classifier.learnNotInClass("a c");
		
		assertEquals(0.5, classifier.getBayesValueFor("a"),0.001);
		assertEquals(1, classifier.getBayesValueFor("b"),0.001);
		assertEquals(0, classifier.getBayesValueFor("c"),0.001);
		
		classifier.learnNotInClass("b c");
		
		assertEquals(0.667, classifier.getBayesValueFor("a"),0.001);
		assertEquals(0.8, classifier.getBayesValueFor("b"),0.001);
		assertEquals(0, classifier.getBayesValueFor("c"),0.001);
	}
}
