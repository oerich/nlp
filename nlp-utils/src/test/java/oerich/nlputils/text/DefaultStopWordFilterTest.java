package oerich.nlputils.text;

import static org.junit.Assert.assertEquals;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.tokenize.DefaultWordTokenizer;
import oerich.nlputils.tokenize.ITokenizer;

import org.junit.Before;
import org.junit.Test;

public class DefaultStopWordFilterTest {

	private DefaultStopWordFilter testfilter;

	@Before
	public void setUp() throws Exception {
		this.testfilter = new DefaultStopWordFilter();
	}

	@Test
	public void testSimpleFiltering() {
		String text = "Dieser Text enthält einige Stopwörter und so.";
		String[] stopwords = { ".", "dieser", "und" };
		this.testfilter.setStopWords(stopwords);
		StringBuffer result = new StringBuffer();

		ITokenizer t = new DefaultWordTokenizer();

		for (String s : this.testfilter.filterStopWords(t.tokenize(text))) {
			result.append(s);
			result.append(" ");
		}
		assertEquals("text enthält einige stopwörter so. ", result.toString());
	}

	@Test
	public void testComplexExample() {
		DefaultStopWordFilter filter = (DefaultStopWordFilter) StopWordFilterFactory
				.createStopWordFilter(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopwords.txt");

		ITokenizer t = StopWordFilterFactory
				.createTokenizer(TestEnvironmentConstants.RESOURCE_PATH_NAME
						+ "stopsigns.txt");
		String[] s = filter.filterStopWords(t
				.tokenize("Satz mit einigen \"Stopwörtern\""));
		// for (String w : s)
		// System.out.println(w);
		assertEquals(3, s.length);
		assertEquals("satz", s[0]);
		assertEquals("mit", s[1]);
		assertEquals("stopwörtern", s[2]);
	}
}
