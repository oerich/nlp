package oerich.nlputils.classifier;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;


/**
 * Term Frequency x Inverse Document Frequency
 * @author Sinan Petrus, Eric Knauss
 */
public final class TFxIDF implements IClassifier<IDataSet> {

	private double countSecReg;
	private double countNonSecReq;
	private IDataSet dataSet;
	private ITokenizer tokenizer = StopWordFilterFactory
			.createTokenizer();
	private IStopWordFilter filter = StopWordFilterFactory.NULL_FILTER;

	private double matchValue;

	public String checkSecurityRequiremnt(String reqText) {
		double secTermFrequency;
		double secDocumentFrequency;
		double secInverseDocumentFrequency;
		double secValue;
		double secSumm = 0.0;

		double nonSecTermFrequency;
		double nonSecDocumentFrequency;
		double nonSecInverseDocumentFrequency;
		double nonSecValue;
		double nonSecSumm = 0.0;

		double maxWordAppearance = 0.0;
		String maxWord = "";

		String[] words = this.filter.filterStopWords(this.tokenizer
				.tokenize(reqText));
		for (String word : words) {
			try {
				secDocumentFrequency = this.dataSet.getSecValue(word);
				secTermFrequency = 1.0;
				secInverseDocumentFrequency = Math.log10(countSecReg
						/ secDocumentFrequency);
				secValue = secTermFrequency * secInverseDocumentFrequency;
				secSumm += secValue;

				// to get the umlsec category later
				if (secDocumentFrequency > maxWordAppearance) {
					maxWordAppearance = secDocumentFrequency;
					maxWord = word;
				}

				nonSecDocumentFrequency = this.dataSet.getNonSecValue(word);
				nonSecTermFrequency = 1.0;
				nonSecInverseDocumentFrequency = Math.log10(countNonSecReq
						/ nonSecDocumentFrequency);
				nonSecValue = nonSecTermFrequency
						* nonSecInverseDocumentFrequency;
				nonSecSumm += nonSecValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (secSumm < nonSecSumm) {
			// System.out.println(secSumm+" : "+nonSecSumm);
			String umlsecCat = this.dataSet.getMostLikelyStereotype(maxWord);
			return umlsecCat;
		} else {
			return "";
		}
	}

	public String computeTFxIDF(String reqText) {
		// System.out.println("-----");
		// System.out.println(reqText);

		double classification = classify(reqText);

		if (classification > 0) {
			// System.out.println("sec" + "(" + weightSec + "/" + weightNonSec
			// + ")");
			return "sec" + "(" + classification + ")";
		}
		// System.out.println("nonsec" + "(" + weightSec + "/" + weightNonSec
		// + ")");
		return "nonsec" + "(" + classification + ")";

	}

	// private double computeTFsec(String t) {
	// double secValue = (double) this.dataSet.getSecValue(t);
	//
	// double secRequirements = (double) this.dataSet.getCountSecReq();
	// // System.out.print("TFsec("+ t + ") = " + secValue + "/" +
	// // secRequirements + " = " + (secValue / secRequirements) + "; ");
	// // System.out.print("TFsec(" + t + ") = " + (secValue / secRequirements)
	// // + "; ");
	// return secValue / secRequirements;
	// }

	private double computeTFsec2(String t) {
		double secValue = (double) this.dataSet.getSecValue(t);

		double nonsecValue = (double) this.dataSet.getNonSecValue(t);
		// System.out.print("TFsec = " + (secValue / (secValue + nonsecValue))
		// + "; ");
		if (secValue + nonsecValue == 0)
			return 0;
		return secValue / (secValue + nonsecValue);
	}

	private double computeTFnonsec2(String t) {
		double secValue = (double) this.dataSet.getSecValue(t);

		double nonsecValue = (double) this.dataSet.getNonSecValue(t);
		// System.out.print("TFnonsec = "
		// + (nonsecValue / (secValue + nonsecValue)) + "; ");
		if (secValue + nonsecValue == 0)
			return 0;
		return nonsecValue / (secValue + nonsecValue);
	}

	// private double computeTFnonsec(String t) {
	// double nonSecValue = this.dataSet.getNonSecValue(t);
	// double nonSecRequirements = this.dataSet.getCountNonSecReq();
	// // System.out.print("TFnonsec("+ t + ") = " + nonSecValue + "/" +
	// // nonSecRequirements + " = " + (nonSecValue / nonSecRequirements) +
	// // "; ");
	// // System.out.print("TFnonsec(" + t + ") = "
	// // + (nonSecValue / nonSecRequirements) + "; ");
	// return nonSecValue / nonSecRequirements;
	// }

	private double computeIDF(String t) {
		int secRequirements = this.dataSet.getCountSecReq();
		int nonSecRequirements = this.dataSet.getCountNonSecReq();
		int secValue = this.dataSet.getSecValue(t);
		int nonSecValue = this.dataSet.getNonSecValue(t);
		// double idf = Math.log10((secRequirements + nonSecRequirements)
		// / (secValue + nonSecValue +1.0));
		double idf = ((secRequirements + nonSecRequirements) / (secValue
				+ nonSecValue + 1.0));
		// System.out.print("IDF("+ t + ") = (" + secRequirements + "+" +
		// nonSecRequirements + ") / ( " + secValue + "+" + nonSecValue +
		// "+1) = " + idf);
		// System.out.print("IDF = " + idf);
		return idf;
	}

	@Override
	public double classify(String text) throws IllegalArgumentException {
		String[] requirement = this.filter.filterStopWords(this.tokenizer
				.tokenize(text));

		double weightSec = 0.0;
		double weightNonSec = 0.0;
		for (String t : requirement) {
			// System.out.print("W(" + t + ")");
			double tfSec = computeTFsec2(t);
			double tfNonSec = computeTFnonsec2(t);
			double idf = computeIDF(t);
			weightSec += tfSec * idf;
			weightNonSec += tfNonSec * idf;
			// System.out.println(" => W(t) = (" + (tfSec * idf) + "/"
			// + (tfNonSec * idf) + ")");
		}

		return weightSec - weightNonSec;
	}

	@Override
	public double getMatchValue() {
		return this.matchValue;
	}

	@Override
	public boolean isMatch(String text) throws IllegalArgumentException {
		return classify(text) > getMatchValue();
	}

	@Override
	public void setMatchValue(double value) {
		this.matchValue = value;
	}

	@Override
	public void init(IDataSet initData) throws Exception {
		setDataSet(initData);
	}

	private void setDataSet(IDataSet initData) {
		this.dataSet = initData;
		countSecReg = this.dataSet.getCountSecReq();
		countNonSecReq = this.dataSet.getCountNonSecReq();
	}

	@Override
	public TableModel explainClassification(String text) {
		String[] words = null;

		if(text != null)
			words = this.filter.filterStopWords(this.tokenizer
				.tokenize(text));
		else
			words = this.dataSet.getWords();

		TableModel tm = new DefaultTableModel(new String[] { "Word", "TFsec",
				"TFnonsec", "IDF", "Weight" }, words.length) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				if (column > 0)
					return Double.class;
				return String.class;
			};

		};
		int i = 0;
		for (String t : words) {
			double tfsec = computeTFsec2(t);
			double tfnonsec = computeTFnonsec2(t);
			double idf = computeIDF(t);

			tm.setValueAt(t, i, 0);
			tm.setValueAt(tfsec, i, 1);
			tm.setValueAt(tfnonsec, i, 2);
			tm.setValueAt(idf, i, 3);
			tm.setValueAt(tfsec * idf - tfnonsec * idf, i, 4);

			i++;
		}
		return tm;
	}

	@Override
	public double getMaximumValue() {
		return Double.MAX_VALUE;
	}

	@Override
	public double getMinimalValue() {
		return Double.MIN_VALUE;
	}
}
