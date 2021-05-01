package io.springbootstarter.FrontEndSentDataFormatClasses;

public class FileFolderSpecificInput 
{
    private String indexFolderName;
    
    private FileFolderSpecificInput() {
    	
    }

	public String getIndexFolderName() {
		return indexFolderName;
	}

	public void setIndexFolderName(String indexFolderName) {
		this.indexFolderName = indexFolderName;
	}

	public FileFolderSpecificInput(String indexFolderName) {
		super();
		this.indexFolderName = indexFolderName;
	}
}
