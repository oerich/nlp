package oerich.nlputils.hyphen;

/**
 * Schnittstelle definiert Methoden, die ein Wortsilben-Erkenner implementieren muss.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 *
 */
public interface IHyphenator
{
	
	/**
	 * Zerlegt das Wort in einzelne Silben, die durch Bindestrich voneinander
	 * getrennt sind.
	 * 
	 * @param word
	 * @return
	 * @throws IllegalArgumentException Falls word null.
	 */
	public String hyphenate( String word ) throws IllegalArgumentException;
	
	/**
	 * Gibt zurück aus wie vielen Silben das Wort besteht.
	 * 
	 * @param word
	 * @return
	 * @throws IllegalArgumentException Falls word null.
	 */
	public int syllableCount( String word ) throws IllegalArgumentException;

}
