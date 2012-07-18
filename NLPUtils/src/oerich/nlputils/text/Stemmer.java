package oerich.nlputils.text;

import java.util.LinkedList;
import java.util.List;

public class Stemmer implements IStemmer {

	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.thesaurus.IStemmer#stemmWords(java
	 * .lang.String[])
	 */
	public String[] stemmWords(String[] words) {
		PorterStemmer stemmer = new PorterStemmer();
		String[] stemmedWords = new String[words.length];
		for (int i = 0; i < words.length; i++) {
			stemmedWords[i] = stemmer.stripAffixes(words[i]);
		}
		return stemmedWords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.thesaurus.IStemmer#stemmWords(java
	 * .util.LinkedList)
	 */
	public List<String> stemmWords(List<String> words) {
		PorterStemmer stemmer = new PorterStemmer();
		LinkedList<String> stemmedWords = new LinkedList<String>();
		for (int i = 0; i < words.size(); i++) {
			stemmedWords.add(stemmer.stripAffixes(words.get(i)));
		}
		return stemmedWords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.thesaurus.IStemmer#stemmText(java
	 * .lang.String)
	 */
	public String stemmText(String text) {
		try {
			String[] stemmedWords = stemmWords(text.split(SPACE));
			StringBuffer stemmedText = new StringBuffer();
			for (String s : stemmedWords) {
				stemmedText.append(s);
				stemmedText.append(SPACE);
			}
			return stemmedText.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return EMPTY_STRING;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.thesaurus.IStemmer#stemmWord(java
	 * .lang.String)
	 */
	public String stemmWord(String word) {
		PorterStemmer stemmer = new PorterStemmer();
		return stemmer.stripAffixes(word);
	}

}
