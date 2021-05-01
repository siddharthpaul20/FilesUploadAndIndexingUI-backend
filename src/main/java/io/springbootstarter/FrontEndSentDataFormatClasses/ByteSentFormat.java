package io.springbootstarter.FrontEndSentDataFormatClasses;

public class ByteSentFormat 
{
    String character;
    
    private ByteSentFormat() {
    	
    }
    
    
    public ByteSentFormat(String character) {
		super();
		this.character = character;
	}


	public String getCharacter() {
		return character;
	}


	public void setCharacter(String character) {
		this.character = character;
	}


	public String toString()
    {
    	return this.character;
    }
}
