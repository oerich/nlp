package oerich.nlputils.text;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class StemmerTest extends TestCase {

	private IStemmer testStemmer = new Stemmer();

	public void testStemmWord() {
		assertEquals("projekt", this.testStemmer.stemmWord("Projekt"));
		assertEquals("projekt", this.testStemmer.stemmWord("Projekte"));
		assertEquals("user", this.testStemmer.stemmWord("User"));
		assertEquals("user", this.testStemmer.stemmWord("Users"));
		assertEquals("walk", this.testStemmer.stemmWord("walks"));
	}

	public void testStemmWordsArray() {
		String[] testWords = new String[] { "users", "Projekte", "walks" };
		String[] stemmedWords = this.testStemmer.stemmWords(testWords);
		assertEquals(testWords.length, stemmedWords.length);
		assertEquals("user", stemmedWords[0]);
		assertEquals("projekt", stemmedWords[1]);
		assertEquals("walk", stemmedWords[2]);

		testWords = new String[] { "users", "read", "details" };
		stemmedWords = this.testStemmer.stemmWords(testWords);
		assertEquals(testWords.length, stemmedWords.length);
		assertEquals("user", stemmedWords[0]);
		assertEquals("read", stemmedWords[1]);
		assertEquals("detail", stemmedWords[2]);

	}

	public void testStemmWordsList() {
		List<String> testWords = new LinkedList<String>();
		testWords.add("users");
		testWords.add("Projekte");
		testWords.add("walks");

		List<String> stemmedWords = this.testStemmer.stemmWords(testWords);
		assertEquals(testWords.size(), stemmedWords.size());
		assertEquals("user", stemmedWords.get(0));
		assertEquals("projekt", stemmedWords.get(1));
		assertEquals("walk", stemmedWords.get(2));
	}

	public void testStemmText() {
		String testText = "Some users want to edit texts in their projects.";
		assertEquals("some user want to edit text in their project",
				this.testStemmer.stemmText(testText));
	}
}
