package oerich.nlputils.text;

import java.util.List;

public interface IStemmer {

	public abstract String[] stemmWords(String[] words);

	public abstract List<String> stemmWords(List<String> words);

	public abstract String stemmText(String text);

	public abstract String stemmWord(String word);

	public static final IStemmer NULL_STEMMER = new IStemmer() {

		@Override
		public String stemmText(String text) {
			return text;
		}

		@Override
		public String stemmWord(String word) {
			return word;
		}

		@Override
		public String[] stemmWords(String[] words) {
			return words;
		}

		@Override
		public List<String> stemmWords(List<String> words) {
			return words;
		}

	};
}