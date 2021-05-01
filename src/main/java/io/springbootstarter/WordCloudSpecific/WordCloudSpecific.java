package io.springbootstarter.WordCloudSpecific;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.FrontEndSentDataFormatClasses.TokenStatisticsSentFormat;
import io.springbootstarter.FrontEndSentDataFormatClasses.WordCloudSpecificSentFormat;
import io.springbootstarter.GetTokensStatisticsSpecific.GetTokensStatisticsSpecific;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class WordCloudSpecific {
	
	static final Logger logger = Logger.getLogger(WordCloudSpecific.class);

	public static void getWordCloudSpecific(String folderName, List<WordCloudSpecificSentFormat> list, FolderIndexedList fIndexedList) 
	{
  
		HashMap<String,Integer> folderWholeWords = new HashMap<>();
    	WordCloudSpecificSentFormat tokenDetail;
    	int currentTokenCount;
    	int oldCount;
    	BigDecimal currentTokenInDecimal;
    	logger.info("inside getWordCloudSpecific --> "+folderName);
        BigInteger totalTokens = BigInteger.valueOf(0);
        // first finding totalTokens in the respective folder
        for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
 		{
     		if(entry.getKey().contains(folderName))
     		{
     			try 
     			{
	   			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
	   			      if(obj != null)
	   			      {
		   			    	logger.info("Inside getWordCloudSpecific ---> "+obj.getFolderToIndex());
		   			    	totalTokens = totalTokens.add(obj.getTotalTokens());
		   			    	// now opening the index hashmap here and copying all the words from
		   			    	// it in our folderWholewords hashmap
		   			    	for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> objectIndexMap : obj.getIndex().entrySet())
		   			    	{
		   			    		// first calculating the current token total occurences IN CURRENT OBJECTREFERENCE HASHMAP
		   			    		currentTokenCount = 0;
		   			    		for(Map.Entry<String, Frequency> innerMapReference: objectIndexMap.getValue().entrySet())
		   					    {
		   						  currentTokenCount = currentTokenCount+innerMapReference.getValue().getFreq();
		   					    }
		   			    		if(folderWholeWords.containsKey(objectIndexMap.getKey()))
		   			    		{
		   			    			// word is present inside ourHashmap already
		   			    			oldCount = folderWholeWords.get(objectIndexMap.getKey());
		   			    			folderWholeWords.put(objectIndexMap.getKey(),oldCount+currentTokenCount);
		   			    		}
		   			    		else
		   			    		{
		   			    			folderWholeWords.put(objectIndexMap.getKey(),currentTokenCount);
		   			    		}
		   			    	}
	   			    	
	   			      }
 	  			}catch(NullPointerException e)
 	  			{
 	  				logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
 	  			}
 	    	}
 		}
        logger.info("total tokens in the folder "+folderName+" ====> "+totalTokens);
        BigDecimal totalTokensInDecimal = new BigDecimal(totalTokens);
        logger.info("total tokens in the folder "+folderName+" ====> "+totalTokensInDecimal);
        logger.info("**************************************************************");
        // we have our foldeWholeWords hashmap ready, now we are going to process it for percentage;
        
        // calculating tokens  percentage and appending in our list;
        for (Map.Entry<String,Integer> entryFolderWholeWords : folderWholeWords.entrySet())
        {
        	currentTokenInDecimal = new BigDecimal(entryFolderWholeWords.getValue());
        	try
			  {
				  currentTokenInDecimal = currentTokenInDecimal.multiply(BigDecimal.valueOf(100));
				  currentTokenInDecimal = currentTokenInDecimal.divide(totalTokensInDecimal,10,RoundingMode.DOWN);				    
			  }catch(ArithmeticException e)
			  {
				  logger.error("Arithmetic Exception at showTokenStatistics.");
			  }
        	
        	
        	tokenDetail = new WordCloudSpecificSentFormat(entryFolderWholeWords.getKey(),currentTokenInDecimal.doubleValue());
        	list.add(tokenDetail);
        } 
		
	}

}
