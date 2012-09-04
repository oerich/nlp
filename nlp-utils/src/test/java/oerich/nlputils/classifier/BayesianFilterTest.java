package oerich.nlputils.classifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.table.TableModel;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BayesianFilterTest {

	private ITokenizer tokenizer;
	private IDataSet dataSet;

	@Before
	public void setup() {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
		dataSet = IDataSetDAO.NEW_XML.createDataSet();
		tokenizer = StopWordFilterFactory
				.createTokenizer(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopsign2.txt");
	}

	@After
	public void tearDown() {
		NLPProperties.getInstance().resetToDefault();
	}

	@Test
	public void testBasics() {
		BayesianFilter testFilter = new BayesianFilter();
		assertEquals(1.0, testFilter.getMaximumValue(), 0.001);
		assertEquals(0.0, testFilter.getMinimalValue(), 0.001);
		testFilter.setDataSet(null);
	}

	@Test
	public void testWithEmtpyDataSet() throws NLPInitializationException {
		BayesianFilter testFilter = new BayesianFilter();

		assertEquals(0.5,
				testFilter.classify("Use logs in with username and password."),
				0.001);
	}

	@Test
	public void seriousTest() {
		tokenizer = StopWordFilterFactory.createTokenizer();

		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");
		dataSet.learn(
				tokenizer.tokenize("User sends private data via internet."),
				"sec");
		dataSet.learn(tokenizer
				.tokenize("User add creditcard number to user profile."), "sec");
		dataSet.learn(tokenizer.tokenize("User writes comments."), "nonsec");
		dataSet.learn(tokenizer.tokenize("System displays current time."),
				"nonsec");

		BayesianFilter testFilter = new BayesianFilter();
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

		// We can do the same, based on another interface:
		assertEquals(0.5, testFilter.classify("Dies ist einfach nur ein Text"),
				0.01);
		assertEquals(0.99,
				testFilter.classify("User registers username and password"),
				0.01);
		assertEquals(0.01,
				testFilter.classify("System shows message of the day"), 0.01);
		assertEquals(1.0,
				testFilter.classify("User logs in with username and password"),
				0.01);
	}

	@Test
	public void testEPurse() throws NLPInitializationException {
		IDataSet dataSet = IDataSetDAO.NEW_XML
				.createDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "ePurse-dataset.xml");

		BayesianFilter testFilter = new BayesianFilter();
		testFilter.setDataSet(dataSet);

		assertEquals(0.5,
				testFilter.computeBayes("Dies ist einfach nur ein Text"), 0.01);
		assertEquals(
				0.5,
				testFilter.computeBayes("User registers username and password"),
				0.01);
		assertFalse(((Double) Double.NaN).equals(testFilter
				.computeBayes("System shows message of the day")));
		assertEquals(0.49,
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
		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");
		dataSet.learn(
				tokenizer.tokenize("User sends private data via internet."),
				"sec");
		dataSet.learn(tokenizer
				.tokenize("User add creditcard number to user profile."), "sec");
		dataSet.learn(tokenizer.tokenize("User writes comments."), "nonsec");
		dataSet.learn(tokenizer.tokenize("System displays current time."),
				"nonsec");

		dataSet.learn(
				tokenizer
						.tokenize("We can use other category names but should not be surprised if this is not useful for classifying sec-reqs."),
				"xyz");

		BayesianFilter testFilter = new BayesianFilter();
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
		assertEquals(testFilter.getUnknownWordValue(),
				testFilter.getBayesValueFor("category"), 0.01);
	}

	@Test
	public void testGetDataSet() {
		BayesianFilter f = new BayesianFilter();
		assertNull(f.getDataSet());
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();
		f.setDataSet(dataSet);
		assertEquals(dataSet, f.getDataSet());
	}

	@Test
	public void testGetComparator() {
		BayesianFilter f = new BayesianFilter();
		assertEquals(BayesianFilter.BEST_VALUES, f.getComparator());
		f.setComparator(BayesianFilter.HIGHEST_VALUES);
		assertEquals(BayesianFilter.HIGHEST_VALUES, f.getComparator());
	}

	@Test
	public void testProSecBias() {
		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");
		dataSet.learn(
				tokenizer.tokenize("User sends private data via internet."),
				"sec");
		dataSet.learn(tokenizer
				.tokenize("User add creditcard number to user profile."), "sec");
		dataSet.learn(tokenizer.tokenize("User writes comments."), "nonsec");
		dataSet.learn(tokenizer.tokenize("System displays current time."),
				"nonsec");

		BayesianFilter testFilter = new BayesianFilter();
		testFilter.setDataSet(dataSet);
		assertEquals(0.842,
				testFilter.computeBayes("User shows message of the day"), 0.01);

		// Pro Sec Bias does nothing, unless we recompute the dataset.
		testFilter.setProSecBias(4.0);
		assertEquals(0.842,
				testFilter.computeBayes("User shows message of the day"), 0.01);

		testFilter.setDataSet(dataSet);
		assertEquals(0.914,
				testFilter.computeBayes("User shows message of the day"), 0.01);
	}

	@Test
	public void testExplain() throws Exception {
		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");

		BayesianFilter testFilter = new BayesianFilter();
		testFilter.init(dataSet);
		TableModel tm = testFilter
				.explainClassification("User logs into system");
		assertEquals("Word", tm.getColumnName(0));
		assertEquals("Probability", tm.getColumnName(1));
		assertEquals(1.0, tm.getValueAt(0, 1));
		assertEquals(1.0, tm.getValueAt(1, 1));
		assertEquals(0.5, tm.getValueAt(2, 1));
		assertEquals(0.5, tm.getValueAt(3, 1));
		assertEquals(Double.class, tm.getColumnClass(1));
		assertEquals(String.class, tm.getColumnClass(0));
	}

	@Test
	public void testWithMatchValue() {
		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");

		BayesianFilter bf = new BayesianFilter();
		bf.setDataSet(dataSet);
		assertEquals(0.9, bf.getMatchValue(), 0.001);
		bf.setMatchValue(0.6);
		assertEquals(0.6, bf.getMatchValue(), 0.001);

		try {
			bf.setMatchValue(-2);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Value must be between 0 and 1.", e.getMessage());
		}

		assertEquals(0.999, bf.classify("User logs into system"), 0.001);
		assertTrue(bf.isMatch("User logs into system"));
		bf.setMatchValue(1.0);
		assertFalse(bf.isMatch("User logs into system"));
	}

	@Test
	public void testSetUnknownWordValue() {
		dataSet.learn(
				tokenizer.tokenize("User logs in with username and password."),
				"sec");

		BayesianFilter bf = new BayesianFilter();
		bf.setDataSet(dataSet);
		assertEquals(0.5, bf.getUnknownWordValue(), 0.001);
		assertEquals(0.999, bf.classify("User logs into system"), 0.001);

		bf.setUnknownWordValue(0.001);
		assertEquals(0.001, bf.getUnknownWordValue(), 0.001);

		assertEquals(0.499, bf.classify("User logs into system"), 0.001);
		assertEquals(1.0, bf.explainClassification("User logs into system")
				.getValueAt(0, 1));
		assertEquals(1.0, bf.explainClassification("User logs into system")
				.getValueAt(1, 1));
		assertEquals(0.001, bf.explainClassification("User logs into system")
				.getValueAt(2, 1));
		assertEquals(0.001, bf.explainClassification("User logs into system")
				.getValueAt(3, 1));
	}

}
