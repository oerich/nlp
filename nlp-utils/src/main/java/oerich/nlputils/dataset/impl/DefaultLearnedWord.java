package oerich.nlputils.dataset.impl;

import java.util.HashMap;
import java.util.Map;

import oerich.nlputils.dataset.ILearnedWord;


public class DefaultLearnedWord implements ILearnedWord {

	private Map<String, Integer> learnedCategories = new HashMap<String, Integer>();

	private String name;

	public DefaultLearnedWord(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public String[] getCategories() {
		return this.learnedCategories.keySet().toArray(new String[0]);
	}

	@Override
	public int getWeight(String category) {
		if (!this.learnedCategories.containsKey(category))
			return 0;
		return this.learnedCategories.get(category);
	}

	@Override
	public void setWeight(String category, int i) {
		this.learnedCategories.put(category, i);
	}

}
