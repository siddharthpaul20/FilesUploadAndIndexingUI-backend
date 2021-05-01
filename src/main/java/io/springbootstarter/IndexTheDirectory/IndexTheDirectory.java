package io.springbootstarter.IndexTheDirectory;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.ControllerClass.ControllerClass;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.PoolExecutor.PoolExecutor;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.ThreadForIndexing.ThreadForIndexing;
import io.springbootstarter.WebSocketController.WebSocketController;

public class IndexTheDirectory 
{
	static final Logger logger = Logger.getLogger(IndexTheDirectory.class);
	
    public static void indexTheDirectoryGiven(String folderPathToIndex,String folderCIndex,String pathTxtFile,FolderIndexedList fIndexedList,PoolExecutor exc)
    {
    	/*if(!MyUtilityClass.checkValidInputs(inp.getFolderCIndex(), inp.getPathTxtFile()))
		return; //The paths entered by user are invalid
	
	  Implement here to send a response back to user defining invalid path
	
	String[] folderToIndex = inp.getFolderToIndexes().split(",");
	for(String folderPathToIndex : folderToIndex)
	{
		if(!MyUtilityClass.checkIsValidDirectory(folderPathToIndex))
			continue;
		 //check if the folerPathToIndex is indexed already or not
		 */
		if(!MyUtilityClass.isAlreadyIndexed(folderPathToIndex, fIndexedList.hRetrieve))
		{
			logger.info(folderPathToIndex);
			// need to index folderPathToIndex
			// creating the fileName in which indexes of this folder will be stored
			String indexFileName = folderCIndex+"\\"+MyUtilityClass.getIndexFileName(folderPathToIndex, folderCIndex);
			IndexingTheFolder obj = new IndexingTheFolder(folderPathToIndex, folderCIndex,pathTxtFile,indexFileName);
			SerializingTheObject.serializingIndexingTheFolderObject(obj);
			// adding the folderPath to index in the fIndexedList.hRetrieve here
			RetrievalObject xyz = new RetrievalObject(obj.getFolderCIndex(),obj.getIndexFileName(),obj.getPathTxtFile());
			MyUtilityClass.addTohRetrieve(obj.getFolderToIndex(), xyz, fIndexedList); //folderToIndex comes in string lowercase format only
			
			// starting doing indexing of the folder
			logger.info("Creating indexes of --> "+folderPathToIndex+"  ,kindly wait for a moment...");
			ThreadForIndexing indexingObj = new ThreadForIndexing(obj,exc,fIndexedList);
			exc.executor.execute(indexingObj);
		}
		{
			WebSocketController.template.convertAndSend("/topic/server-broadcaster", folderPathToIndex+ " is already indexed.");
		}
		/*else
			{
			 logger.info("Already Indexed --> "+folderPathToIndex);
			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(folderPathToIndex, fIndexedList.hRetrieve);
			 obj.showindexMap(fIndexedList);
			}*/
    }
    
    public static void removeTheDirectoryGiven(String folderPathToRemove,FolderIndexedList fIndexedList)
    {
    	for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
    	{
    		if(entry.getKey().contains(folderPathToRemove))
    		{
    			RetrievalObject xyz = entry.getValue();
      		    MyUtilityClass.removeFromHretrieve(entry.getKey(), fIndexedList);
      		    File f = new File(xyz.getfileName());
      		    if(f.delete())
      			    logger.info("successfully deleted file ---> "+xyz.getfileName());
      		    else
      			    logger.info("File is not able to deleted ---> "+xyz.getfileName());	
				  
    		}
    	}
    }
}
