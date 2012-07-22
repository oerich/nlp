package oerich.nlputils.classifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;

public class SimpleGermanTextClassifierTest {

	private SimpleGermanTextClassifier classifier;

	@Before
	public void setUp() throws Exception {
		if (classifier == null)
			classifier = new SimpleGermanTextClassifier();
	}

	@Test
	public void testClassify() {
		assertEquals(
				0.666,
				classifier
						.classify("Dies ist deutscher Text with few english words"),
				0.01);
	}

	@Test
	public void testIsMatch() {
		assertFalse(classifier
				.isMatch("Dies ist deutscher Text with few english words"));
		assertTrue(classifier.isMatch("Dies ist ein echter deutscher Satz"));
	}

	@Test
	public void testExplainClassification() {
		// nothing to explain:
		TableModel tm = classifier.explainClassification("");
		assertEquals(0, tm.getRowCount());

		// is match:
		tm = classifier.explainClassification("Ein echter deutscher Satz");
		assertEquals("echt", tm.getValueAt(0, 0));
		assertEquals("based on dictionary", true, tm.getValueAt(0, 1));
		assertEquals("deutsch", tm.getValueAt(1, 0));
		assertEquals("based on dictionary", true, tm.getValueAt(1, 1));
		assertEquals("satz", tm.getValueAt(2, 0));
		assertEquals("based on dictionary", true, tm.getValueAt(2, 1));
		assertEquals("Tokenizer filters stop words.", 3, tm.getRowCount());

	}

}
