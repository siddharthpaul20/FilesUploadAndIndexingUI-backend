package io.springbootstarter.SetStopWordsForUpload;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class SetStopWordsForUpload 
{
	static final Logger logger = Logger.getLogger(SetStopWordsForUpload.class);
	
	private SetStopWordsForUpload()
	{
		
	}
	
    public static void setStopWordsForUpload(String stopWordsContainingString)
    {
    	logger.info("Inside setStopWordsForUpload-----> "+stopWordsContainingString);
    	String[] words = stopWordsContainingString.split(",");
    	HashSet<String> stopWords = SerializingTheObject.deserializingStopWordsHashSet();
    	if(stopWords==null)
    		stopWords = new HashSet<String>();
    	// now serialize the stopWords set
    	Collections.addAll(stopWords,words);
    	SerializingTheObject.serializingStopWordsHashSet(stopWords);
    	//for(String i : stopWords)
    		//logger.info(i);
    }
}
