package oerich.nlputils.classifier.machinelearning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.swing.table.TableModel;

import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WekaSMOClassifierTest {

	private ILearningClassifier classifier;
	private File tempFile;

	@Before
	public void setUp() throws Exception {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
		tempFile = new File("TEST-NewWekaClassifierTest.tmp");
		this.classifier = new WekaClassifier();
		this.classifier.init(tempFile);
	}

	@After
	public void tearDown() {
		if (tempFile.exists())
			assertTrue(tempFile.delete());
		NLPProperties.getInstance().resetToDefault();
	}

	@Test
	public void testBasics() {
		assertEquals(1.0, classifier.getMaximumValue(), 0.001);
		assertEquals(0.0, classifier.getMinimalValue(), 0.001);
	}

	@Test(expected = Exception.class)
	public void testWithEmtpyDataSet() throws Exception {
		assertEquals(0.5,
				classifier.classify("Use logs in with username and password."),
				0.001);
	}

	@Test
	public void seriousTest() throws Exception {

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");
		classifier.learnNotInClass("User writes comments.");
		classifier.learnNotInClass("System displays current time.");

		assertEquals(1.0, classifier.classify("Dies ist einfach nur ein Text"),
				0.01);
		assertEquals(1.0,
				classifier.classify("User registers username and password"),
				0.01);
		assertEquals(1.0,
				classifier.classify("System shows message of the day"), 0.01);
		assertEquals(1.0,
				classifier.classify("User logs in with username and password"),
				0.01);
		assertEquals(1.0, classifier.classify("System displays current time."),
				0.01);
	}

	@Test
	public void testOnesidedTraining() throws Exception {

		classifier.learnInClass("User logs in with username and password.");
		classifier.learnInClass("User sends private data via internet.");
		classifier.learnInClass("User add creditcard number to user profile.");

		assertEquals(1.0, classifier.classify("Dies ist einfach nur ein Text"),
				0.01);
		assertEquals(1.0,
				classifier.classify("User registers username and password"),
				0.01);
		assertEquals(1.0,
				classifier.classify("System shows message of the day"), 0.01);
		assertEquals(1.0,
				classifier.classify("User logs in with username and password"),
				0.01);
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

		assertEquals("Error", tm.getColumnName(0));
	}

	// TODO @Test
	// public void testClear() throws Exception {
	// assertTrue(this.tempFile.exists());
	//
	// BufferedReader r = new BufferedReader(new FileReader(this.tempFile));
	//
	// String line = r.readLine();
	// int lines = 0;
	//
	// while (line != null) {
	// lines++;
	// line = r.readLine();
	// }
	// r.close();
	//
	// assertEquals(0, lines);
	//
	// classifier.learnInClass("User logs in with username and password.");
	// classifier.learnInClass("User sends private data via internet.");
	// classifier.learnInClass("User add creditcard number to user profile.");
	// classifier.learnNotInClass("User writes comments.");
	// classifier.learnNotInClass("System displays current time.");
	//
	// r = new BufferedReader(new FileReader(this.tempFile));
	//
	// line = r.readLine();
	// lines = 0;
	//
	// while (line != null) {
	// lines++;
	// line = r.readLine();
	// }
	// r.close();
	//
	// assertEquals(25, lines);
	//
	// classifier.clear();
	//
	// r = new BufferedReader(new FileReader(this.tempFile));
	//
	// line = r.readLine();
	// lines = 0;
	//
	// while (line != null) {
	// lines++;
	// // System.out.println(line);
	// line = r.readLine();
	// }
	// r.close();
	// assertEquals(2, lines);
	// }

	// TODO @Test
	// public void testSecreqCPN() throws Exception {
	// File dataset = new File(FileUtils.getTempDirectory(), "CPN-dataset.txt");
	// dataset.delete();
	// classifier.init(dataset);
	//
	// // Load the requirements from file
	// BufferedReader r = new BufferedReader(new FileReader(
	// TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME + "CPN.csv"));
	// String line = r.readLine();
	//
	// // Train the classifier
	// while (line != null) {
	// String[] vals = line.split(";");
	// if ("sec".equals(vals[1])) {
	// classifier.learnInClass(vals[0]);
	// } else {
	// classifier.learnNotInClass(vals[0]);
	// }
	// line = r.readLine();
	// }
	// r.close();
	//
	// // test it
	// assertFalse(classifier
	// .isMatch("A CND may use the CNG to access NGN services."));
	// assertTrue(classifier
	// .isMatch("A CPN-user or external NGN user attempting to invoke a CNG-mediated service, including transparent routing, shall be identified and authenticated by the CNG before being granted access to the service."));
	// }

	// TODO @Test
	// public void testSecReqHomeNetwork() throws Exception {
	// File dataset = new File(FileUtils.getTempDirectory(),
	// "homeNetwork-dataset.txt");
	// dataset.delete();
	// classifier.init(dataset);
	//
	// // Load the requirements from file
	// BufferedReader r = new BufferedReader(new FileReader(
	// TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
	// + "homeNetwork-complete.csv"));
	// String line = r.readLine();
	//
	// // Train the classifier
	// String textInClass = "";
	// String textNotInClass = "";
	// while (line != null) {
	// String[] vals = line.split(";");
	// if ("sec".equals(vals[2])) {
	// textInClass = vals[1];
	// classifier.learnInClass(textInClass);
	// } else {
	// textNotInClass = vals[1];
	// classifier.learnNotInClass(textNotInClass);
	// }
	// line = r.readLine();
	// }
	// r.close();
	//
	// // test it
	// assertFalse(classifier.isMatch(textNotInClass));
	// assertTrue(classifier.isMatch(textInClass));
	// assertFalse(classifier
	// .isMatch("A CND may use the CNG to access NGN services."));
	// assertTrue(classifier
	// .isMatch("A CPN-user or external NGN user attempting to invoke a CNG-mediated service, including transparent routing, shall be identified and authenticated by the CNG before being granted access to the service."));
	// }
}
