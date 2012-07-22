package oerich.nlputils.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Manages the internatiolization. Basically we try to support english and
 * german.
 * 
 * @author Eric Knauss
 * 
 */
public class NLPUtili18n {

	private static final String BUNDLE_NAME = "oerich/nlputils/i18n/NLPUtils";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private NLPUtili18n() {
	}

	/**
	 * Fetch the correct localized String from the resource bundle.
	 * 
	 * @param key
	 * @return the localized String based on the locale of the VM
	 */
	public static String getString(String key) {
		try {
			String internalName = key.replaceAll(" ", "");
			return RESOURCE_BUNDLE.getString(internalName);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
