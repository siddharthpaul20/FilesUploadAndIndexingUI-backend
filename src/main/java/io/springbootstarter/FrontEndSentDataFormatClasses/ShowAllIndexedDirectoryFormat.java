package io.springbootstarter.FrontEndSentDataFormatClasses;

public class ShowAllIndexedDirectoryFormat 
{
     private String directory;
     
     private ShowAllIndexedDirectoryFormat() {
    	 
     }

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public ShowAllIndexedDirectoryFormat(String directory) {
		super();
		this.directory = directory;
	}
     
     
}
