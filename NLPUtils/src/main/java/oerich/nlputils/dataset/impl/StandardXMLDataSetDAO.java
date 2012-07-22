package oerich.nlputils.dataset.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.dataset.IDataSet;
import oerich.nlputils.dataset.IDataSetDAO;
import oerich.nlputils.i18n.NLPUtili18n;
import oerich.nlputils.text.IStemmer;
import oerich.nlputils.text.Stemmer;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public final class StandardXMLDataSetDAO implements IDataSetDAO {

	private static final String KEY_COUNT_NON_SEC = "count_non_sec";
	private static final String KEY_COUNT_SEC = "count_sec";
	private static final String DATA_SET_FILE = "./data_set.xml";
	private static final String STEMM_DATA_SET_FILE = "./stemm_data_set.xml";

	@Deprecated
	public StandardXMLDataSetDAO() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.datamining.IDataSetDAO#createDataSet
	 * (boolean)
	 */
	public IDataSet createDataSet(boolean stemming)
			throws NLPInitializationException {
		if (stemming) {
			return createDataSet(STEMM_DATA_SET_FILE, new Stemmer());
		}
		return createDataSet(DATA_SET_FILE, IStemmer.NULL_STEMMER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.datamining.IDataSetDAO#createDataSet
	 * (java.lang.String,
	 * de.unihannover.se.hera.secreq.logic.thesaurus.IStemmer)
	 */
	@SuppressWarnings("unchecked")
	public IDataSet createDataSet(String databaseFileName, IStemmer stemmer)
			throws NLPInitializationException {
		IDataSet dataSet = new DefaultDataSet();
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;

		if (stemmer == null)
			stemmer = IStemmer.NULL_STEMMER;

		try {
			doc = builder.build(databaseFileName);
		} catch (Exception e) {
			throw new NLPInitializationException(NLPUtili18n
					.getString("ERROR_INIT_EXCEPTION"), e);
		}

		Element root = doc.getRootElement();
		Element sec = root.getChild(IDataSet.KEY_SEC);
		Element nonsec = root.getChild(IDataSet.KEY_NON_SEC);
		if (sec != null)
			dataSet.setCountSecReq(Integer.parseInt(sec.getText()));
		if (nonsec != null)
			dataSet.setCountNonSecReq(Integer.parseInt(nonsec.getText()));

		List<Element> children = root.getChildren();
		for (Element child : children) {
			if ((sec == null || !sec.equals(child))
					&& (nonsec == null || !nonsec.equals(child))) {
				String word = child.getName();
				word = stemmer.stemmWord(word);

				Element secCount = child.getChild(KEY_COUNT_SEC);
				int countSec = 0;
				if (secCount != null)
					countSec = (int) Double.parseDouble(secCount.getText());

				Element nonsecCount = child.getChild(KEY_COUNT_NON_SEC);
				int countNonSec = 0;
				if (nonsecCount != null)
					countNonSec = (int) Double.parseDouble(nonsecCount
							.getText());

				dataSet.addSecValue(word, countSec);
				dataSet.addNonSecValue(word, countNonSec);

				Element elem = root.getChild(word);
				List<Attribute> attributes = elem.getAttributes();
				for (int j = 0; j < attributes.size(); j++) {
					Attribute attr = attributes.get(j);
					int value;
					try {
						value = attr.getIntValue();
						dataSet.addValue(word, value, attr.getName());
					} catch (DataConversionException e) {
						getLogger().error("Database corrupt for " + word + ".",
								e);
					}
				}
			}
		}

		return dataSet;
	}

	private Logger getLogger() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unihannover.se.hera.secreq.logic.datamining.IDataSetDAO#storeDataSet
	 * (java.lang.String,
	 * de.unihannover.se.hera.secreq.logic.datamining.IDataSet)
	 */
	public void storeDataSet(String databaseFileName, IDataSet dataSet)
			throws NLPInitializationException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			File f = new File(databaseFileName);
			if (!f.exists()) {
				doc = new Document();
				Element root = new Element("root");
				doc.setRootElement(root);
			} else {
				doc = builder.build(f);
			}
		} catch (Exception e) {
			throw new NLPInitializationException(NLPUtili18n
					.getString("ERROR_INIT_EXCEPTION"), e);
		}

		Element root = doc.getRootElement();

		Element sec = root.getChild(IDataSet.KEY_SEC);
		if (sec == null) {
			sec = new Element(IDataSet.KEY_SEC);
			root.addContent(sec);
		}
		Element nonsec = root.getChild(IDataSet.KEY_NON_SEC);
		if (nonsec == null) {
			nonsec = new Element(IDataSet.KEY_NON_SEC);
			root.addContent(nonsec);
		}
		sec.setText(Integer.valueOf(dataSet.getCountSecReq()).toString());
		nonsec.setText(Integer.valueOf(dataSet.getCountNonSecReq()).toString());

		for (String word : dataSet.getWords()) {
			storeWord(root, dataSet, word);
		}
		writeDataSetFile(doc, databaseFileName);
	}

	private static void storeWord(Element root, IDataSet dataSet, String word) {
		Element wordElem = root.getChild(word);

		if (wordElem == null) {
			wordElem = new Element(word);
			Element countSecElem = new Element(KEY_COUNT_SEC);
			Element countNonSecElem = new Element(KEY_COUNT_NON_SEC);
			wordElem.addContent(countSecElem);
			wordElem.addContent(countNonSecElem);
			root.addContent(wordElem);

		}
		// Save the sec and non-sec value as element:
		Element countSecElem = wordElem.getChild(KEY_COUNT_SEC);
		countSecElem.setText(Integer.valueOf(dataSet.getSecValue(word))
				.toString());

		Element countNonSecElem = wordElem.getChild(KEY_COUNT_NON_SEC);
		countNonSecElem.setText(Integer.valueOf(dataSet.getNonSecValue(word))
				.toString());

		// Save the categories as attributes
		for (String c : dataSet.getCategories(word)) {
			if (!c.equals(IDataSet.STEREOTYPE_NOT_KNOWN)
					&& !c.equals(IDataSet.KEY_SEC)
					&& !c.equals(IDataSet.KEY_NON_SEC)) {
				wordElem.setAttribute(c, Integer.valueOf(
						dataSet.getValue(word, c)).toString());
			}
		}

	}

	private static void writeDataSetFile(Document doc, String fileName) {
		try {
			File file = new File(fileName);
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream outStream = new FileOutputStream(file);
			outputter.output(doc, outStream);
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkFileFormat(String databaseFileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDataSet createDataSet(String databaseFileName)
			throws NLPInitializationException {
		return createDataSet(databaseFileName, IStemmer.NULL_STEMMER);
	}

	@Override
	public IDataSet createDataSet() {
		return new DefaultDataSet();
	}
}
