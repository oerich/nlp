package oerich.nlputils.classifier;

import junit.framework.Assert;
import oerich.nlputils.EasyMockBasedTestContext;
import oerich.nlputils.dictionary.Dictionary;
import oerich.nlputils.tokenize.ITokenizer;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philipp Förmer
 * 
 */
public class LanguageMembershipClassifierTest extends EasyMockBasedTestContext {

	private ITokenizer tokenizerMock;

	private Dictionary dictionaryMock;

	private LanguageMembershipClassifier underTest;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		tokenizerMock = mockControl.createMock(ITokenizer.class);
		dictionaryMock = mockControl.createMock(Dictionary.class);
		underTest = new LanguageMembershipClassifier(tokenizerMock,
				dictionaryMock);
	}

	@Test
	public void testGetMaximumValue() throws Exception {
		Assert.assertEquals(1.0, underTest.getMaximumValue(), 0.0);
	}

	@Test
	public void testGetMinimalValue() throws Exception {
		Assert.assertEquals(0.0, underTest.getMinimalValue(), 0.0);
	}

	@Test
	public void testGetSetMatchValue() throws Exception {
		underTest.setMatchValue(0.5);
		Assert.assertEquals(0.5, underTest.getMatchValue(), 0.0);
	}

	@Test
	public void testClassify() throws Exception {
		recordOneMatchAndOneDismatch("asd");

		mockControl.replay();
		Assert.assertEquals(0.5, underTest.classify("asd"), 0.0);
		mockControl.verify();
		mockControl.reset();
	}

	@Test
	public void testIsMatch() throws Exception {
		underTest.setMatchValue(0.5);
		recordOneMatchAndOneDismatch("asd");

		mockControl.replay();
		Assert.assertTrue(underTest.isMatch("asd"));
		mockControl.verify();
		mockControl.reset();

		recordOneMatchAndOneDismatch("asd");
		underTest.setMatchValue(0.75);
		mockControl.replay();
		Assert.assertFalse(underTest.isMatch("asd"));
		mockControl.verify();
	}

	private void recordOneMatchAndOneDismatch(String text) {
		tokenizerMock.setText(text);
		EasyMock.expect(tokenizerMock.nextToken()).andReturn("a");
		EasyMock.expect(dictionaryMock.isCorrectSpelled("a")).andReturn(true);
		EasyMock.expect(tokenizerMock.nextToken()).andReturn("b");
		EasyMock.expect(dictionaryMock.isCorrectSpelled("b")).andReturn(false);
		EasyMock.expect(tokenizerMock.nextToken()).andReturn(null);
	}

}
