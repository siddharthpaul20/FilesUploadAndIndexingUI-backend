package io.springbootstarter.FrontEndSentDataFormatClasses;

public class WordCloudSpecificSentFormat 
{
    private String text;
    private double weight;
    
    private WordCloudSpecificSentFormat() {
    	
    }

	public WordCloudSpecificSentFormat(String text, double weight) {
		super();
		this.text = text;
		this.weight = weight;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public String toString()
	{
		return this.text+"  "+this.weight;
	}
}
