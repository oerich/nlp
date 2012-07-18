package oerich.nlputils.tokenize.sentenceboundarydetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

/**
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 */
public class GermanSimpleSentenceBoundaryDetectorTest {

	@Test
	public void testDetectSentenceBoundaries() {

		String sentences = "Hallo Test, was testest du gerade? Ok. Du bist ein Test";
		SimpleSentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector(
				Locale.GERMAN);
		String[] sentenceList = detector.tokenize(sentences);
		assertNotNull(sentenceList);

		StringBuffer buffer = new StringBuffer();

		for (String str : sentenceList) {
			buffer.append(str);
			// System.out.println("+" + str);
		}

		assertEquals(sentences, buffer.toString());
		assertNotNull(detector.tokenize(""));

		try {
			detector.tokenize(null);
			fail("null is invalid input");
		} catch (IllegalArgumentException exception) {
			assertTrue(true);
		}

	}

}
