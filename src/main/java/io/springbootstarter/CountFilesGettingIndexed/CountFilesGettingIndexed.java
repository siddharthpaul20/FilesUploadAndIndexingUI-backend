package io.springbootstarter.CountFilesGettingIndexed;

import org.apache.log4j.Logger;

public class CountFilesGettingIndexed 
{
     private static CountFilesGettingIndexed singleInstance = null;
     private static float totalFilesSubmitted = 0;
     private static float totalFilesIndexed = 0;
     
     static final Logger logger = Logger.getLogger(CountFilesGettingIndexed.class);
     
     private CountFilesGettingIndexed()
     {
    	 
     }
     
	public static float getTotalFilesSubmitted() {
		return totalFilesSubmitted;
	}

	public static void setTotalFilesSubmitted(float totalFilesSubmitted) {
		CountFilesGettingIndexed.totalFilesSubmitted = totalFilesSubmitted;
	}

	public static float getTotalFilesIndexed() {
		return totalFilesIndexed;
	}

	public static void setTotalFilesIndexed(float totalFilesIndexed) {
		CountFilesGettingIndexed.totalFilesIndexed = totalFilesIndexed;
	}

	public static CountFilesGettingIndexed getInstance()
	{
		 if(singleInstance==null)
			 singleInstance = new CountFilesGettingIndexed();
		   
		 return singleInstance;
	}
	
	public static void incrementTotalFilesSubmitted()
	{
		totalFilesSubmitted += 1;
	}
	
	public static void decrementTotalFilesSubmitted()
	{
		totalFilesSubmitted -= 1;
	}
	
	public static void decrementTotalFilesIndexed()
	{
		totalFilesIndexed -= 1;
	}
	
	public static void incrementTotalFilesIndexed()
	{
		totalFilesIndexed += 1;
	}
	
	public static void showProgress()
	{
		logger.info("current progress is :");
		logger.info("totalFilesSubmitted --> "+totalFilesSubmitted);
		logger.info("totalFilesIndexed --> "+totalFilesIndexed);
		logger.info("**********************************************Percentage indexing completed --> "+ (totalFilesIndexed/totalFilesSubmitted)*100);
	}
	
		
}
