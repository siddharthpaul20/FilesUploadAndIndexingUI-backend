package io.springbootstarter.Frequency;

public class Frequency implements java.io.Serializable
{
   private int freq;
   
   private Frequency() {
   }
   public Frequency(int freq)
   {
	   this.freq = freq;
   }
   
   public int  getFreq()
   {
	   return freq;
   }
   public void setFreq(int freq)
   {
	   this.freq = freq;
   }
}
