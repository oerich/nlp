package oerich.nlputils.tokenize.sentenceboundarydetection;

import java.io.IOException;


import oerich.nlputils.tokenize.AbstractTokenizerAdapter;
import opennlp.tools.lang.german.SentenceDetector;

/**
 * Klasse stellt einen Adapter auf den deutschen Satzende Erkenner der OpenNLP
 * Gruppe dar, welcher auf dem Maximum Entropie Modell beruht.
 *
 * @author Philipp Förmer, philipp at cathaldar dot net
 *
 * @see opennlp.tools.lang.german.SentenceDetector
 */
public class GermanMaximumEntropySentenceBoundaryDetector extends AbstractTokenizerAdapter {

	private static final String MODEL_FILE_PATH = "src/oerich/nlputils/resource/sentenceModel.bin.gz";

	/**
	 * Satzerkenner.
	 */
	private SentenceDetector detector;

	/**
	 *
	 * @param entropyMaxentModelFilepath
	 *            Pfad zum deutschen Maxent Entropie Modell.
	 * @throws IOException
	 *             Falls Modell nicht gelesen werden kann.
	 */
	public GermanMaximumEntropySentenceBoundaryDetector(
			String entropyMaxentModelFilepath) throws IOException {
		this.detector = new SentenceDetector(entropyMaxentModelFilepath);
	}

	public GermanMaximumEntropySentenceBoundaryDetector() throws IOException {
		this(MODEL_FILE_PATH);
	}

	/**
	 * @see ISentenceBoundaryDetector
	 */
	@Override
	public String[] tokenize(String text)
			throws IllegalArgumentException {

		if (text == null) {
			throw new IllegalArgumentException(
					"Übergabeparameter text darf nicht null sein.");
		}

		return this.detector.sentDetect(text);
	}

}
