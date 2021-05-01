package io.springbootstarter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.springbootstarter.FileStorageProperties.FileStorageProperties;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexTheDirectory.IndexTheDirectory;
import io.springbootstarter.IndexingAFile.IndexingAFile;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.PoolExecutor.PoolExecutor;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.TimeAttributeOfFile.TimeAttributeOfFile;
import io.springbootstarter.WatchDirectory.WatchDirectory;

@SpringBootApplication
public class FileInsightApiApp {

	static final Logger logger = Logger.getLogger(FileInsightApiApp.class);
    private static Path fileStorageLocation;
    
    @Autowired
    public FileInsightApiApp(FileStorageProperties fileStorageProperties) 
    {
    	 fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                 .toAbsolutePath().normalize();
	}
    
	public static void main(String[] args) {
        SpringApplication.run(FileInsightApiApp.class, args);
        performOfflinSyncAtStartOfServer();
        try {
			WatchDirectory watcher = new WatchDirectory(fileStorageLocation, true);
			watcher.start();
			
		} catch (IOException e) 
        {
			logger.error("error on starting watcher in fileIinsight api",e);
		}
	}
	
	public static void performOfflinSyncAtStartOfServer()
	{
		logger.info("performing offline sync at the start of the server ************");
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		for (Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
		{
			if(MyUtilityClass.checkIsValidDirectory(entry.getKey()))
			{
				// entry.getKey() is valid directory ,ie still existing
				if(MyUtilityClass.isAlreadyIndexed(entry.getKey(), fIndexedList.hRetrieve))
			    {
			    	// entry.getKey() is already indexed means its indexing file still exists correctly
			    	// we need to perform offline sync for this directory now
					RetrievalObject obc = fIndexedList.hRetrieve.get(entry.getKey());
				   	 if(obc!=null)
				   	 {
				   		 //*******************************************
				   		 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
				   		 File fi = new File(obc.getfileName());   
				   		 long lastModDtOfIndexedFile = fi.lastModified();
						 File indexedDirectory = new File(entry.getKey());
						 long mainDirlstModDt = indexedDirectory.lastModified();
						 //logger.info(lastModDtOfIndexedFile+"  ------  "+mainDirlstModDt);
						 if(mainDirlstModDt>lastModDtOfIndexedFile)
						 {
							 // then do the processing
							 logger.info("mainDirlstModDt > lastModDtOfIndexedFile");
						     offlineSyncDirectories(obj,indexedDirectory,lastModDtOfIndexedFile, fIndexedList);
						 }
						 else
						 {
							 //logger.info("in else part ..");
							 File[] arr = indexedDirectory.listFiles();
							 if(arr!=null)
							 {
								 try
						    	  {
						    		  
						    		  for(File file : arr)
							    	  {
							    		  if(file.isFile()&&MyUtilityClass.isValidExtension(file.getName().toLowerCase()))
							    		  {
							    			 if(file.lastModified() > lastModDtOfIndexedFile)
							    			 {
							    				 // This file is either created new or modified
							    				 //logger.info(" going for synching file");
							    				 synchingFile(obj,file);
							    				 
							    			 }   			  
							    			  
							    		 }
							    		  else if(file.isDirectory()&&file.lastModified() > lastModDtOfIndexedFile) 
							    	       { 
								    			   // This directory is either created new or modified
							    			       //logger.info("going for synching directory");
								    			   synchingDirectory(obj,file); 
							    				   // after processing this directory
								    			   //offlineSyncDirectories(file, lastModDtOfIndexedFile);
								    			   
								    			   if(MyUtilityClass.isAlreadyIndexed(file.getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve))
								    			   {
								    				   // leave it
								    				   // its offline sync will get performed when we iterate over it inside this same function
								    			   }
								    			   else
								    			   {
								    				   //perform the indexing of the current directory
								    				   PoolExecutor exc = PoolExecutor.getInstance();
								    				   IndexTheDirectory.indexTheDirectoryGiven(file.getAbsolutePath().toLowerCase(), obj.getFolderCIndex(), obj.getPathTxtFile(), fIndexedList, exc);
								    			   }
								    			   
							    	       } 
							    	         
							    	  }// end of for , for iterating over listFiles of current entry.getKey()
							    	
						    	  }catch(NullPointerException e)
						    	  {
						    		  logger.error("startingIndexingAndRegisteringFiles throwing nullpointer exception. check here.",e);
						    	  }
							 }
							 
						 } 
				   		 
				   		 //*******************************************
				   	 }
			    }
			    else
			    {
			    	// entry.getkey() is present in Hretreive but its indexing file got deleted
			    	// remove entry.getkey() entry from Hretreive list
			    	MyUtilityClass.removeFromHretrieve(entry.getKey(), fIndexedList);
			    }
			}
			else
			{
				// entry.getKey() now doesnt exists
				MyUtilityClass.removeFromHretrieve(entry.getKey(), fIndexedList);
			}
		    
		}// end of for of iterating over hRetreive
		
	}// end of method
	
	public static void synchingFile(IndexingTheFolder obj,File f)
    {
   	    Path child = f.toPath();
	    obj.removeFromindexMap(child.toString().toLowerCase());
        obj.removeFromHmap(child);
        IndexingAFile fileThread = new IndexingAFile(obj,child);
        PoolExecutor exc = PoolExecutor.getInstance();
        exc.executor.execute(fileThread);
        //addEntryOfFileAtRuntime(child);
    }
    
    public static void synchingDirectory(IndexingTheFolder obj,File folder)
    {
   	    Path child = folder.toPath();
   	    obj.removeFromHmap(child);
   	    obj.addEntryOfFolderAtRuntime(child);
    }
    
    public static void offlineSyncDirectories(IndexingTheFolder obj,File fTIndex,long lastModDtOfIndexedFile, FolderIndexedList fIndexedList)
    {
   	      logger.info("in offlineSyncDirectories");
		  // Listing all the files and directories in the folder to index path
	      if(fTIndex.exists() && fTIndex.isDirectory())
	      {
	    	  logger.info("inside if of offlineSynchDirectories");
	    	  File[] arr = fTIndex.listFiles();
	    	  if( arr != null)
	    	  {
	    		  try
		    	  {
		    		  // FIRST CHECKING IF ANY ENTRY IN HMAP IS DELETED OR NOT
		    		  for (Map.Entry<String,Records> entry : obj.gethMap().entrySet())  
		    		  {
		    			  File fil = new File(entry.getKey());
		    			  if(!fil.exists())
		    			  {
		    				  // this particular entry in Hmap is deleted, i.e,now it does not exists
		    				  if(fil.isDirectory())
		    				  {
		    					  obj.removeFromHmap(fil.toPath());
		    					  //remove its entry from hRetrieve and delete its index file
		    					  IndexTheDirectory.removeTheDirectoryGiven(fil.getAbsolutePath().toLowerCase(), fIndexedList);
		    				  }
		    				  else
		    				  {
		    					  Path pth = fil.toPath();
		    					  obj.removeFromindexMap(pth.toString().toLowerCase());
		    					  obj.removeFromHmap(pth);
		    				  }
		    			  }
		    		  }
		    		  
		    		  // SECOND CHECKING IF ANY FILE IN THE FILESTRUCTURE IS MODIFIED OR CREATED NEWLY
		    		  for(File file : arr)
			    	  {
			    		  if(file.isFile()&&MyUtilityClass.isValidExtension(file.getName().toLowerCase()))
			    		  {
			    			 //logger.info(file.toString());
			    			 //logger.info(file.lastModified() +" ----- "+lastModDtOfIndexedFile);
			    			 //logger.info(TimeAttributeOfFile.getFileDateCreatedInMillisLong(file.toPath())+"  >  "+ lastModDtOfIndexedFile);
			    			 if(file.lastModified() > lastModDtOfIndexedFile|| TimeAttributeOfFile.getFileDateCreatedInMillisLong(file.toPath()) > lastModDtOfIndexedFile)
			    			 {
			    				 // This file is either created new or modified
			    				 //logger.info(file.lastModified() +" > "+lastModDtOfIndexedFile);
			    				 //logger.info("Or "+TimeAttributeOfFile.getFileDateCreatedInMillisLong(file.toPath())+" > "+lastModDtOfIndexedFile);
			    				 synchingFile(obj,file);
			    				 
			    			 }   			  
			    			  
			    		 }
			    		  else if(file.isDirectory()&&file.lastModified() > lastModDtOfIndexedFile) 
			    	       {
			    			       //logger.info(file.toString());
			    			       //logger.info(file.lastModified() +" ---- "+lastModDtOfIndexedFile);
				    			   // This directory is either created new or modified
				    			   synchingDirectory(obj,file); 
			    				   // after processing this directory
				    			   //offlineSyncDirectories(file, lastModDtOfIndexedFile);
				    			   
				    			   // check if this subdirectory is indexed or not
				    			   if(MyUtilityClass.isAlreadyIndexed(file.getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve))
				    			       {
				    				      // leave it
				    				      // its offline sync will get performed when we iterate over it inside this same function
				    			       }
				    			   else
				    			   {
				    				   //this directory is not already indexed, we need to index this directory
				    				   PoolExecutor exc = PoolExecutor.getInstance();
				    				   IndexTheDirectory.indexTheDirectoryGiven(file.getAbsolutePath().toLowerCase(), obj.getFolderCIndex(), obj.getPathTxtFile(),fIndexedList , exc);
				    				   // no need to perform offline sync, as it is being freshly indexed
				    			   }
			    	       } 
			    	         
			    	  }
		    		  
		    	  }catch(NullPointerException e)
		    	  {
		    		  logger.error("startingIndexingAndRegisteringFiles throwing nullpointer exception. check here.",e);
		    	  }
		    	  ///add catch here  
	    	  }
	    	  
	      }
	      
    }
	
}
