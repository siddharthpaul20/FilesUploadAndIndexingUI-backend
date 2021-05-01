package io.springbootstarter.SearchByFileSize;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.TokenSentFormat.TokenSentFormat;

public class SearchByFileSize 
{
   static final Logger logger = Logger.getLogger(SearchByFileSize.class);	
   private SearchByFileSize() {
	   
   }
   
   public static void searchByFileSizeLessSpecific(int limitSize,String folderCarryingSearch,FolderIndexedList fIndexedList,List<Records> list)
   {
	   long limitSizeInLong = (long) limitSize;
	   logger.info("limitSizeInLong  --> "+limitSizeInLong);
	   for(Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
	   {
		   if(entry.getKey().contains(folderCarryingSearch))
		   {
			   try 
	 			{
					IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
	 			      if(obj != null)
	 			      {
	 			    	 logger.info("Inside searchByFileSizeLessSpecific ---> "+obj.getFolderToIndex());
	 			    	 for(Map.Entry<String, Records> objectHMap : obj.gethMap().entrySet())
	 			    	 {			 			    	 
	 			    		 if(objectHMap.getValue().getsize() <= limitSizeInLong && !objectHMap.getValue().getDirectoryType())
	 			    		 {
	 			    			 logger.info("adding in list fileName --> "+objectHMap.getKey());
	 			    			 // add the record of this file in list
	 			    			 list.add(objectHMap.getValue());
	 			    		 }
	 			    	 }
	 			      }
	 			} catch(OutOfMemoryError o)
				{
	 				logger.info("out of memory error thrown in controller class getAllTokensWhole");
	 				logger.error(o);
			    }catch(NullPointerException e)
		  		{
		  			logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
		  		}
		   }
	   }
   }
   
   public static void searchByFileSizeMoreSpecific( int limitSize,String folderCarryingSearch,FolderIndexedList fIndexedList,List<Records> list )
   {
	   long limitSizeInLong = (long) limitSize;
	   logger.info("limitSizeInLong  --> "+limitSizeInLong);
	   for(Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
	   {
		   if(entry.getKey().contains(folderCarryingSearch))
		   {
			   try 
	 			{
					IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
	 			      if(obj != null)
	 			      {
	 			    	 logger.info("Inside searchByFileSizeLessSpecific ---> "+obj.getFolderToIndex());
	 			    	 for(Map.Entry<String, Records> objectHMap : obj.gethMap().entrySet())
	 			    	 {			 			    	 
	 			    		 if(objectHMap.getValue().getsize() >= limitSizeInLong && !objectHMap.getValue().getDirectoryType())
	 			    		 {
	 			    			 logger.info("adding in list fileName --> "+objectHMap.getKey());
	 			    			 // add the record of this file in list
	 			    			 list.add(objectHMap.getValue());
	 			    		 }
	 			    	 }
	 			      }
	 			} catch(OutOfMemoryError o)
				{
	 				logger.info("out of memory error thrown in controller class getAllTokensWhole");
	 				logger.error(o);
			    }catch(NullPointerException e)
		  		{
		  			logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
		  		}
		   }
	   }
   }
   
}
