package io.springbootstarter.FrontEndSentDataFormatClasses;

public class FileFolderSearchSpecificInputs 
{
   private String folderCarryingSearch;
   private String fileFolderToSearch;
   
   private FileFolderSearchSpecificInputs() {
	   
   }

	public String getFolderCarryingSearch() {
		return folderCarryingSearch;
	}
	
	public void setFolderCarryingSearch(String folderCarryingSearch) {
		this.folderCarryingSearch = folderCarryingSearch;
	}
	
	public String getFileFolderToSearch() {
		return fileFolderToSearch;
	}
	
	public void setFileFolderToSearch(String fileFolderToSearch) {
		this.fileFolderToSearch = fileFolderToSearch;
	}
	
	public FileFolderSearchSpecificInputs(String folderCarryingSearch, String fileFolderToSearch) {
		super();
		this.folderCarryingSearch = folderCarryingSearch;
		this.fileFolderToSearch = fileFolderToSearch;
	}
   
   
}
