package io.springbootstarter.FolderPathsInput;

public class FolderPathsInput 
{
    private String folderToIndex;
    private String folderCIndex;
    private String pathTxtFile;
	
    public FolderPathsInput() {
    	
    }
    
    public FolderPathsInput(String folderToIndex, String folderCIndex, String pathTxtFile) {
		super();
		this.folderToIndex = folderToIndex;
		this.folderCIndex = folderCIndex;
		this.pathTxtFile = pathTxtFile;
	}
    public String getFolderToIndex() {
		return folderToIndex;
	}
	public void setFolderToIndex(String folderToIndex) {
		this.folderToIndex = folderToIndex;
	}
	public String getFolderCIndex() {
		return folderCIndex;
	}
	public void setFolderCIndex(String folderCIndex) {
		this.folderCIndex = folderCIndex;
	}
	public String getPathTxtFile() {
		return pathTxtFile;
	}
	public void setPathTxtFile(String pathTxtFile) {
		this.pathTxtFile = pathTxtFile;
	}
    
    
}
