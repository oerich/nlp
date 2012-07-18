package oerich.nlputils.text;

class NewString {
	public String str;

	NewString() {
		str = "";
	}
}

public class PorterStemmer {

	private String Clean(String str) {
		int last = str.length();
		StringBuffer temp = new StringBuffer();

		if (last > 0) {

			for (int i = 0; i < last; i++) {
				if (Character.isLetterOrDigit(str.charAt(i)))
					temp.append(str.charAt(i));
			}
		}

		return temp.toString();
	} // clean

	private boolean hasSuffix(String word, String suffix, NewString stem) {

		StringBuffer tmp = new StringBuffer();

		if (word.length() <= suffix.length())
			return false;
		if (suffix.length() > 1)
			if (word.charAt(word.length() - 2) != suffix
					.charAt(suffix.length() - 2))
				return false;

		stem.str = "";

		for (int i = 0; i < word.length() - suffix.length(); i++)
			stem.str += word.charAt(i);
		tmp.append(stem.str);

		for (int i = 0; i < suffix.length(); i++)
			tmp.append(suffix.charAt(i));

		if (tmp.toString().compareTo(word) == 0)
			return true;
		else
			return false;
	}

	private boolean vowel(char ch, char prev) {
		switch (ch) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return true;
		case 'y': {

			switch (prev) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return false;

			default:
				return true;
			}
		}

		default:
			return false;
		}
	}

	private int measure(String stem) {

		int i = 0, count = 0;
		int length = stem.length();

		while (i < length) {
			for (; i < length; i++) {
				if (i > 0) {
					if (vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (vowel(stem.charAt(i), 'a'))
						break;
				}
			}

			for (i++; i < length; i++) {
				if (i > 0) {
					if (!vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (!vowel(stem.charAt(i), '?'))
						break;
				}
			}
			if (i < length) {
				count++;
				i++;
			}
		} // while

		return (count);
	}

	private boolean containsVowel(String word) {

		for (int i = 0; i < word.length(); i++)
			if (i > 0) {
				if (vowel(word.charAt(i), word.charAt(i - 1)))
					return true;
			} else {
				if (vowel(word.charAt(0), 'a'))
					return true;
			}

		return false;
	}

	private boolean cvc(String str) {
		int length = str.length();

		if (length < 3)
			return false;

		if ((!vowel(str.charAt(length - 1), str.charAt(length - 2)))
				&& (str.charAt(length - 1) != 'w')
				&& (str.charAt(length - 1) != 'x')
				&& (str.charAt(length - 1) != 'y')
				&& (vowel(str.charAt(length - 2), str.charAt(length - 3)))) {

			if (length == 3) {
				if (!vowel(str.charAt(0), '?'))
					return true;
				else
					return false;
			} else {
				if (!vowel(str.charAt(length - 3), str.charAt(length - 4)))
					return true;
				else
					return false;
			}
		}

		return false;
	}

	private String step1(String str) {

		NewString stem = new NewString();

		if (str.charAt(str.length() - 1) == 's') {
			if ((hasSuffix(str, "sses", stem)) || (hasSuffix(str, "ies", stem))) {
				StringBuffer tmp = new StringBuffer();
				for (int i = 0; i < str.length() - 2; i++)
					tmp.append(str.charAt(i));
				str = tmp.toString();
			} else {
				if ((str.length() == 1)
						&& (str.charAt(str.length() - 1) == 's')) {
					str = "";
					return str;
				}
				if (str.charAt(str.length() - 2) != 's') {
					StringBuffer tmp = new StringBuffer();
					for (int i = 0; i < str.length() - 1; i++)
						tmp.append(str.charAt(i));
					str = tmp.toString();
				}
			}
		}

		if (hasSuffix(str, "eed", stem)) {
			if (measure(stem.str) > 0) {
				StringBuffer tmp = new StringBuffer();
				for (int i = 0; i < str.length() - 1; i++)
					tmp.append(str.charAt(i));
				str = tmp.toString();
			}
		} else {
			if ((hasSuffix(str, "ed", stem)) || (hasSuffix(str, "ing", stem))) {
				if (containsVowel(stem.str)) {

					StringBuffer tmp = new StringBuffer();
					for (int i = 0; i < stem.str.length(); i++)
						tmp.append(str.charAt(i));
					str = tmp.toString();
					if (str.length() == 1)
						return str;

					if ((hasSuffix(str, "at", stem))
							|| (hasSuffix(str, "bl", stem))
							|| (hasSuffix(str, "iz", stem))) {
						str += "e";

					} else {
						int length = str.length();
						if ((str.charAt(length - 1) == str.charAt(length - 2))
								&& (str.charAt(length - 1) != 'l')
								&& (str.charAt(length - 1) != 's')
								&& (str.charAt(length - 1) != 'z')) {

							tmp = new StringBuffer();
							for (int i = 0; i < str.length() - 1; i++)
								tmp.append(str.charAt(i));
							str = tmp.toString();
						} else if (measure(str) == 1) {
							if (cvc(str))
								str += "e";
						}
					}
				}
			}
		}

		if (hasSuffix(str, "y", stem))
			if (containsVowel(stem.str)) {
				StringBuffer tmp = new StringBuffer();
				for (int i = 0; i < str.length() - 1; i++)
					tmp.append(str.charAt(i));
				tmp.append('i');
				str = tmp.toString();
			}
		return str;
	}

	private String step2(String str) {

		String[][] suffixes = { { "ational", "ate" }, { "tional", "tion" },
				{ "enci", "ence" }, { "anci", "ance" }, { "izer", "ize" },
				{ "iser", "ize" }, { "abli", "able" }, { "alli", "al" },
				{ "entli", "ent" }, { "eli", "e" }, { "ousli", "ous" },
				{ "ization", "ize" }, { "isation", "ize" }, { "ation", "ate" },
				{ "ator", "ate" }, { "alism", "al" }, { "iveness", "ive" },
				{ "fulness", "ful" }, { "ousness", "ous" }, { "aliti", "al" },
				{ "iviti", "ive" }, { "biliti", "ble" } };
		NewString stem = new NewString();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index][0], stem)) {
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes[index][1];
					return str;
				}
			}
		}

		return str;
	}

	private String step3(String str) {

		String[][] suffixes = { { "icate", "ic" }, { "ative", "" },
				{ "alize", "al" }, { "alise", "al" }, { "iciti", "ic" },
				{ "ical", "ic" }, { "ful", "" }, { "ness", "" } };
		NewString stem = new NewString();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index][0], stem))
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes[index][1];
					return str;
				}
		}
		return str;
	}

	private String step4(String str) {

		String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible",
				"ant", "ement", "ment", "ent", "sion", "tion", "ou", "ism",
				"ate", "iti", "ous", "ive", "ize", "ise" };

		NewString stem = new NewString();

		for (int index = 0; index < suffixes.length; index++) {
			if (hasSuffix(str, suffixes[index], stem)) {

				if (measure(stem.str) > 1) {
					str = stem.str;
					return str;
				}
			}
		}
		return str;
	}

	private String step5(String str) {

		if (str.charAt(str.length() - 1) == 'e') {
			if (measure(str) > 1) {/*
									 * measure(str)==measure(stem) if ends in
									 * vowel
									 */
				StringBuffer tmp = new StringBuffer();
				for (int i = 0; i < str.length() - 1; i++)
					tmp.append(str.charAt(i));
				str = tmp.toString();
			} else if (measure(str) == 1) {
				StringBuffer stem = new StringBuffer();
				for (int i = 0; i < str.length() - 1; i++)
					stem.append(str.charAt(i));

				String stemString = stem.toString();
				if (!cvc(stemString))
					str = stemString;
			}
		}

		if (str.length() == 1)
			return str;
		if ((str.charAt(str.length() - 1) == 'l')
				&& (str.charAt(str.length() - 2) == 'l') && (measure(str) > 1))
			if (measure(str) > 1) {/*
									 * measure(str)==measure(stem) if ends in
									 * vowel
									 */
				StringBuffer tmp = new StringBuffer();
				for (int i = 0; i < str.length() - 1; i++)
					tmp.append(str.charAt(i));
				str = tmp.toString();
			}
		return str;
	}

	private String stripPrefixes(String str) {

		try {
			String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra",
					"mega", "nano", "pico", "pseudo" };

			int last = prefixes.length;
			for (int i = 0; i < last; i++) {
				if (str.startsWith(prefixes[i])) {
					StringBuffer temp = new StringBuffer();
					for (int j = 0; j < str.length() - prefixes[i].length(); j++)
						temp.append(str.charAt(j + prefixes[i].length()));
					return temp.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	private String stripSuffixes(String str) {
		try {
			str = step1(str);
			if (str.length() >= 1)
				str = step2(str);
			if (str.length() >= 1)
				str = step3(str);
			if (str.length() >= 1)
				str = step4(str);
			if (str.length() >= 1)
				str = step5(str);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	public String stripAffixes(String str) {
		try {
			str = str.toLowerCase();
			str = Clean(str);

			if ((str != "") && (str.length() > 2)) {
				str = stripPrefixes(str);

				if (str != "") {
					str = stripSuffixes(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	} // stripAffixes

} // class
