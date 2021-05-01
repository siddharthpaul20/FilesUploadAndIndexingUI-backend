package io.springbootstarter.FrontEndSentDataFormatClasses;

public class SearchAKeywordInCompleteSentFormat 
{
    private String absolutePath;
    private int freq;
	
    private SearchAKeywordInCompleteSentFormat() {
    	
    }
    
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public SearchAKeywordInCompleteSentFormat(String absolutePath, int freq) {
		super();
		this.absolutePath = absolutePath;
		this.freq = freq;
	}
    
}
