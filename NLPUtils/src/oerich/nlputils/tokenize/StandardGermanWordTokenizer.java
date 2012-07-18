package oerich.nlputils.tokenize;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * Klasse benutzt den Standard Token Scanner von Apache Lucence. Zusätzlich zu
 * diesem werden keine Tokens zurück gegeben, die als Mail Zahl, Unternehmen
 * identifiziert werden konnten. Die Unterdrückung von Host-Adressen
 * funktioniert nicht richtig. Der Scanner sollte benutzt werden, wenn man rein
 * an "echten" Wort Tokens interessiert ist.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * @see org.apache.lucene.analysis.standard.StandardTokenizer
 */
public class StandardGermanWordTokenizer extends AbstractTokenizerAdapter
		implements ITokenizer {

	private LuceneTokenizerAdapter delegate = new LuceneTokenizerAdapter();

	/**
	 * @see ITokenizer
	 */
	public String nextToken() {
		String ret = this.delegate.nextToken();
		while (!isGoodToken() && ret !=null)
			ret = this.delegate.nextToken();
		return ret;
	}

	/**
	 * Prüft ob ein gelesenes Token gut ist. Ein Token ist gut, wenn es kein
	 * schlechtes Token ist und es keine Zahlen enthält.
	 * 
	 * @return
	 */
	private boolean isGoodToken() {

		if ("<NUM>".equals(this.delegate.getType())) {
			return false;
		}

		return true;
	}

	@Override
	public void setText(String text) {
		if (text == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter text darf nicht null sein.");

		}

		StringReader strReader = new StringReader(text);
		try {
			this.delegate.setTokenStream(new StandardTokenizer(
					Version.LUCENE_34, strReader));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
