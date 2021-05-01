package io.springbootstarter.FrontEndSentDataFormatClasses;

import java.math.BigDecimal;

public class TokenStatisticsSentFormat 
{
    private String word;
    private int freq;
    private String percent;
    
    private TokenStatisticsSentFormat() {
    	
    }
    
    public TokenStatisticsSentFormat(String word, int freq, String percent) {
		super();
		this.word = word;
		this.freq = freq;
		this.percent = percent;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public String toString()
	{
		return this.word+" - "+this.freq+" - "+this.percent;
	}
    
}
