package oerich.nlputils.dataset;

import static org.junit.Assert.assertEquals;
import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.StopWordFilterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TrainDataSetCommandTest {

	@Before
	public void setup() {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
	}

	@After
	public void tearDown() {
		NLPProperties.getInstance().resetToDefault();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTrainFromFile() throws NLPInitializationException {
		TrainDataSetCommand cmd = new TrainDataSetCommand();
		cmd.setFile(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
				+ "reqs-sec.txt");
		cmd.setTokenizer(StopWordFilterFactory
				.createTokenizer(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopsign2.txt"));
		cmd.setFilter(StopWordFilterFactory
				.createStopWordFilter(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopwords.txt"));

		IDataSet dataSet = IDataSetDAO.STANDARD_XML.createDataSet(
				TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "Test_EmptyDataSet.xml", IStemmer.NULL_STEMMER);

		cmd.setDataSet(dataSet);

		cmd.run();
		String[] words = dataSet.getWords();
		// Arrays.sort(words);
		// for (String w : words)
		// System.out.println(w);
		assertEquals(771, words.length);
		assertEquals("approving", words[0]);
		assertEquals(1, dataSet.getCategories("approving").length);
		assertEquals("nonsec", dataSet.getCategories("approving")[0]);
		assertEquals(0, dataSet.getSecValue("approving"));
		assertEquals(1, dataSet.getNonSecValue("approving"));
		assertEquals(1, dataSet.getValue("approving", "nonsec"));
		IDataSetDAO.NEW_XML
				.storeDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "secReq.xml", dataSet);

		IDataSet dataSet2 = IDataSetDAO.NEW_XML
				.createDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "secReq.xml", null);
		assertEquals(771, dataSet2.getWords().length);
		assertEquals("approving", dataSet2.getWords()[0]);
		assertEquals("nonsec", dataSet2.getCategories("approving")[0]);
		assertEquals(0, dataSet2.getSecValue("approving"));
		assertEquals(1, dataSet2.getNonSecValue("approving"));
		assertEquals(1, dataSet2.getValue("approving", "nonsec"));

	}

	@Test
	public void testTrainFromFileWithoutStopWordFilter()
			throws NLPInitializationException {
		TrainDataSetCommand cmd = new TrainDataSetCommand();
		cmd.setFile(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
				+ "reqs-sec.txt");
		cmd.setTokenizer(StopWordFilterFactory
				.createTokenizer(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopsign2.txt"));

		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();

		cmd.setDataSet(dataSet);

		cmd.run();
		assertEquals(811, dataSet.getWords().length);
		assertEquals("approving", dataSet.getWords()[0]);
		assertEquals(1, dataSet.getCategories("approving").length);
		assertEquals("nonsec", dataSet.getCategories("approving")[0]);
		assertEquals(0, dataSet.getSecValue("approving"));
		assertEquals(1, dataSet.getNonSecValue("approving"));
		assertEquals(1, dataSet.getValue("approving", "nonsec"));
		IDataSetDAO.NEW_XML
				.storeDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "secReq.xml", dataSet);

		IDataSet dataSet2 = IDataSetDAO.NEW_XML
				.createDataSet(TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "secReq.xml", null);
		assertEquals(811, dataSet2.getWords().length);
		assertEquals("approving", dataSet2.getWords()[0]);
		assertEquals("nonsec", dataSet2.getCategories("approving")[0]);
		assertEquals(0, dataSet2.getSecValue("approving"));
		assertEquals(1, dataSet2.getNonSecValue("approving"));
		assertEquals(1, dataSet2.getValue("approving", "nonsec"));

	}

}
