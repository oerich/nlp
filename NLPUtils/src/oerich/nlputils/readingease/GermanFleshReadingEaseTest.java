package oerich.nlputils.readingease;

import static org.junit.Assert.*;

import oerich.nlputils.hyphen.TexHyphenator;

import org.junit.Before;
import org.junit.Test;


public class GermanFleshReadingEaseTest {

	private GermanFleshReadingEase readingEase;

	@Before
	public void setUp() throws Exception {
		this.readingEase = new GermanFleshReadingEase(new TexHyphenator());
	}

	@Test
	public void testCalculateReadingEaseForSentence() {
		assertEquals(100.0d, this.readingEase
				.calculateReadingEaseForSentence("Dies ist ein einfacher Text"));
		assertEquals(
				54.31d,
				(Double) this.readingEase
						.calculateReadingEaseForSentence("Dieser individuelle Test-Satz ist verhältnismäßig schwieriger als der Satz zuvor - auch weil er länger ist."),
				0.01d);
		assertEquals(
				10.13d,
				(Double) this.readingEase
						.calculateReadingEaseForSentence("Gefördert werden sowohl Forschungsaufenthalte an Universitäten als auch promotionsbezogene Tätigkeiten außerhalb Deutschlands im Zeitruam von August 2010 bis Ende Mai 2010"),
				0.01d);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullParagraph() {
		this.readingEase.calculateReadingEaseForParagraph(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSentence() {
		this.readingEase.calculateReadingEaseForSentence(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullHyphonator() {
		new GermanFleshReadingEase(null);
	}

	@Test
	public void testCalculateReadingEaseForParagraph() {
		String[] paragraph = new String[] {
				"Weder im Requirements Engineering noch im Erfahrungsmanagement gibt es einen einheitlichen Sprachgebrauch.",
				"Vielmehr existieren verschiedene Schulen, von denen selbst zentrale Begriffe widersprüchlich verwendet werden.",
				"Als Grundlage für diese Arbeit liefert dieses Kapitel zwei Dinge:" };
		assertEquals(25.85d, (Double) this.readingEase
				.calculateReadingEaseForParagraph(paragraph), 0.01d);

		paragraph = new String[] {
				"Weder im Requirements Engineering noch im Erfahrungsmanagement gibt es einen einheitlichen Sprachgebrauch.",
				"Vielmehr existieren verschiedene Schulen, von denen selbst zentrale Begriffe widersprüchlich verwendet werden.",
				"Als Grundlage für diese Arbeit liefert dieses Kapitel zwei Dinge:",
				"" };
		assertEquals(25.85d, (Double) this.readingEase
				.calculateReadingEaseForParagraph(paragraph), 0.01d);
	}

	@Test
	public void testPlausibility() {
		String[] paragraph = new String[] {
				"Weder im Requirements Engineering noch im Erfahrungsmanagement gibt es einen einheitlichen Sprachgebrauch.",
				"Vielmehr existieren verschiedene Schulen, von denen selbst zentrale Begriffe widersprüchlich verwendet werden.",
				"Als Grundlage für diese Arbeit liefert dieses Kapitel zwei Dinge:" };
		String sentence = "Weder im Requirements Engineering noch im Erfahrungsmanagement gibt es einen einheitlichen Sprachgebrauch."
				+ "Vielmehr existieren verschiedene Schulen, von denen selbst zentrale Begriffe widersprüchlich verwendet werden."
				+ "Als Grundlage für diese Arbeit liefert dieses Kapitel zwei Dinge:";

		assertEquals("Para has good readability for a scientific text.",25.85d, (Double) this.readingEase
				.calculateReadingEaseForParagraph(paragraph), 0.01d);
		assertEquals("Funny, but for a sentence, this is very long.",-0.078125d, (Double) this.readingEase
				.calculateReadingEaseForSentence(sentence), 0.01d);
		
	}
}
