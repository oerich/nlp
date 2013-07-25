package oerich.nlputils.text;

import java.util.HashSet;
import java.util.Set;

public class DuplicateFilter implements IStopWordFilter {

	@Override
	public String[] filterStopWords(String[] text) {
		Set<String> tmp = new HashSet<String>();

		for (String s:text)
			tmp.add(s);
		
		return tmp.toArray(new String[0]);
	}

}
