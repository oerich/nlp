package oerich.nlputils.dictionary;

/**
 * Interface defines methods a dictionary for a natural language must support.
 * 
 * @author Philipp Förmer
 */
public interface Dictionary {

	/**
	 * Returns true, if the passed word is part of the language the dictionary represents.
	 * Returns false, else.
	 * 
	 * @param word null or non null
	 * @return true or false
	 */
	public boolean isCorrectSpelled(String word);
	
}
