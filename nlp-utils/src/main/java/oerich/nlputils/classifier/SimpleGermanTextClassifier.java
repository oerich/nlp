package oerich.nlputils.classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.tokenize.LuceneTokenizerAdapter;

import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.util.Version;


/**
 * Klasse kann Texte auf deutsche Sprache hin klassifizieren. Zur
 * Wortklassifizierung benutzt die Klasse ein gestämmtes Vokabular das mit Hilfe
 * des deutschen Porter-Stämmers erstellt wird.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 */
public class SimpleGermanTextClassifier implements IClassifier<InputStream> {

	private static final String DEFAULT_DICTIONARY = "oerich/nlputils/resource/de_DE_frami.dic";
	private static final double DEFAULT_MATCH_VALUE = 0.95;
	private Set<String> stemmedVocabulary;
	private GermanAnalyzer analyzer;
	private double matchValue;
	private LuceneTokenizerAdapter tokenizer = new LuceneTokenizerAdapter();

	/**
	 * Constructs a TextClassifier, initialized with the default dictionary.
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public SimpleGermanTextClassifier() throws IllegalArgumentException,
			IOException {
		this.stemmedVocabulary = new TreeSet<String>();
		this.analyzer = new GermanAnalyzer(Version.LUCENE_34);
		this.setMatchValue(DEFAULT_MATCH_VALUE);

		init(getClass().getClassLoader()
				.getResourceAsStream(DEFAULT_DICTIONARY));
	}

	/**
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void addVocabulariesFromVocabularyStream(InputStream stream)
			throws IllegalArgumentException, IOException {

		BufferedReader bReader = null;

		bReader = new BufferedReader(new InputStreamReader(stream));

		tokenizer.setTokenStream(this.analyzer.tokenStream(null, bReader));

		String token = tokenizer.nextToken();
		while (token != null) {
			this.stemmedVocabulary.add(token);
			token = tokenizer.nextToken();
		}

	}

	public void clear() {
		this.stemmedVocabulary.clear();
	}

	/**
	 * 
	 */
	public double classify(String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException(
					"Parameter text darf nicht null sein.");
		}

		int totalWords = 0, totalMatches = 0;
		StringReader strReader = new StringReader(text);

		try {
			tokenizer
					.setTokenStream(this.analyzer.tokenStream(null, strReader));

			String token = tokenizer.nextToken();
			while (token != null) {
				totalWords++;
				if (this.stemmedVocabulary.contains(token)) {
					totalMatches++;
				}
				// System.out.println( token );
				token = tokenizer.nextToken();
			}

		} catch (IOException exception) {
			// Sollte niemals auftreten, Bibliothek ist sonst kaputt.
			exception.printStackTrace();
		}

		// System.out.println( "Matches: " + totalMatches + " Words: " +
		// totalWords );
		if (totalWords > 0) {
			return ((double) totalMatches / (double) totalWords);
		}

		return getMinimalValue();
	}

	/**
	 * 
	 */
	public boolean isMatch(String text) throws IllegalArgumentException {
		return (this.classify(text) >= this.getMatchValue());
	}

	/**
	 * 
	 */
	public void setMatchValue(double value) {
		if (value >= getMinimalValue() && value <= getMaximumValue()) {
			this.matchValue = value;
		}
	}

	/**
	 * 
	 */
	public double getMatchValue() {
		return this.matchValue;
	}

	@Override
	public void init(InputStream initData) throws IllegalArgumentException,
			IOException {
		clear();
		addVocabulariesFromVocabularyStream(initData);
	}

	@Override
	public TableModel explainClassification(String text) {
		StringReader strReader = new StringReader(text);

		List<String> words = new LinkedList<String>();
		List<Boolean> result = new LinkedList<Boolean>();

		try {
			tokenizer
					.setTokenStream(this.analyzer.tokenStream(null, strReader));

			String term = tokenizer.nextToken();
			while (term != null) {
				words.add(term);
				result.add(this.stemmedVocabulary.contains(term));
				term = tokenizer.nextToken();
			}

		} catch (IOException exception) {
			// Sollte niemals auftreten, Bibliothek ist sonst kaputt.
			exception.printStackTrace();
		}

		TableModel tm = new DefaultTableModel(
				new String[] { "Word", "Correct" }, words.size());
		for (int i = 0; i < words.size(); i++) {
			tm.setValueAt(words.get(i), i, 0);
			tm.setValueAt(result.get(i), i, 1);
		}
		return tm;
	}

	@Override
	public double getMaximumValue() {
		return 1;
	}

	@Override
	public double getMinimalValue() {
		return 0;
	}

}
