package oerich.nlputils.dataset;

import static org.junit.Assert.assertEquals;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.StopWordFilterFactory;

import org.junit.Before;
import org.junit.Test;


public class TrainDataSetCommandTest {

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTrainFromFile() throws NLPInitializationException {
		TrainDataSetCommand cmd = new TrainDataSetCommand();
		cmd.setFile("testfiles/reqs-sec.txt");
		cmd.setTokenizer(StopWordFilterFactory
				.createTokenizer("./stopsign2.txt"));
		cmd.setFilter(StopWordFilterFactory
				.createStopWordFilter("./stopwords.txt"));

		IDataSet dataSet = IDataSetDAO.STANDARD_XML.createDataSet(
				"testfiles/Test_EmptyDataSet.xml", IStemmer.NULL_STEMMER);

		cmd.setDataSet(dataSet);

		cmd.run();
		assertEquals(771, dataSet.getWords().length);
		assertEquals("approving", dataSet.getWords()[0]);
		assertEquals(1, dataSet.getCategories("approving").length);
		assertEquals("nonsec", dataSet.getCategories("approving")[0]);
		assertEquals(0, dataSet.getSecValue("approving"));
		assertEquals(1, dataSet.getNonSecValue("approving"));
		assertEquals(1, dataSet.getValue("approving", "nonsec"));
		IDataSetDAO.NEW_XML.storeDataSet("testfiles/secReq.xml", dataSet);

		IDataSet dataSet2 = IDataSetDAO.NEW_XML.createDataSet(
				"testfiles/secReq.xml", null);
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
		cmd.setFile("testfiles/reqs-sec.txt");
		cmd.setTokenizer(StopWordFilterFactory
				.createTokenizer("./stopsign2.txt"));

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
		IDataSetDAO.NEW_XML.storeDataSet("testfiles/secReq.xml", dataSet);

		IDataSet dataSet2 = IDataSetDAO.NEW_XML.createDataSet(
				"testfiles/secReq.xml", null);
		assertEquals(811, dataSet2.getWords().length);
		assertEquals("approving", dataSet2.getWords()[0]);
		assertEquals("nonsec", dataSet2.getCategories("approving")[0]);
		assertEquals(0, dataSet2.getSecValue("approving"));
		assertEquals(1, dataSet2.getNonSecValue("approving"));
		assertEquals(1, dataSet2.getValue("approving", "nonsec"));

	}

}
