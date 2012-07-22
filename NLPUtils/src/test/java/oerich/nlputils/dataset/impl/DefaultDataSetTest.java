package oerich.nlputils.dataset.impl;

import static org.junit.Assert.assertEquals;
import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.StopWordFilterFactory;

import org.junit.Before;
import org.junit.Test;

public class DefaultDataSetTest {

	private IDataSet dataSet;

	@Before
	public void setup() {
		this.dataSet = new DefaultDataSet();
	}

	@Test
	public void testWithEmtpyDataSet() throws NLPInitializationException {
		assertEquals(0, dataSet.getCountSecReq(), 0.1d);
		assertEquals(0, dataSet.getCountNonSecReq(), 0.1d);
	}

	@Test
	public void testDelegates() throws NLPInitializationException {
		assertEquals(IStemmer.NULL_STEMMER, dataSet.getStemmer());
		assertEquals(StopWordFilterFactory.getInstance(),
				dataSet.getStopWordFilter());
	}

	@Test
	public void testSecNonSecValues() throws NLPInitializationException {
		dataSet.addSecValue("test", 12);
		dataSet.addNonSecValue("test", 2);

		assertEquals(12, dataSet.getSecValue("test"), 0.01d);
		assertEquals(2, dataSet.getNonSecValue("test"), 0.01d);

		assertEquals(12, dataSet.getValue("test", "sec"), 0.01d);
		assertEquals(2, dataSet.getValue("test", "nonsec"));
		dataSet.addValue("test", 13, "sec");
		assertEquals(13, dataSet.getSecValue("test"), 0.01d);
	}

	@Test
	public void testGetMostLikelyStereotype() throws NLPInitializationException {
		assertEquals("not known", dataSet.getMostLikelyStereotype("test"));
		dataSet.addSecValue("test", 7);
		assertEquals("not known", dataSet.getMostLikelyStereotype("test"));
		dataSet.addValue("test", 2, "cat2");
		dataSet.addValue("test", 4, "cat4");
		dataSet.addValue("test", 5, "cat5");
		assertEquals("cat5", dataSet.getMostLikelyStereotype("test"));
	}

	@Test
	public void testLearnCategories() throws NLPInitializationException {
		String[] sentence = { "user", "logs", "in", "with", "username", "and",
				"password" };
		String[] sentence2 = { "user", "logs", "in", "with", "browser", "via",
				"internet" };

		dataSet.learn(sentence, "sec", "secrecy");
		dataSet.learn(sentence, "sec", "test");
		dataSet.learn(sentence2, "sec", "secrecy");

		assertEquals(3, dataSet.getValue("user", "sec"), 0.01d);
		assertEquals(2, dataSet.getValue("user", "secrecy"), 0.01d);
		assertEquals(1, dataSet.getValue("internet", "secrecy"), 0.01d);

		assertEquals(3, dataSet.getCountSecReq());
		assertEquals(0, dataSet.getCountNonSecReq());
	}

	@Test
	public void testCountSecNonSec() {
		assertEquals(0, dataSet.getCountSecReq());
		assertEquals(0, dataSet.getCountNonSecReq());

		dataSet.learn(new String[] { "sicherheitskritisch" }, "sec");
		dataSet.learn(new String[] { "unbedenklich" }, "nonsec");

		assertEquals(1, dataSet.getCountSecReq());
		assertEquals(1, dataSet.getCountNonSecReq());
	}
}
