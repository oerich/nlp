package oerich.nlputils.classifier;

import javax.swing.table.TableModel;

import oerich.nlputils.NLPInitializationException;

/**
 * Schnittstelle definiert Methoden, die eine Sprachklassifikations-Metrik
 * implementieren muss.
 * 
 * @author Philipp Förmer, Eric Knauss
 * 
 */
public interface IClassifier<T> {

	/**
	 * Minimaler Wert den die Metrik annehmen kann.
	 */
	public double getMinimalValue();

	/**
	 * Maximaler Wert den die Metrik annehmen kann.
	 */
	public double getMaximumValue();

	/**
	 * Initialisiert den Classifier.
	 * 
	 * @param initData
	 */
	public void init(T initData) throws Exception;

	/**
	 * Gibt den ermittelten Metrik-Wert zurück. Wert ist aus dem Intervall [0;1]
	 * 
	 * @param text
	 * @return
	 * @throws NLPInitializationException
	 */
	public double classify(String text) throws NLPInitializationException;

	/**
	 * Gibt wahr zurück, falls der Text, durch Ermittlung des Metrikwertes, zur
	 * Sprache gehört. Falsch sonst.
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public boolean isMatch(String text) throws NLPInitializationException;

	/**
	 * Setzt den Wert, oberhalb dessen ein Text nach der Metrik zur Sprache
	 * gehört.
	 * 
	 * @param value
	 *            Wert aus Intervall [0;1]
	 */
	public void setMatchValue(double value);

	/**
	 * Gibt den Match-Wert zurück. Wert ist aus dem Intervall [0;1].
	 * 
	 * @return
	 */
	public double getMatchValue();

	/**
	 * Gives an Overview on how the classification of <code>text</code> was
	 * computed.
	 * 
	 * @param text
	 * @return
	 */
	public TableModel explainClassification(String text);

}
