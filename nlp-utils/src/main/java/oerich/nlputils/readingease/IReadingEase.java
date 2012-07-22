package oerich.nlputils.readingease;

/**
 * Schnittstelle beschreibt Methoden, die eine Klasse zur Bestimmung eines
 * Lesbarkeitsindex von natürlich sprachigen Texten bereitstellen muss.
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * @see http://de.wikipedia.org/wiki/Lesbarkeitsindex
 */
public interface IReadingEase
{
	
	/**
	 * Berechnet den Lesbarkeitsindex eines Satzes. 
	 * 
	 * @param sentence Ein Satz.
	 * @return
	 * @throws IllegalArgumentException Falls sentence null ist.
	 */
	public Number calculateReadingEaseForSentence( String sentence ) throws IllegalArgumentException;

	/**
	 * Berechnet den Lesbarkeitsindex für einen Abschnitt.
	 * 
	 * @param sentences
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Number calculateReadingEaseForParagraph( String sentences[] ) throws IllegalArgumentException;
	
}
