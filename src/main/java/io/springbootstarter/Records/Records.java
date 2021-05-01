package io.springbootstarter.Records;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.log4j.Logger;

 

public class Records implements java.io.Serializable 

{
	private static final long serialversionUID = 158658892L; 

	 static final Logger logger = Logger.getLogger(Records.class);
	
     public long noOfLines;

     public long noOfWords;

     public long noOfChars;

     public long size;

     public String parentFolder;

     public String creationDate;

     public long lastModifiedDate;

     public String fileName;

     public String absolutePath;

     public boolean isDirectoryType; //Whether file or folder
     
     public long getsize()
     {
    	 return size;
     }
     
    public String  getabsolutePath()
    {
    	return absolutePath;
    }
    
    public String  getParentFolder()
    {
    	return parentFolder;
    }

    public boolean getDirectoryType()
    {
    	return isDirectoryType;
    }

     public String getfileName()

     {
           return fileName;
     }

     public long getnoOfBits()

     {
           return noOfChars;
     }

     public long getnoOfWords()

     {
           return noOfWords;
     }

     public long getnoOfLines()
     {
           return noOfLines;
     }

     public Date getCreationDate() throws ParseException
     {
           return new SimpleDateFormat("dd-MM-yyyy").parse(creationDate);
     }
     public long getLastModifiedDate()
     {
    	 return lastModifiedDate;
     }

    

     public Records(boolean isDirectoryType,String fileName,String absolutePath,long noOfLines,long noOfWords,long noOfChars,long size,String parentFolder,String creationDate,long lastModifiedDate )
     {

           this.absolutePath = absolutePath;

           this.isDirectoryType=isDirectoryType;

           this.noOfLines =noOfLines;

           this.noOfWords =noOfWords;

           this.noOfChars =noOfChars;

           this.size =size;

           this.parentFolder =parentFolder;

           this.creationDate =creationDate;

           this.lastModifiedDate =lastModifiedDate;

           this.fileName = fileName;

     }

     public String toString()

    {

        return this.absolutePath+" "+this.fileName + " "+this.creationDate+" "+this.lastModifiedDate + " "+this.size+" "+this.noOfWords + " "

            + this.isDirectoryType + "\n";

    }

 

}
