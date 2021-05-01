package io.springbootstarter.FrontEndSentDataFormatClasses;

public class KeywordInput 
{
    private String keywordToSearch;
    
    public KeywordInput() {
    	
    }

	public KeywordInput(String keywordToSearch) {
		super();
		this.keywordToSearch = keywordToSearch;
	}

	public String getKeywordToSearch() {
		return keywordToSearch;
	}

	public void setKeywordToSearch(String keywordToSearch) {
		this.keywordToSearch = keywordToSearch;
	}
    
    
}
