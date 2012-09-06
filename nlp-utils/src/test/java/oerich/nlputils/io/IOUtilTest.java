package oerich.nlputils.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author Philipp Förmer
 * 
 */
public class IOUtilTest {

	private static final String existingResource = "stopsigns.txt";

	@Test
	public void testGetResourceAsStream() throws Exception {
		Assert.assertNotNull(IOUtil.getResourceAsStream(existingResource));
	}

	@Test(expected = IOException.class)
	public void testGetResourceAsStreamForUnknownLocation() throws Exception {
		IOUtil.getResourceAsStream("qwertz987654");
	}

	@Test
	public void testDecorateAsBufferedInputStream() throws Exception {
		InputStream stream = IOUtil.getResourceAsStream(existingResource);
		Assert.assertNotNull(IOUtil.decorateAsBufferedInputStream(stream));

		BufferedInputStream alreadyDecoratedStream = new BufferedInputStream(
				stream);
		Assert.assertSame(alreadyDecoratedStream,
				IOUtil.decorateAsBufferedInputStream(alreadyDecoratedStream));
	}

}
