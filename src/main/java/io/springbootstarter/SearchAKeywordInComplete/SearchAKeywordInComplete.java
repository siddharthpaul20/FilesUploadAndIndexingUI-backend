package io.springbootstarter.SearchAKeywordInComplete;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.FrontEndSentDataFormatClasses.SearchAKeywordInCompleteSentFormat;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class SearchAKeywordInComplete 
{
	static final Logger logger = Logger.getLogger(SearchAKeywordInComplete.class);

	private SearchAKeywordInComplete()
	{
		
	}
	
	public static void searchAKeywordInWhole(String word, FolderIndexedList fIndexedList,List<SearchAKeywordInCompleteSentFormat> list) 
	{
		for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
		{
			try {
			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
			      if(obj != null)
				     {
			    	    logger.info("calling the search searchAKeyword method of indexingThefolder.");
			    	    obj.searchAKeyword(word,list);	
				     }
			}catch(NullPointerException e)
			{
				logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
			}
			
		}
		
	}
	
	public static void searchAKeywordInSpecific(String keywordToSearch, String folderCarryingSearch,FolderIndexedList fIndexedList,List<SearchAKeywordInCompleteSentFormat> list)
	{
		for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
		{
			if(entry.getKey().contains(folderCarryingSearch))
			{
				try {
				      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
				      if(obj != null)
					     obj.searchAKeyword(keywordToSearch,list);	
				}catch(NullPointerException e)
				{
					logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
				}
			}
		}
			
	}
}