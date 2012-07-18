package oerich.nlputils.text;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DefaultStopWordFilter implements IStopWordFilter {

	private Set<String> stopwords = new TreeSet<String>();

	@Override
	public String[] filterStopWords(String[] text) {
		List<String> ret = new LinkedList<String>();

		for (String w : text) {
			if (!this.stopwords.contains(w))
				ret.add(w);
		}
		return ret.toArray(new String[0]);
	}

	public void setStopWords(String[] stopWords) {
		this.stopwords.clear();
		for (String w : stopWords)
			this.stopwords.add(w);
	}

	public String[] getStopWords() {
		return this.stopwords.toArray(new String[0]);
	}

}
