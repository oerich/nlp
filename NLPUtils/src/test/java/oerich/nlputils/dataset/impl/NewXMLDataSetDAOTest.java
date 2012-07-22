package test.java.oerich.nlputils.dataset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;
import oerich.nlputils.dataset.impl.NewXMLDataSetDAO;
import oerich.nlputils.text.IStemmer;

import org.junit.Before;
import org.junit.Test;


public class NewXMLDataSetDAOTest {

	private IDataSetDAO dataSetDAO;

	@Before
	public void setUp() throws Exception {
		this.dataSetDAO = new NewXMLDataSetDAO();
	}

	@Test(expected = NLPInitializationException.class)
	public void testWithNonExistingDataSet() throws NLPInitializationException {
		this.dataSetDAO.createDataSet("Test_" + getClass().getSimpleName()
				+ ".xlm", IStemmer.NULL_STEMMER);
	}

	@Test
	public void testWriteDataSet() throws NLPInitializationException {

		IDataSet dataSet = this.dataSetDAO.createDataSet();
		String[] sentence = { "user", "logs", "in", "with", "username", "and",
				"password" };
		String[] sentence2 = { "user", "logs", "in", "with", "browser", "via",
				"internet" };

		dataSet.learn(sentence, "sec", "secrecy");
		dataSet.learn(sentence, "sec", "test");
		dataSet.learn(sentence2, "sec", "secrecy");
		dataSet.learn(new String[] { "Benutzer", "macht", "etwas",
				"vollkommen", "unkritisches" }, "nonsec");
		assertEquals(3, dataSet.getCountSecReq());
		assertEquals(1, dataSet.getCountNonSecReq());

		String databaseFileName = "testfiles/Test_NewDataSet.xml";

		File f = new File(databaseFileName);
		// assertFalse(f.exists());
		this.dataSetDAO.storeDataSet(databaseFileName, dataSet);
		assertTrue(f.exists());

		IDataSet dataSet2 = this.dataSetDAO.createDataSet(databaseFileName,
				IStemmer.NULL_STEMMER);
		assertEquals(3, dataSet2.getValue("user", "sec"), 0.01d);
		assertEquals(2, dataSet2.getValue("user", "secrecy"), 0.01d);
		assertEquals(1, dataSet2.getValue("internet", "secrecy"), 0.01d);
		assertEquals(3, dataSet2.getCountSecReq());
		assertEquals(1, dataSet2.getCountNonSecReq());

		assertTrue(f.delete());
	}

	@Test
	public void testCheckFile() {
		assertFalse(this.dataSetDAO
				.checkFileFormat("testfiles/Test_EmptyDataSet.xml"));
		assertFalse(this.dataSetDAO.checkFileFormat("testfiles/Test_"
				+ getClass().getSimpleName() + ".xml"));
		assertTrue(this.dataSetDAO.checkFileFormat("testfiles/secReq.xml"));
	}
	
	@Test
	public void testSetStemmer() throws NLPInitializationException {
		IStemmer stemmer = new IStemmer() {
			
			@Override
			public List<String> stemmWords(List<String> words) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] stemmWords(String[] words) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String stemmWord(String word) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String stemmText(String text) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		IDataSet dataSet = this.dataSetDAO.createDataSet("testfiles/ePurse-dataset.xml", stemmer);
		assertEquals(stemmer, dataSet.getStemmer());
	}
}
