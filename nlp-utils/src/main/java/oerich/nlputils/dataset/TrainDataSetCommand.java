package oerich.nlputils.dataset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import oerich.nlputils.text.IStopWordFilter;
import oerich.nlputils.text.StopWordFilterFactory;
import oerich.nlputils.tokenize.ITokenizer;

public class TrainDataSetCommand {

	private String file;
	private IDataSet dataSet;
	private ITokenizer tokenizer = StopWordFilterFactory.NULL_TOKENIZER;
	private IStopWordFilter filter = StopWordFilterFactory.NULL_FILTER;

	public void setFile(String string) {
		this.file = string;
	}

	public void setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet;
	}

	public ITokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(ITokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public IStopWordFilter getFilter() {
		return filter;
	}

	public void setFilter(IStopWordFilter filter) {
		this.filter = filter;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(this.file), Charset.forName("UTF-8")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] reqCatType = line.split(";");
				String reqText = reqCatType[0].trim();
				String reqCategory = reqCatType[1].trim();
				String[] sentence = this.filter.filterStopWords(this.tokenizer
						.tokenize(reqText));

				if (reqCatType.length > 2) {
					String reqStereoType = reqCatType[2].trim();
					this.dataSet.learn(sentence, reqCategory, reqStereoType);
				} else {
					this.dataSet.learn(sentence, reqCategory);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
