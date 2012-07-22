package test.java.oerich.nlputils.tokenize;

import static org.junit.Assert.assertEquals;

import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.DefaultWordTokenizer;

import org.junit.Before;
import org.junit.Test;


public class DefaultWordTokenizerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDefaultFilter() {
		DefaultWordTokenizer t = (DefaultWordTokenizer) StopWordFilterFactory
				.createTokenizer("./stopsign2.txt");

		String txt = "This.is a File with\n some; \t funny signs.";
		String[] tokens = t.tokenize(txt);

		// System.out.println(Arrays.toString(tokens));
		assertEquals(8, tokens.length);
	}
}