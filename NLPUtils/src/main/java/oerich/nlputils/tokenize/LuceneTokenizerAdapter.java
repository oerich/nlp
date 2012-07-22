package oerich.nlputils.tokenize;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class LuceneTokenizerAdapter extends AbstractTokenizerAdapter implements
		ITokenizer, AttributeReflector {

	private TokenStream tokenStream;
	private String type;
	private String word;

	public void setTokenStream(TokenStream tokenStream) throws IOException {
		this.tokenStream = tokenStream;
		this.tokenStream.reset();
	}

	public String getType() {
		return type;
	}

	public String getWord() {
		return word;
	}

	@Override
	public void reflect(Class<? extends Attribute> arg0, String key,
			Object value) {
		if ("term".equals(key)) {
			this.word = (String) value;
			return;
		}
		if ("type".equals(key))
			this.type = (String) value;
		// System.out.println(word + ":" + key + "=" + value);
	}

	@Override
	public String nextToken() {
		try {
			while (this.tokenStream.incrementToken()) {
				Iterator<AttributeImpl> it = this.tokenStream
						.getAttributeImplsIterator();
				while (it.hasNext()) {
					it.next().reflectWith(this);
				}
				return getWord();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setText(String text) {
		System.out.println(text);
	}
}
