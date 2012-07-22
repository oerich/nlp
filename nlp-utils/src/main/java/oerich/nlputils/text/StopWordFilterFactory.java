package oerich.nlputils.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import oerich.nlputils.NLPProperties;
import oerich.nlputils.tokenize.AbstractTokenizerAdapter;
import oerich.nlputils.tokenize.DefaultWordTokenizer;
import oerich.nlputils.tokenize.ITokenizer;



// TODO the factory is mixed up with the implementation. This has to be cleaned up.
public final class StopWordFilterFactory extends AbstractTokenizerAdapter
		implements IStopWordFilter, ITokenizer {

	public static final ITokenizer NULL_TOKENIZER = new AbstractTokenizerAdapter() {

		@Override
		public String[] tokenize(String text) {
			return text.split(" ");
		}
	};

	public static final IStopWordFilter NULL_FILTER = new IStopWordFilter() {

		@Override
		public String[] filterStopWords(String[] text) {
			return text;
		}
	};

	private static StopWordFilterFactory instance;
	private BufferedReader reader;
	private List<String> stopwords;
	private List<String> stopsign;

	private StopWordFilterFactory() {
		stopwords = new LinkedList<String>();
		stopsign = new LinkedList<String>();
		try {
			// stop words
			reader = new BufferedReader(new FileReader(NLPProperties
					.getInstance().getStopwordFilePath()));
			String line = "";

			while ((line = reader.readLine()) != null) {
				String[] stopwordsArray = line.split(";");
				for (int i = 0; i < stopwordsArray.length; i++) {
					stopwords.add(stopwordsArray[i]);
				}
			}
			reader.close();

			// stop sign
			reader = new BufferedReader(new FileReader(NLPProperties
					.getInstance().getStopsignsFilePath()));
			line = "";

			while ((line = reader.readLine()) != null) {
				stopsign.add(line.trim());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a stopword filter. Mostly used for tests. You could always use
	 * the methods of this class instead.
	 * 
	 * @param filename
	 *            name of a file with a list of stopwords. Consider to retrieve
	 *            this name from {@link NLPProperties}.
	 * @return a stopword filter that will filter out all words from the file.
	 */
	public static IStopWordFilter createStopWordFilter(String filename) {
		try {
			DefaultStopWordFilter ret = new DefaultStopWordFilter();
			List<String> stopwords = new LinkedList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";

			while ((line = reader.readLine()) != null) {
				String[] stopwordsArray = line.split(";");
				for (int i = 0; i < stopwordsArray.length; i++) {
					stopwords.add(stopwordsArray[i]);
				}
			}
			reader.close();

			ret.setStopWords(stopwords.toArray(new String[0]));
			return ret;
		} catch (Exception e) {
			System.err.println(StopWordFilterFactory.class.getSimpleName()
					+ ": Could not read file " + filename
					+ ". NULL_TOKENIZER returned.");
			e.printStackTrace();
		}
		return NULL_FILTER;
	}

	/**
	 * Returns a tokenizer that will give single words without any
	 * interpunctuation. Probably it makes sense to use the version without
	 * parameter - then the default file from {@link NLPProperties} is used.
	 * 
	 * @param filename
	 *            name of a file with a list of stopsigns. Consider to retrieve
	 *            this name from {@link NLPProperties}.
	 * @return a tokenizer that will filter out all signs from the file.
	 */
	public static ITokenizer createTokenizer(String filename) {
		try {
			DefaultWordTokenizer ret = new DefaultWordTokenizer();
			List<String> stopsigns = new LinkedList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";

			while ((line = reader.readLine()) != null) {
				stopsigns.add(line.trim());
			}
			reader.close();
			ret.setStopSigns(stopsigns.toArray(new String[0]));
			return ret;
		} catch (Exception e) {
			System.err.println(StopWordFilterFactory.class.getSimpleName()
					+ ": Could not read file " + filename
					+ ". NULL_TOKENIZER returned.");
			e.printStackTrace();
		}
		return NULL_TOKENIZER;
	}

	public static ITokenizer createTokenizer() {
		return createTokenizer(NLPProperties.getInstance()
				.getStopsignsFilePath());
	}

	public static IStopWordFilter getInstance() {
		if (instance == null) {
			instance = new StopWordFilterFactory();
		}
		return instance;
	}

	public String[] filterStopWords(String text) {
		return filterStopWords(tokenize(text));
	}

	@Override
	public String[] filterStopWords(String[] text) {
		LinkedList<String> words = new LinkedList<String>();
		for (String s : text) {
			if (!stopwords.contains(s)) {
				words.add(s);
			}

		}
		return words.toArray(new String[0]);
	}

	@Override
	public String[] tokenize(String text) {
		text = text.toLowerCase();
		// remove stop sign
		for (String sign : stopsign) {
			while (text.indexOf(sign) >= 0)
				text = text.replace(sign, " ");
		}

		return text.split(" ");
	}

}
