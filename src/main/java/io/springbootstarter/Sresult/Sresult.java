package io.springbootstarter.Sresult;

import org.apache.log4j.Logger;

public class Sresult implements java.io.Serializable 
{
	private static final long serialversionUID = 98965958L;
	static final Logger logger = Logger.getLogger(Sresult.class);
	private String filePath;
	private int freq;

	public Sresult(String filePath, int freq) {
		this.filePath = filePath;
		this.freq = freq;
	}
	public String getfilePath()
	{
		return filePath;
	}
	public int getfreq()
	{
		return freq;
	}
}