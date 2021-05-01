package io.springbootstarter.GetFileStatsSpecific;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.ControllerClass.ControllerClass;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SearchingCase.SearchingCase;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class GetFileStatsSpecific 
{
	static final Logger logger = Logger.getLogger(GetFileStatsSpecific.class);
	
    public static void getFileStats(String folderName,List<Records> list, FolderIndexedList fIndexedList)
    {
    	for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
		{
    		if(entry.getKey().contains(folderName))
    		{
    			try {
  			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
  			      if(obj != null)
  			      {
  			    	for (Map.Entry<String,Records> ent : obj.gethMap().entrySet())
  			    	{
  			    		list.add(ent.getValue());
  			    	}
  			      }
	  			}catch(NullPointerException e)
	  			{
	  				logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
	  			}
	    	}
		}
    }
}
