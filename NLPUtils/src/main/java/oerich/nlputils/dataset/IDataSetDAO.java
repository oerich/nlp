package oerich.nlputils.dataset;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.dataset.impl.NewXMLDataSetDAO;
import oerich.nlputils.dataset.impl.StandardXMLDataSetDAO;
import oerich.nlputils.text.IStemmer;


/**
 * Load and Store datasets. This package is in an inconsistent and experimental
 * state. It basically supports the management of data for the Bayesian and
 * TFxIDF classifiers.
 *
 * @author Eric Knauss
 *
 */
public interface IDataSetDAO {
	@Deprecated
	public static final IDataSetDAO STANDARD_XML = new StandardXMLDataSetDAO();
	public static final IDataSetDAO NEW_XML = new NewXMLDataSetDAO();

	public abstract IDataSet createDataSet(boolean stemming)
			throws NLPInitializationException;

	public abstract IDataSet createDataSet(String databaseFileName,
			IStemmer stemmer) throws NLPInitializationException;

	public abstract IDataSet createDataSet(String databaseFileName)
			throws NLPInitializationException;

	/**
	 * Creates an empty <code>IDataSet</code>.
	 *
	 * @return an empty <code>IDataSet</code>
	 */
	public abstract IDataSet createDataSet();

	public abstract void storeDataSet(String databaseFileName, IDataSet dataSet)
			throws NLPInitializationException;

	/**
	 * Checks, whether this DAO can read the fileformat.
	 *
	 * @param databaseFileName
	 * @return true, if the DAO can work with this file.
	 */
	public abstract boolean checkFileFormat(String databaseFileName);
}