package io.springbootstarter.DisplayRecords;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import io.springbootstarter.Records.Records;

public class DisplayRecords 
{
	static final Logger logger = Logger.getLogger(DisplayRecords.class);

	private DisplayRecords()
	{		
	}
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public static void displayRecord1(Records ob)
    {
    	 System.out.format("%n%60s %30s %20s %20s %10s %10s %10s %10s %40s%n", ob.getabsolutePath(), ob.fileName,ob.creationDate,sdf.format(ob.lastModifiedDate),ob.size,ob.noOfChars,ob.noOfWords,ob.noOfLines, ob.parentFolder);
    }
    public static void displayRecord2(Records ob)
    {
    	 System.out.format("%n%60s %30s %15s %15s %10s %10s %10s %10s%n", ob.absolutePath, ob.fileName,ob.creationDate,sdf.format(ob.lastModifiedDate),ob.size,ob.noOfChars,ob.noOfWords,ob.noOfLines);
    }
    
}