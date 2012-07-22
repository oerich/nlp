package test.java.oerich.nlputils.tokenize;

import static org.junit.Assert.assertEquals;

import oerich.nlputils.tokenize.ITokenizer;
import oerich.nlputils.tokenize.StandardGermanWordTokenizer;

import org.junit.Test;

public class StandardGermanWordTokenizerTest {

	@Test
	public void testBasics() {
		String text = "Dies ist z.B. ein bisserl <i>Text</i>...";

		StandardGermanWordTokenizer t = new StandardGermanWordTokenizer();
		t.setText(text);

		assertEquals("Dies", t.nextToken());
		assertEquals("ist", t.nextToken());
		assertEquals("z.B", t.nextToken());
		assertEquals("ein", t.nextToken());
		assertEquals("bisserl", t.nextToken());
		assertEquals("i", t.nextToken());
		assertEquals("Text", t.nextToken());
		assertEquals("i", t.nextToken());
	}

	@Test
	public void testSecondInterface() {
		StandardGermanWordTokenizer t = new StandardGermanWordTokenizer();
		String[] tokens = t
				.tokenize("Dies ist z.B. ein bisserl <i>Text</i>...");

		assertEquals(8, tokens.length);

		assertEquals("Dies", tokens[0]);
		assertEquals("ist", tokens[1]);
		assertEquals("z.B", tokens[2]);
		assertEquals("ein", tokens[3]);
		assertEquals("bisserl", tokens[4]);
		assertEquals("i", tokens[5]);
		assertEquals("Text", tokens[6]);
		assertEquals("i", tokens[7]);

	}

	@Test
	public void testBadTokens() {
		String text = "IBM knauss@computer.org 15 www.se.un-hannover.de";

		StandardGermanWordTokenizer t = new StandardGermanWordTokenizer();
		String[] tokens = t.tokenize(text);
		assertEquals(5, tokens.length);

		// TODO I think these should not be returned... :-(
		assertEquals("IBM", tokens[0]);
		assertEquals("knauss", tokens[1]);
		assertEquals("computer.org", tokens[2]);
		assertEquals("www.se.un", tokens[3]);
		assertEquals("hannover.de", tokens[4]);

	}

	@Test
	public void testLastTokenIsBad() {
		String text = "im Zeitruam von August 2010 bis Ende Mai 2010";

		ITokenizer tokenizer = new StandardGermanWordTokenizer();

		tokenizer.setText(text);

		while (tokenizer.nextToken() != null) {
			// This should never be an endless loop
		}

	}
}
