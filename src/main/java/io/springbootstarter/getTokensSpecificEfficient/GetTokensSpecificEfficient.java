package io.springbootstarter.getTokensSpecificEfficient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.TokenSentFormat.TokenSentFormat;

public class GetTokensSpecificEfficient 
{
	static final Logger logger = Logger.getLogger(GetTokensSpecificEfficient.class);
	
	public static void getTokensSpecificEfficientMethod(String folderName,List<TokenSentFormat> list,FolderIndexedList fIndexedList)
	{
		logger.info("hello");
		TokenSentFormat tokenDetail;
		for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
		{
    		if(entry.getKey().contains(folderName))
    		{
    			try {
  			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
  			      if(obj != null)
  			      {
  			    	logger.info("Inside getTokensSpecificEfficientMethod ---> "+obj.getFolderToIndex());
  			    	for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> ent : obj.getIndex().entrySet())
  			    	{
  			    		for(Map.Entry<String, Frequency> entity: ent.getValue().entrySet())
  					    {
  						  tokenDetail = new TokenSentFormat(ent.getKey(), entity.getKey(), entity.getValue().getFreq());
  						  list.add(tokenDetail);
  					    }
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
