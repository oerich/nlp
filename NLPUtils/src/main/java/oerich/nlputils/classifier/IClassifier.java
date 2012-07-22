package oerich.nlputils.classifier;

import javax.swing.table.TableModel;

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
	 * @param initData
	 */
	public void init(T initData) throws Exception;

	/**
	 * Gibt den ermittelten Metrik-Wert zurück. Wert ist aus dem Intervall [0;1]
	 *
	 * @param text
	 * @return
	 * @throws IllegalArgumentException
	 */
	public double classify(String text) throws IllegalArgumentException;

	/**
	 * Gibt wahr zurück, falls der Text, durch Ermittlung des Metrikwertes, zur
	 * Sprache gehört. Falsch sonst.
	 *
	 * @param text
	 * @return
	 * @throws IllegalArgumentException
	 *             Falls Übergabeparameter null.
	 */
	public boolean isMatch(String text) throws IllegalArgumentException;

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
	 * Gives an Overview on how the classification of <code>text</code> was computed.
	 * @param text
	 * @return
	 */
	public TableModel explainClassification(String text);

}
