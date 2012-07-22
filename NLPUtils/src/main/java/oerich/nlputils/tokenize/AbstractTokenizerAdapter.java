package oerich.nlputils.tokenize;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTokenizerAdapter implements ITokenizer {

	private int index = 0;
	private String[] tokens;

	@Override
	public String[] tokenize(String text) {
		setText(text);
		List<String> tokens = new LinkedList<String>();
		String nextToken = nextToken();
		while (nextToken != null) {
			tokens.add(nextToken);
			nextToken = nextToken();
		}
		return tokens.toArray(new String[0]);
	}

	
	public void setText(String text) {
		this.tokens = tokenize(text);
		this.index = 0;
	}
	
	public String nextToken() {
		if (tokens == null  || this.index >= this.tokens.length) 
			return null;
		return this.tokens[this.index++];
	}
}
