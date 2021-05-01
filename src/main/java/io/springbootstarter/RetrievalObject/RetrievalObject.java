package io.springbootstarter.RetrievalObject;

import org.apache.log4j.Logger;

public class RetrievalObject implements java.io.Serializable
{
   private static final long serialversionUID = 489576138L;
   static final Logger logger = Logger.getLogger(RetrievalObject.class);
   private String folderCIndex;
   private String fileName;
   private String stopwordFile;
   
   public RetrievalObject(String folderCIndex, String fileName, String stopwordFile)
   {
	   this.folderCIndex =folderCIndex.toLowerCase();
	   
	   this.fileName = fileName.toLowerCase();
	   this.stopwordFile = stopwordFile.toLowerCase();
   }
   public String getFolderCIndex()
   {
	   return folderCIndex;
   }
   public String getfileName()
   {
	   return fileName;
   }
   public String getstopwordFile()
   {
	   return stopwordFile;
   }
}