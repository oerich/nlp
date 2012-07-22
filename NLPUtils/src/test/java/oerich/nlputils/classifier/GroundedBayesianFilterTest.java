package oerich.nlputils.classifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;

import org.junit.Test;

public class GroundedBayesianFilterTest {

	@Test
	public void seriousTest() {
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();

		ITokenizer t = StopWordFilterFactory.createTokenizer();

		dataSet.learn(t.tokenize("User logs in with username and password."),
				"sec");
		dataSet.learn(t.tokenize("User sends private data via internet."),
				"sec");
		dataSet.learn(
				t.tokenize("User add creditcard number to user profile."),
				"sec");
		dataSet.learn(t.tokenize("User writes comments."), "nonsec");
		dataSet.learn(t.tokenize("System displays current time."), "nonsec");

		IBayesianFilter testFilter = new GroundedBayesianFilter();
		testFilter.setDataSet(dataSet);

		assertEquals(0.5,
				testFilter.computeBayes("Dies ist einfach nur ein Text"), 0.01);
		assertEquals(
				0.99,
				testFilter.computeBayes("User registers username and password"),
				0.01);
		assertEquals(0.01,
				testFilter.computeBayes("System shows message of the day"),
				0.01);
		assertEquals(
				1.0,
				testFilter
						.computeBayes("User logs in with username and password"),
				0.01);
	}

	@Test
	public void testEPurse() throws NLPInitializationException {
		IDataSet dataSet = IDataSetDAO.NEW_XML
				.createDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "ePurse-dataset.xml");

		IBayesianFilter testFilter = new GroundedBayesianFilter();
		testFilter.setDataSet(dataSet);

		assertEquals(0.5,
				testFilter.computeBayes("Dies ist einfach nur ein Text"), 0.01);
		assertEquals(
				0.5,
				testFilter.computeBayes("User registers username and password"),
				0.01);
		assertFalse(((Double) Double.NaN).equals(testFilter
				.computeBayes("System shows message of the day")));
		assertEquals(0.75,
				testFilter.computeBayes("System shows message of the day"),
				0.01);
		assertEquals(
				0.5,
				testFilter
						.computeBayes("User logs in with username and password"),
				0.01);
		assertEquals(
				1.0,
				testFilter
						.computeBayes("If this is not a cash load, the funds issuer needs to verify that the funds are requested by the legitimate account owner, which is normally done by the presentation of an enciphered PIN"),
				0.01);
	}

	@Test
	public void testNaN() {
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();
		ITokenizer t = StopWordFilterFactory.createTokenizer("./stopsign2.txt");

		dataSet.learn(t.tokenize("User logs in with username and password."),
				"sec");
		dataSet.learn(t.tokenize("User sends private data via internet."),
				"sec");
		dataSet.learn(
				t.tokenize("User add creditcard number to user profile."),
				"sec");
		dataSet.learn(t.tokenize("User writes comments."), "nonsec");
		dataSet.learn(t.tokenize("System displays current time."), "nonsec");

		dataSet.learn(
				t.tokenize("We can use other category names but should not be surprised if this is not useful for classifying sec-reqs."),
				"xyz");

		IBayesianFilter testFilter = new GroundedBayesianFilter();
		testFilter.setDataSet(dataSet);

		// Does not influence other results
		assertEquals(0.5,
				testFilter.computeBayes("Dies ist einfach nur ein Text"), 0.01);
		assertEquals(
				0.99,
				testFilter.computeBayes("User registers username and password"),
				0.01);
		assertEquals(0.01,
				testFilter.computeBayes("System shows message of the day"),
				0.01);
		assertEquals(
				1.0,
				testFilter
						.computeBayes("User logs in with username and password"),
				0.01);

		// But gives bad data, when we use words only used in xyz-class. We
		// should use the unkownWordValue in such cases.
		assertEquals(
				1.0,
				testFilter
						.computeBayes("User logs in with username category and password"),
				0.001);
	}
}
