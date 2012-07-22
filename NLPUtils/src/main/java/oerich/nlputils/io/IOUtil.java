package oerich.nlputils.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Defines utility operations for io tasks.
 * 
 * @author Philipp Förmer
 */
public class IOUtil {

	/**
	 * Returns an input stream for the passed resourcePath.
	 * The input stream is created via the thread context class loader.
	 * 
	 * @param resourcePath non null
	 * @return non null
	 * @throws IOException if the resource could not be located
	 */
	public static InputStream getResourceAsStream(String resourcePath) throws IOException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = contextClassLoader.getResourceAsStream(resourcePath);
		if(stream == null) {
			throw new IOException("No resource found under path: " + resourcePath);
		}
		return stream;
	}
	
	/**
	 * Returns the passed stream if it is an instance of {@link BufferedInputStream}, else decorates the passed stream with a {@link BufferedInputStream}.
	 * 
	 * @param stream non null
	 * @return non null
	 */
	public static BufferedInputStream decorateAsBufferedInputStream(InputStream stream) {
		if(stream instanceof BufferedInputStream) {
			return (BufferedInputStream)stream;
		}
		return new BufferedInputStream(stream);		
	}
	
}
