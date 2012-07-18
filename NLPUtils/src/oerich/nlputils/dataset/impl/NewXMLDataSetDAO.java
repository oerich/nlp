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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class NewXMLDataSetDAO implements IDataSetDAO {

	private static final String KEY_VALUE = "value";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_NAME = "name";
	private static final String KEY_TOTAL_NUMBER_OF_NON_SECURITY_REQUIREMENTS = "totalNumberOfNonSecurityRequirements";
	private static final String KEY_TOTAL_NUMBER_OF_SECURITY_REQUIREMENTS = "totalNumberOfSecurityRequirements";
	private static final String KEY_DAO = "dao";
	private static final String KEY_ROOT_ELEMENT = "dataset";
	private static final String KEY_LEARNED_WORD = "learnedWord";

	@Override
	public boolean checkFileFormat(String databaseFileName) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
				doc = builder.build(databaseFileName);
			Element root = doc.getRootElement();
			if (KEY_ROOT_ELEMENT.equals(root.getName())
					&& getClass().getName().equals(root.getAttributeValue(KEY_DAO)))
					return true;
		} catch (JDOMException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	@Override
	public IDataSet createDataSet(boolean stemming)
			throws NLPInitializationException {
		throw new RuntimeException("Not supported");

	}

	@SuppressWarnings("unchecked")
	@Override
	public IDataSet createDataSet(String databaseFileName, IStemmer stemmer)
			throws NLPInitializationException {
		IDataSet dataSet = new DefaultDataSet();
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;

		if (stemmer == null)
			stemmer = IStemmer.NULL_STEMMER;

		dataSet.setStemmer(stemmer);
		try {
			doc = builder.build(databaseFileName);
		} catch (Exception e) {
			throw new NLPInitializationException(NLPUtili18n
					.getString("ERROR_INIT_EXCEPTION"), e);
		}

		Element root = doc.getRootElement();

		dataSet.setCountSecReq(Integer.parseInt(root
				.getAttributeValue(KEY_TOTAL_NUMBER_OF_SECURITY_REQUIREMENTS)));
		dataSet.setCountNonSecReq(Integer.parseInt(root
				.getAttributeValue(KEY_TOTAL_NUMBER_OF_NON_SECURITY_REQUIREMENTS)));

		List<Element> children = root.getChildren();
		for (Element learnedWord : children) {

			String word = learnedWord.getAttributeValue(KEY_NAME);
			List<Element> categories = learnedWord.getChildren();
			for (Element cat : categories) {
				dataSet.addValue(word,
						Integer.parseInt(cat.getAttributeValue(KEY_VALUE)), cat
								.getAttributeValue(KEY_NAME));
			}
		}
		return dataSet;
	}

	@Override
	public void storeDataSet(String databaseFileName, IDataSet dataSet)
			throws NLPInitializationException {
		try {
			Document doc = null;
			doc = new Document();
			Element root = new Element(KEY_ROOT_ELEMENT);
			root.setAttribute(KEY_DAO, getClass().getName());
			root.setAttribute(KEY_TOTAL_NUMBER_OF_SECURITY_REQUIREMENTS, ((Integer) dataSet
					.getCountSecReq()).toString());
			root.setAttribute(KEY_TOTAL_NUMBER_OF_NON_SECURITY_REQUIREMENTS,
					((Integer) dataSet.getCountNonSecReq()).toString());
			doc.setRootElement(root);

			for (String word : dataSet.getWords()) {
				Element wordElem = new Element(KEY_LEARNED_WORD);
				wordElem.setAttribute(KEY_NAME, word);
				root.addContent(wordElem);

				// Save the categories as attributes
				for (String c : dataSet.getCategories(word)) {
					Element catElem = new Element(KEY_CATEGORY);
					catElem.setAttribute(KEY_NAME, c);
					catElem.setAttribute(KEY_VALUE, Integer.valueOf(dataSet.getValue(word, c))
							.toString());
					wordElem.addContent(catElem);
				}
			}
			writeDataSetFile(doc, databaseFileName);

		} catch (Exception e) {
			throw new NLPInitializationException(NLPUtili18n
					.getString("ERROR_INIT_EXCEPTION"), e);
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
	public IDataSet createDataSet(String databaseFileName)
			throws NLPInitializationException {
		return createDataSet(databaseFileName, IStemmer.NULL_STEMMER);
	}

	@Override
	public IDataSet createDataSet() {
		return new DefaultDataSet();
	}

}
