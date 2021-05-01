package io.springbootstarter.SearchAFileFolderInWhole;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SearchAKeywordInComplete.SearchAKeywordInComplete;
import io.springbootstarter.SearchingCase.SearchingCase;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;

public class SearchAFileFolderInWhole 
{
	static final Logger logger = Logger.getLogger(SearchAFileFolderInWhole.class);
	
	private SearchAFileFolderInWhole() {
		
	}

    public static void startSearchAFileFolderInWhole(String fileName,FolderIndexedList fIndexedList,List<Records> list)
    {
    	int positionOfLastSlash;
    	String strParent, strOurDirectoryName;
    	Records obc;
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())  
		{
    		if(entry.getKey().contains(fileName))
    		{
    			// entry.getKey() is directory type
    			// check if the parent of this directory is already present in fIndexedList
    			positionOfLastSlash = entry.getKey().lastIndexOf('\\');
    			strParent = entry.getKey().substring(0, positionOfLastSlash);
    			strOurDirectoryName = entry.getKey().substring(positionOfLastSlash+1);
    			logger.info(entry.getKey()+"  manually finding the parent --> "+strParent);
    			if( !MyUtilityClass.checkHRetrieve(strParent, fIndexedList.hRetrieve))
    			{
    				logger.info("adding the records -->  "+strOurDirectoryName+"  "+entry.getKey()+"  "+strParent);
    				// then only add the details of this directory
    				
    				obc = new Records(true,strOurDirectoryName,entry.getKey(),0,0,0,0,strParent,sdf.format(new Date(Long.valueOf("1580280289808"))),Long.valueOf("1580280289808") );
    				list.add(obc);
    			}
    		}
			try {
			      IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
			      if(obj != null)
			    	  SearchingCase.searchingByFileName(fileName, obj.gethMap(), list);
			}catch(NullPointerException e)
			{
				logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
			}
			
		}
    }
}
