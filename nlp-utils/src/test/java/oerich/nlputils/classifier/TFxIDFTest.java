package oerich.nlputils.classifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import oerich.nlputils.NLPProperties;
import oerich.nlputils.TestEnvironmentConstants;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TFxIDFTest {

	@Before
	public void setup() {
		NLPProperties.getInstance().setResourcePath(
				TestEnvironmentConstants.RESOURCE_PATH_NAME);
	}

	@After
	public void tearDown() {
		NLPProperties.getInstance().resetToDefault();
	}

	@Test
	public void testComputeTFxIDF() throws Exception {
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();
		dataSet.learn(new String[] { "Benutzer", "meldet", "sich", "mit",
				"Passwort", "an" }, "sec");
		dataSet.learn(new String[] { "Dies", "ist", "eine", "normale",
				"Anforderung" }, "nonsec");
		TFxIDF classifier = new TFxIDF();
		classifier.init(dataSet);
		assertEquals("sec(4.0)",
				classifier
						.computeTFxIDF("Benutzer meldet sich mit Passwort an"));
		assertEquals("nonsec(-2.0)",
				classifier
						.computeTFxIDF("Benutzer hat eine normale Anforderung"));
	}

	@Test
	public void testComputeTFxIDFcomplex() throws Exception {
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();
		dataSet.learn(new String[] { "system", "erlaubt", "es", "das",
				"passwort", "zu", "ändern" }, "sec");
		dataSet.learn(new String[] { "system", "ist", "einfach", "zu",
				"bedienen" }, "nonsec");
		dataSet.learn(new String[] { "system", "zeigt", "den", "angemeldeten",
				"benutzer", "an" }, "nonsec");
		assertEquals(1, dataSet.getNonSecValue("benutzer"));
		dataSet.learn(new String[] { "system", "ist", "durch",
				"verschlüsselungstechnik", "vor", "ausspähen", "des",
				"Passworts", "geschützt" }, "sec");
		TFxIDF classifier = new TFxIDF();
		classifier.init(dataSet);
		assertEquals("nonsec(-2.0)",
				classifier
						.computeTFxIDF("Benutzer meldet sich mit Passwort an"));
		assertEquals("nonsec(-2.0)",
				classifier
						.computeTFxIDF("Benutzer hat eine normale Anforderung"));
		assertEquals(
				"sec(6.0)",
				classifier
						.computeTFxIDF("System erlaubt es dem Benutzer sich mit Benutzername und Passwort anzumelden (Verschlüsselungstechnik)"));
	}

	@Test
	public void testClassifier() throws Exception {
		IDataSet dataSet = IDataSetDAO.NEW_XML.createDataSet();
		dataSet.learn(new String[] { "system", "erlaubt", "es", "das",
				"passwort", "zu", "ändern" }, "sec");
		dataSet.learn(new String[] { "system", "ist", "einfach", "zu",
				"bedienen" }, "nonsec");
		dataSet.learn(new String[] { "system", "zeigt", "den", "angemeldeten",
				"benutzer", "an" }, "nonsec");
		assertEquals(1, dataSet.getNonSecValue("benutzer"));
		dataSet.learn(new String[] { "system", "ist", "durch",
				"verschlüsselungstechnik", "vor", "ausspähen", "des",
				"Passworts", "geschützt" }, "sec");
		TFxIDF classifier = new TFxIDF();
		classifier.init(dataSet);

		assertEquals(-2.0,
				classifier.classify("Benutzer meldet sich mit Passwort an"),
				0.001);
		assertEquals(-2.0,
				classifier.classify("Benutzer hat eine normale Anforderung"),
				0.001);
		assertEquals(
				6.0,
				classifier
						.classify("System erlaubt es dem Benutzer sich mit Benutzername und Passwort anzumelden (Verschlüsselungstechnik)"),
				0.001);

		assertEquals(Double.MAX_VALUE, classifier.getMaximumValue(), 0.001);
		assertEquals(Double.MIN_VALUE, classifier.getMinimalValue(), 0.001);

		assertEquals(0.0, classifier.getMatchValue(), 0.001);
		assertTrue(classifier
				.isMatch("System erlaubt es dem Benutzer sich mit Benutzername und Passwort anzumelden (Verschlüsselungstechnik)"));

		classifier.setMatchValue(7.0);
		assertEquals(7.0, classifier.getMatchValue(), 0.001);
		assertFalse(classifier
				.isMatch("System erlaubt es dem Benutzer sich mit Benutzername und Passwort anzumelden (Verschlüsselungstechnik)"));
	}
}
