package oerich.nlputils.dataset.impl;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DefaultCategoryTest {

	private DefaultLearnedWord category;

	@Before
	public void setUp() throws Exception {
		this.category = new DefaultLearnedWord("Test");
	}

	@Test
	public void testGetName() {
		assertEquals("Test", this.category.getName());
	}

	@Test
	public void testWeight() {
		assertEquals(0, this.category.getWeight("test"));
		this.category.setWeight("test", 5);
		assertEquals(5, this.category.getWeight("test"));
		assertEquals(0, this.category.getWeight("test1"));
	}
}
