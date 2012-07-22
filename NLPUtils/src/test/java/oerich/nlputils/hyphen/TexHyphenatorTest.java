package test.java.oerich.nlputils.hyphen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import oerich.nlputils.hyphen.TexHyphenator;

import org.junit.Before;
import org.junit.Test;

public class TexHyphenatorTest {
	private static final String HYPHEN_TABLE_FILEPATH = "oerich/nlputils/resource/dehypha.tex";
	private TexHyphenator hyphonator;

	@Before
	public void setUp() throws Exception {
		this.hyphonator = new TexHyphenator(this.getClass().getClassLoader()
				.getResourceAsStream(HYPHEN_TABLE_FILEPATH));
	}

	@Test
	public void emptyConstructor() throws Exception {
		assertNotNull(new TexHyphenator());
	}

	@Test
	public void testSimpleHyphs() {
		String hyphWord = this.hyphonator.hyphenate("Fachgebiet");
		assertEquals("Fach­ge­biet", hyphWord);
		assertEquals(3, this.hyphonator.syllableCount("Fachgebiet"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitIllegalArgs() throws FileNotFoundException,
			IllegalArgumentException, IOException {
		this.hyphonator = new TexHyphenator(this.getClass().getClassLoader()
				.getResourceAsStream("Mist!"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHyphenIllegalArgs() throws FileNotFoundException,
			IllegalArgumentException, IOException {
		this.hyphonator.hyphenate(null);
	}

	@Test
	public void testCountHyphenIllegalArgs() throws FileNotFoundException,
			IllegalArgumentException, IOException {
		assertEquals(0, this.hyphonator.syllableCount(null));
	}

}
