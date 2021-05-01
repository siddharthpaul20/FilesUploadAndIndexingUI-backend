package io.springbootstarter.FrontEndSentDataFormatClasses;

public class FileFolderSearchKeywordInput 
{
   private String fileFolderToSearch;
   
   private FileFolderSearchKeywordInput() {
	   
   }

	public FileFolderSearchKeywordInput(String fileFolderToSearch) {
		super();
		this.fileFolderToSearch = fileFolderToSearch;
	}
	
	public String getFileFolderToSearch() {
		return fileFolderToSearch;
	}
	
	public void setFileFolderToSearch(String fileFolderToSearch) {
		this.fileFolderToSearch = fileFolderToSearch;
	}
}
