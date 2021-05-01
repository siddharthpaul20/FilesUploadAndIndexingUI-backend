package io.springbootstarter.SearchAFileFolderInSpecific;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SearchingCase.SearchingCase;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class SearchAFileFolderInSpecific 
{
static final Logger logger = Logger.getLogger(SearchAFileFolderInSpecific.class);
	
	private SearchAFileFolderInSpecific() {
		
	}

    public static void startSearchAFileFolderInSpecific(String folderCarryingSearch,String fileName,FolderIndexedList fIndexedList,List<Records> list)
    {
    	for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
		{
    		if(entry.getKey().contains(folderCarryingSearch))
    		{
    			try {
  			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
  			      if(obj != null)
  			      {
  			    	logger.info("deserializing  ===> "+obj.getFolderToIndex());
  			    	SearchingCase.searchingByFileName(fileName, obj.gethMap(), list);  
  			      }
	  			}catch(NullPointerException e)
	  			{
	  				logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
	  			}
	    	}
		}
    }
}
