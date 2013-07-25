package oerich.nlputils.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DuplicateFilterTest {

	@Test
	public void test() {
		DuplicateFilter df = new DuplicateFilter();

		assertEquals(1,
				df.filterStopWords(new String[] { "a", "a", "a" }).length);
		assertEquals(2,
				df.filterStopWords(new String[] { "a", "b", "a" }).length);
	}

}
