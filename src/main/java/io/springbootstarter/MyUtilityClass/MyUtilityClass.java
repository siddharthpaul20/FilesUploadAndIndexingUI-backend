package io.springbootstarter.MyUtilityClass;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.log4j.Logger;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.WebSocketController.WebSocketController;

public class MyUtilityClass 
{
	private MyUtilityClass()
	{	
	}
	static final Logger logger = Logger.getLogger(MyUtilityClass.class);
    public static final String INVALID_MESSAGE = "Invalid input entered by user!!!.";
    public static final String CONTEXT = "context";
    public static final String NOT_A_DIRECTORY_MESSAGE = "Entered path is not a directory!!!";
    public static final String FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY = "d:\\IndexesOfUploadDir";
    public static final String FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY = "d:\\StopWordsHashSetForUpload.ser";
    
    public static boolean checkIsValidDirectory(String folderPath)
    {
    	Path path = Paths.get(folderPath);
        if(!path.toFile().exists())
        {
      	  logger.info(INVALID_MESSAGE+" -> "+folderPath);
      	  return false;
        }
        else
        {
      	  if(!path.toFile().isDirectory())
      	  {
      		  logger.info(NOT_A_DIRECTORY_MESSAGE+" -> "+folderPath);
      		  return false;
      	  }
        }
        
        return true;
    }
    
    public static boolean checkIsValidFile(String folderPath)
    {
    	Path path = Paths.get(folderPath);
        if(!path.toFile().exists()||path.toFile().isDirectory())
        {
      	  logger.info(INVALID_MESSAGE+" -> "+folderPath);
      	  return false;
        }
        
        return true;
    }
    
    public static boolean checkValidInputs(String folderCIndex,String pathTxtFile)
    {
 	   if(checkIsValidDirectory(folderCIndex)&&checkIsValidFile(pathTxtFile))
 		   return true;
 	   
 	   return false;
    }
    
    synchronized public static String getIndexFileName(String folderPathToIndex,String folderCIndex)
    {
    	int i=1;
    	String justFolderPathToIndex = new File(folderPathToIndex).getName().toLowerCase();
    	String path = folderCIndex+"//"+justFolderPathToIndex;
    	File file = new File(path+".ser"); 
    	if(!file.exists())
    		return justFolderPathToIndex+".ser";
    	file =new File(path+String.valueOf(i)+".ser");
    	while(file.exists())
    	{
    		i++;
    		file =new File(path+String.valueOf(i)+".ser");
    	}
    		 
    	return justFolderPathToIndex+String.valueOf(i)+".ser";
    }
    
    synchronized public static void addTohRetrieve(String folderPath,RetrievalObject abc,FolderIndexedList fIndexedList)
	{
		fIndexedList.hRetrieve.put(folderPath.toLowerCase(),abc);
		SerializingTheObject.serializingFolderIndexedListObject(fIndexedList);
	}
	
	public static boolean checkHRetrieve(String folderPath,Map<String, RetrievalObject> hRet)
	{
		return hRet.containsKey(folderPath.toLowerCase());
	}
	public static RetrievalObject getHRetrieve(String folderPath,Map<String, RetrievalObject> hRet)
	{
		return hRet.get(folderPath.toLowerCase());
	}
	synchronized public static void removeFromHretrieve(String folderPath,FolderIndexedList fIndexedList)
	{
		fIndexedList.hRetrieve.remove(folderPath.toLowerCase());
		SerializingTheObject.serializingFolderIndexedListObject(fIndexedList);
	}
	synchronized public static boolean isAlreadyIndexed(String folderPathToIndex,Map<String, RetrievalObject> hRet)
	{
		if(checkHRetrieve(folderPathToIndex, hRet))
        {
      	  //Folder is already indexed.
      	  RetrievalObject obc = getHRetrieve(folderPathToIndex, hRet);
      	  Path path1;
      	  Path path2;
      	  Path path3;
      	  path1 = Paths.get(obc.getFolderCIndex());
      	  path2 = Paths.get(obc.getstopwordFile());
      	  path3 = Paths.get(obc.getfileName()); // this is not existing because it is just getFileName
      	  if(path1.toFile().exists()&&path2.toFile().exists()&&path3.toFile().exists())
      	  {
      		// all the above path exists
          	// no need to index the folder  
      		WebSocketController.template.convertAndSend(WebSocketController.SENDING_URL,folderPathToIndex+ " :-> is alredy indexed.");
      		  return true;
      	  }
      	  // any of the above path now not exists
  		  // need to reindex the folder
      	  return false;
	    }
		// folderPathToIndex is not indexed : not present in hRet
		return false;
	}
	
	public static boolean isValidExtension(String fileNameToIndex)
	{
		if(fileNameToIndex.endsWith(".txt")||fileNameToIndex.endsWith(".java")||fileNameToIndex.endsWith(".cpp")||fileNameToIndex.endsWith(".py"))
			return true;
		
		return false;
	}
}
