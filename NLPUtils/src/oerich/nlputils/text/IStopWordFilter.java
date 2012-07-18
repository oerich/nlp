package oerich.nlputils.text;

public interface IStopWordFilter {

	public abstract String[] filterStopWords(String[] text);
}