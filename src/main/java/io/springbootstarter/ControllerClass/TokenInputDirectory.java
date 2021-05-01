package io.springbootstarter.ControllerClass;

public class TokenInputDirectory 
{
    String folderToIndex;

    private TokenInputDirectory() {
    	
    }
	public TokenInputDirectory(String folderToIndex) 
	{
		super();
		this.folderToIndex = folderToIndex;
	}

	public String getFolderToIndex() {
		return folderToIndex;
	}

	public void setFolderToIndex(String folderToIndex) {
		this.folderToIndex = folderToIndex;
	}
}
