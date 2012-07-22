package oerich.nlputils.dictionary;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philipp Förmer
 * 
 */
public class LuceneHunspellDictionaryAdapterTest {

	private LuceneHunspellDictionaryAdapter underTest;

	@Before
	public void setUp() throws Exception {
		underTest = new LuceneHunspellDictionaryAdapter(
				"oerich/nlputils/resource/de_DE_frami.dic",
				"oerich/nlputils/resource/de_DE_frami.aff", true);
	}

	@Test
	public void testIsCorrectSpelled() throws Exception {
		Assert.assertTrue(underTest.isCorrectSpelled("Lorelei"));
		Assert.assertFalse(underTest.isCorrectSpelled("lorelei"));
	}

}
