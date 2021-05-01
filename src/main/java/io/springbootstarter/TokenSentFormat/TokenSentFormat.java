package io.springbootstarter.TokenSentFormat;

public class TokenSentFormat 
{
    private String word;
    private String absolutePath;
    private int freq;
    
    
    public TokenSentFormat(String word, String absolutePath, int freq) {
		super();
		this.word = word;
		this.absolutePath = absolutePath;
		this.freq = freq;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
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
	
	public String toString()
	{ 
		  return word+" "+absolutePath+" "+freq;  
	}  
	
}
