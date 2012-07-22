package oerich.nlputils.tokenize.sentenceboundarydetection;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import oerich.nlputils.tokenize.AbstractTokenizerAdapter;


/**
 * Klasse stellt einen Adapter auf den Java BreakIterator dar, mit gesetzter
 * deutscher Lokale. Die genaue Funktionsweise ist unbekannt. Der BreakIterator
 * scheint jedoch einige "einfache" Regeln für jede Lokale zu besitzen, die in
 * der Regel gute Resultate bringen. Kompliziertere Abkürzungen mit Leerzeichen
 * wie "z. B." werden nicht korrekt zum Satz erkannt, wohl aber deren alte
 * Variante "z.B.".
 * 
 * @author Philipp Förmer, philipp at cathaldar dot net
 * 
 * @see java.text.BreakIterator
 */
public class SimpleSentenceBoundaryDetector extends AbstractTokenizerAdapter {

	/**
	 * 
	 */
	private BreakIterator detector;

	/**
	 * 
	 */
	public SimpleSentenceBoundaryDetector(Locale language) {

		this.detector = BreakIterator.getSentenceInstance(language);

	}

	/**
	 * @see ISentenceBoundaryDetector
	 */
	@Override
	public String[] tokenize(String text) throws IllegalArgumentException {

		if (text == null) {

			throw new IllegalArgumentException(
					"Übergabeparameter text darf nicht null sein.");

		}

		this.detector.setText(text);
		List<String> sentenceList = new LinkedList<String>();
		String sentence;
		int lastPosition = 0;
		int nextSentenceStart;

		while ((nextSentenceStart = this.detector.next()) != BreakIterator.DONE) {

			sentence = text.substring(lastPosition, nextSentenceStart);
			sentenceList.add(sentence);
			lastPosition = nextSentenceStart;

		}

		return sentenceList.toArray(new String[] {});

	}

}
