package oerich.nlputils.dataset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;
import oerich.nlputils.text.IStemmer;

import org.junit.Before;
import org.junit.Test;

public class StandardXMLDataSetDAOTest {

	private IDataSetDAO dataSetDAO;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		this.dataSetDAO = new StandardXMLDataSetDAO();
	}

	@Test(expected = NLPInitializationException.class)
	public void testWithNonExistingDataSet() throws NLPInitializationException {
		this.dataSetDAO.createDataSet("Test_" + getClass().getSimpleName()
				+ ".xlm", IStemmer.NULL_STEMMER);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testWriteDataSet() throws NLPInitializationException {

		IDataSet dataSet = StandardXMLDataSetDAO.STANDARD_XML.createDataSet(
				TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
						+ "Test_EmptyDataSet.xml", IStemmer.NULL_STEMMER);
		String[] sentence = { "user", "logs", "in", "with", "username", "and",
				"password" };
		String[] sentence2 = { "user", "logs", "in", "with", "browser", "via",
				"internet" };

		dataSet.learn(sentence, "sec", "secrecy");
		dataSet.learn(sentence, "sec", "test");
		dataSet.learn(sentence2, "sec", "secrecy");

		assertEquals(3, dataSet.getCountSecReq());
		assertEquals(0, dataSet.getCountNonSecReq());

		String databaseFileName = TestEnvironmentConstants.TEST_RESOURCE_PATH_NAME
				+ "Test_NewDataSet.xml";

		File f = new File(databaseFileName);
		// assertFalse(f.exists());
		StandardXMLDataSetDAO.STANDARD_XML.storeDataSet(databaseFileName,
				dataSet);
		assertTrue(f.exists());

		IDataSet dataSet2 = StandardXMLDataSetDAO.STANDARD_XML.createDataSet(
				databaseFileName, IStemmer.NULL_STEMMER);
		assertEquals(3, dataSet2.getValue("user", "sec"), 0.01d);
		assertEquals(2, dataSet2.getValue("user", "secrecy"), 0.01d);
		assertEquals(1, dataSet2.getValue("internet", "secrecy"), 0.01d);
		assertEquals(3, dataSet2.getCountSecReq());
		assertEquals(0, dataSet2.getCountNonSecReq());

		assertTrue(f.delete());
	}

}
