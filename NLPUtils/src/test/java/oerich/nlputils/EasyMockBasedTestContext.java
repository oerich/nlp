package oerich.nlputils;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;

/**
 * 
 * @author Philipp Förmer
 * 
 */
public class EasyMockBasedTestContext {

	protected IMocksControl mockControl;

	@Before
	public void setUp() throws Exception {
		mockControl = EasyMock.createControl();
	}

	@After
	public void tearDown() {
		mockControl.reset();
	}
}
