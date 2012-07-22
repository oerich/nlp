package oerich.nlputils.text;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WagnerFisherStringSearchTest {

	private WagnerFisherStringSearch wagnerFisher;

	@Before
	public void setUp() throws Exception {
		this.wagnerFisher = new WagnerFisherStringSearch();
	}

	@Test
	public void testSimpleCases() {
		assertEquals(0, this.wagnerFisher.getDistance("test", "test"));
		assertEquals(1, this.wagnerFisher.getDistance("Test", "test"));
		assertEquals(4, this.wagnerFisher.getDistance("Test", ""));
		assertEquals(3, this.wagnerFisher.getDistance("test", "testing"));
		assertEquals(4, this.wagnerFisher.getDistance("", "test"));
		assertEquals(0, this.wagnerFisher.getDistance(null, null));
		assertEquals(0, this.wagnerFisher.getDistance("", null));
		assertEquals(0, this.wagnerFisher.getDistance(null, ""));
		assertEquals(4, this.wagnerFisher.getDistance(null, "test"));
	}
}
