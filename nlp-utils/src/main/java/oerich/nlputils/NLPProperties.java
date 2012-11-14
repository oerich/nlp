package oerich.nlputils;

public class NLPProperties {

	private static NLPProperties INSTANCE;
	private String stopwordsFileName;
	private String stopsignsFileName;
	private String resourcePath;

	public static synchronized NLPProperties getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NLPProperties();
			INSTANCE.resetToDefault();
		}
		return INSTANCE;
	}

	public void resetToDefault() {
		setStopwordsFileName("stopwords.txt");
		setStopsignsFileName("stopsigns.txt");
		setResourcePath("");
	}

	private NLPProperties() {

	}

	public String getStopwordsFileName() {
		return stopwordsFileName;
	}

	public void setStopwordsFileName(String stopwordsFileName) {
		this.stopwordsFileName = stopwordsFileName;
	}

	public String getStopsignsFileName() {
		return stopsignsFileName;
	}

	public void setStopsignsFileName(String stopsignsFileName) {
		this.stopsignsFileName = stopsignsFileName;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getResourcesPath() {
		return this.resourcePath;
	}

	public String getStopwordFileName() {
		return this.stopwordsFileName;
	}

	public String getStopwordFilePath() {
		return this.resourcePath + getStopwordFileName();
	}

	public String getStopsignsFilePath() {
		return this.resourcePath + getStopsignsFileName();
	}
}
