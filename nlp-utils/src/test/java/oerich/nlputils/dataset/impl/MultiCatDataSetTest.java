package oerich.nlputils.dataset.impl;

import static org.junit.Assert.assertEquals;
import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.Stemmer;
import oerich.nlputils.text.StopWordFilterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultiCatDataSetTest {

	private MultiCatDataSet dataSet;

	@Before
	public void setUp() throws Exception {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
		this.dataSet = new MultiCatDataSet();
	}

	@After
	public void tearDown() {
		NLPProperties.getInstance().resetToDefault();
	}

	@Test
	public void testGetSetStemmer() {
		assertEquals(IStemmer.NULL_STEMMER, this.dataSet.getStemmer());
		this.dataSet.setStemmer(null);
		assertEquals(IStemmer.NULL_STEMMER, this.dataSet.getStemmer());
		Stemmer stememr = new Stemmer();
		this.dataSet.setStemmer(stememr);
		assertEquals(stememr, this.dataSet.getStemmer());
	}

	@Test
	public void testGetSetStopWordFilter() {
		assertEquals(StopWordFilterFactory.NULL_FILTER,
				this.dataSet.getStopWordFilter());
		this.dataSet.setStopWordFilter(null);
		assertEquals(StopWordFilterFactory.NULL_FILTER,
				this.dataSet.getStopWordFilter());
		this.dataSet.setStopWordFilter(StopWordFilterFactory.getInstance());
		assertEquals(StopWordFilterFactory.getInstance(),
				this.dataSet.getStopWordFilter());
	}

	@Test
	public void testAddValue() {
		assertEquals(0, this.dataSet.getValue("Neu", "Test"));

		this.dataSet.addValue("Neu", 1, "Test");
		assertEquals(1, this.dataSet.getWords().length);
		assertEquals("Neu", this.dataSet.getWords()[0]);
		assertEquals(1, this.dataSet.getCategories("Neu").length);
		assertEquals("Test", this.dataSet.getCategories("Neu")[0]);

		this.dataSet.addValue("Neu", 2, "Test2");
		assertEquals(1, this.dataSet.getWords().length);
		assertEquals("Neu", this.dataSet.getWords()[0]);
		assertEquals(2, this.dataSet.getCategories("Neu").length);
		assertEquals("Test", this.dataSet.getCategories("Neu")[0]);
		assertEquals("Test2", this.dataSet.getCategories("Neu")[1]);

		assertEquals(2, this.dataSet.getValue("Neu", "Test2"));
		assertEquals(1, this.dataSet.getValue("Neu", "Test"));
	}

	@Test
	public void testClear() {
		this.dataSet.addValue("Neu", 1, "Test");
		assertEquals(1, this.dataSet.getWords().length);
		assertEquals("Neu", this.dataSet.getWords()[0]);
		assertEquals(1, this.dataSet.getCategories("Neu").length);
		assertEquals("Test", this.dataSet.getCategories("Neu")[0]);

		this.dataSet.clear();
		assertEquals(0, this.dataSet.getWords().length);
		assertEquals(0, this.dataSet.getCategories("Neu").length);
	}

	@Test
	public void testNumberOfLearnedItemsInCategory() {
		assertEquals(0, this.dataSet.getNumberOfLearnedItemsInCategory("Test"));
		this.dataSet.setNumberOfLearnedItemsInCategory("Test", 13);
		assertEquals(13, this.dataSet.getNumberOfLearnedItemsInCategory("Test"));

	}

	@Test
	public void testTotalNumberOfLearnedItems() {
		assertEquals(0, this.dataSet.getTotalNumberOfLearnedItems());
		this.dataSet.setTotalNumberOfLearnedItems(15);
		assertEquals(15, this.dataSet.getTotalNumberOfLearnedItems());
	}

	@Test
	public void testLearn() {
		assertEquals(0, this.dataSet.getValue("system", "Test"));
		String teststring = "The system shall learn categories from categorized requirements.";
		this.dataSet.learn(
				StopWordFilterFactory.NULL_TOKENIZER.tokenize(teststring),
				"Test");
		assertEquals(1, this.dataSet.getValue("system", "Test"));

		assertEquals(8, this.dataSet.getWords().length);
		assertEquals(1, this.dataSet.getCategories("system").length);

		assertEquals(1, this.dataSet.getNumberOfLearnedItemsInCategory("Test"));
		assertEquals(0, this.dataSet.getNumberOfLearnedItemsInCategory("Test1"));
		assertEquals(1, this.dataSet.getTotalNumberOfLearnedItems());

	}

}
