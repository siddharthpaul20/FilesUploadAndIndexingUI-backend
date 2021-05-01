package io.springbootstarter.WatchDirectory;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.springbootstarter.CountFilesGettingIndexed.CountFilesGettingIndexed;
import io.springbootstarter.FileStorageProperties.FileStorageProperties;
import io.springbootstarter.FileUploadAndDownloadException.FileStorageException;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexTheDirectory.IndexTheDirectory;
import io.springbootstarter.IndexingAFile.IndexingAFile;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.PoolExecutor.PoolExecutor;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.WebSocketController.WebSocketController;

public class WatchDirectory extends Thread
{

	static final Logger logger = Logger.getLogger(WatchDirectory.class);
	private volatile boolean exit = false;
    private final WatchService watcher;
    private Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    public FolderIndexedList fIndexedList;
    public PoolExecutor exc;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    public WatchDirectory(Path dir, boolean recursive) throws IOException 
    {
    	WebSocketController.template.convertAndSend("/topic/server-broadcaster", "watchere started ho gaya bhai.");
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;
        this.exc = PoolExecutor.getInstance();
        this.fIndexedList = FolderIndexedList.getInstance();

        if (recursive) 
        {
        	String str = String.format("Scanning %s ...%n", dir);
            logger.info("==========================================================================================> "+str);
            registerAll(dir);
            logger.info("==========================================================================================> Done.");
        }
        else 
        {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }
    
    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException, AccessDeniedException
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) 
        {
            Path prev = keys.get(key);
            if (prev == null) 
            {
            	String str = String.format("register: %s%n", dir);
                logger.info("==========================================================================================> "+str);
            } else 
            {
                if (!dir.equals(prev)) 
                {
                	String str = String.format("update: %s -> %s%n", prev, dir);
                    logger.info("==========================================================================================> "+str);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws AccessDeniedException, IOException
    {
        try
        {
        	// register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
                {
                	
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    //logger.info("Failed to access file: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch(AccessDeniedException e)
        {
        	logger.error("==========================================================================================> Access denied exception for directory : "+start);
        }
    }

    /**
     * Creates a WatchService and registers the given directory
     * @param hRetrieve 
    */
    
    public void stops()
    {
    	Thread t = Thread.currentThread();
        String name = t.getName();
        logger.info("==========================================================================================> name of closing thread =" + name);	
    	exit = true;
    }
    
    //***************************************************************
    
    
    @Override
    public void run()
    {
         try 
         {    	
		    	while(!exit)
		    	{
		
		    		if(Thread.interrupted())
		    		{  
			    		logger.info("==========================================================================================> Exiting the watcher_thread");
			    		keys.clear();
			    		watcher.close();
			    		return;
			    	} 
		    		
		            // wait for key to be signalled
		            WatchKey key;
		            try 
		            {
		                key = watcher.poll();
		            } catch (Exception x) 
		            {
		                return;
		            }
		
		            Path dir = keys.get(key);
		            if (dir == null) 
		            {
		                //System.err.println("WatchKey not recognized!!")
		                continue;
		            }
		
		            for (WatchEvent<?> event: key.pollEvents()) 
		            {
		                Kind<?> kind = event.kind();
		
		                // TBD - provide example of how OVERFLOW event is handled
		                if (kind == OVERFLOW) {
		                    continue;
		                }
		                
		
		                // Context for directory entry event is the file name of entry
		                WatchEvent<Path> ev = cast(event);
		                Path name = ev.context();
		                Path child = dir.resolve(name);
		
		                
		                // Writing logic of respective cases
		                
		                if(kind==ENTRY_CREATE)
		                {
		                	//Logic for new .txt file
		                	logger.info("==========================================================================================> "+ event.kind().name()+"  "+name+"    "+child);
		                	WebSocketController.template.convertAndSend("/topic/server-broadcaster", event.kind().name()+"  "+name+"    "+child);
		                	if(MyUtilityClass.isValidExtension(child.toFile().getAbsolutePath().toLowerCase()))
		                	{
		                		 Thread.sleep(2);
		                		 int positionOfLastSlash = child.toFile().getAbsolutePath().lastIndexOf('\\');
		                	     String parentDir = child.toFile().getAbsolutePath().substring(0,positionOfLastSlash).toLowerCase();
		                	     logger.info("parent directory -----> "+parentDir);
		                	     
		                	     if(MyUtilityClass.checkIsValidDirectory(parentDir))
		             			 {
		                	    		 //parentDir is valid directory
		                	    		 //checking if the parentDir is already indexed or not
		                	    		 
		                	    		 if(!MyUtilityClass.isAlreadyIndexed(parentDir, fIndexedList.hRetrieve))
		                	    		 {
		                	    			 // parentDir is not indexed already.
		                	    			 //indexing the parent directory
		                	    			 IndexTheDirectory.indexTheDirectoryGiven(parentDir, MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
		                	    			 logger.info("Indexed the unindexed folder in the watcher create event completed .");
		                	    		 }
		                	    		 else
		                	    		 {
		                	    			 // parentDir is already indexed
		                	    			 // load the parentDir obj
		                	    			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(parentDir, fIndexedList.hRetrieve);
		                	    			 IndexingAFile fileThread = new IndexingAFile(obj,child);
		                	    			 exc.executor.execute(fileThread); // this file is indexed in multithreading environment
		                	    		 }
		                	     }
		                	 
		                	     
		                	}
		                	else
		                	{
		                		// Logic for new Folder added
		                		File file = new File(child.toString());
		                		if(file.isDirectory())
		                		{
		                			
		                			logger.info("RUNNING OUR FUNCTION********44444444444");
		                			// first index the directory and then register the directory to watcher
		                			IndexTheDirectory.indexTheDirectoryGiven(file.getAbsolutePath().toLowerCase(), MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
		                			logger.info("gone out");
		                			//register(child);
		                		}
		                	}
		                }
		                else
		                {
		                	if(kind==ENTRY_DELETE)
		                	{
		                		logger.info("==========================================================================================> "+ event.kind().name()+"  "+name+"    "+child);
		                		WebSocketController.template.convertAndSend("/topic/server-broadcaster", event.kind().name()+"  "+name+"    "+child);
		                		if(MyUtilityClass.isValidExtension(child.toFile().getAbsolutePath().toLowerCase()))
		                		{
		                			Thread.sleep(2);
		                			// It is a file, get its parentDir
		                			int positionOfLastSlash = child.toString().lastIndexOf('\\');
			                	    String parentDir = child.toFile().getAbsolutePath().substring(0,positionOfLastSlash).toLowerCase();
			                	    logger.info("parent directory -----> "+parentDir);
			                	    
			                	    //check if the parentDir is already indexed or not
			                	    if(MyUtilityClass.checkIsValidDirectory(parentDir))
			             			 {
			                	    		 //parentDir is valid directory
			                	    		 //checking if the parentDir is already indexed or not
			                	    		 
			                	    		 if(!MyUtilityClass.isAlreadyIndexed(parentDir, fIndexedList.hRetrieve))
			                	    		 {
			                	    			 // parentDir is not indexed already.
			                	    			 //indexing the parent directory
			                	    			 IndexTheDirectory.indexTheDirectoryGiven(parentDir, MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
			                	    			 logger.info("Indexed the unindexed folder in the watcher delete event completed .");
			                	    			 // here when parentDir is being getting indexed, deleted file will not get indexed
			                	    			 // as it is not present in the file system
			                	    		 }
			                	    		 else
			                	    		 {
			                	    			 // parentDir is already indexed
			                	    			 // load the parentDir obj
			                	    			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(parentDir, fIndexedList.hRetrieve);
			                	    			 obj.removeFromindexMap(child.toFile().getAbsolutePath().toLowerCase());
			                		    	     // Removing the file from Hmap
			                			         obj.removeFromHmap(child);  
			                	    		 }
			                	     }
		                		}
		                		else
		                		{
		                			// folder is being deleted
		                			// here simple remove the folder entry from fIndexedList and delete its indexingFile
		                			
		                			// first retreiveing indexingFileName from fIndexedList
		                			IndexTheDirectory.removeTheDirectoryGiven(child.toFile().getAbsolutePath().toLowerCase(), fIndexedList);
		                		}
		                	}
		                	else
		                	{
		                		if(kind==ENTRY_MODIFY)
		                		{
		                			logger.info("==========================================================================================> "+ event.kind().name()+"  "+name+"    "+child);
		                			WebSocketController.template.convertAndSend("/topic/server-broadcaster", event.kind().name()+"  "+name+"    "+child);
		                			Thread.sleep(2);
		                			//first checking given path is valid or not
		                			if(child.toFile().exists())
		                			{
		                				// child is a valid existing path
		                				if(MyUtilityClass.isValidExtension(child.toFile().getAbsolutePath().toLowerCase()))
			                			{
		                					// child is a valid existing file
		                					// we need to delete the old records of the file and insert a new one in its parent folder
		                					
		                					// get the parentDir of child
		                					int positionOfLastSlash = child.toString().lastIndexOf('\\');
					                	    String parentDir = child.toFile().getAbsolutePath().substring(0,positionOfLastSlash).toLowerCase();
					                	    logger.info("parent directory -----> "+parentDir);
					                	    
					                	     //parentDir is valid directory
			                	    		 //checking if the parentDir is already indexed or not
			                	    		 
			                	    		 if(!MyUtilityClass.isAlreadyIndexed(parentDir, fIndexedList.hRetrieve))
			                	    		 {
			                	    			 // parentDir is not indexed already.
			                	    			 //indexing the parent directory
			                	    			 IndexTheDirectory.indexTheDirectoryGiven(parentDir, MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
			                	    			 logger.info("Indexed the unindexed folder in the watcher modify event completed .");
			                	    			 // here when parentDir is being getting indexed, modified file will also get indexed
			                	    		 }
			                	    		 else
			                	    		 {
			                	    			 // parentDir is already indexed
			                	    			 // load the parentDir obj
			                	    			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(parentDir, fIndexedList.hRetrieve);
			                	    			 obj.removeFromindexMap(child.toFile().getAbsolutePath().toLowerCase());
			                		    	     // Removing the file from Hmap
			                			         obj.removeFromHmap(child);
			                			         IndexingAFile fileThread = new IndexingAFile(obj,child);
			                       				 exc.executor.execute(fileThread); // this file is indexed in multithreading environment
			                       				 // child file which is modified will get indexed
			                	    		 }
		                					
			                			}
			                			else
			                			{
			                				// it is known that file is a valid path
			                				// first check it is a directory or not
			                				
			                				if(child.toFile().isDirectory())
			                				{
			                					//child is a valid existing directory path
			                					// this directory got modified, we need to perform offline sync code on this directory
			                					
			                					//check if this directory is already indexed or not
			                					if(!MyUtilityClass.isAlreadyIndexed(child.toFile().getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve))
				                	    		 {
				                	    			 // Directory is not indexed already.
				                	    			 //indexing the parent directory
				                	    			 IndexTheDirectory.indexTheDirectoryGiven(child.toFile().getAbsolutePath().toLowerCase(), MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
				                	    			 logger.info("Indexed the unindexed folder in the watcher modify event completed .");
				                	    			 // here when Directory is being getting indexed no need to check anything else
				                	    		 }
				                	    		 else
				                	    		 {
				                	    			 // Directory is already indexed
				                	    			 // load the Directory obj
				                	    			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(child.toFile().getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve);
				                	    			 
				                	    			 //perform offlineSynch for this modified directory
				                	    			 obj.offlineSync(child.toFile().getAbsolutePath().toLowerCase(),fIndexedList);
				                	    		 }
			                					
			                				}
			                				else
			                				{
			                					// child is a valid existing path
			                					// but it is neither directory nor a valid existing file extension
			                					
			                					// we do not need to do anything with it
			                				}
			                				
			                				
			                				
			                			}
		                			}
		                			else
		                			{
		                				// THE PARTICULAR ENTRY GOT DELETED
		                				
		                				// child is not a valid existing part
		                				// delete the entries of the child if it is present anywhere like in fIndexedList or in fileStats of some folder etc
		                				
		                				// if child is a file, then remove its entry from parentDirectory filestats and tokens
		                				
		                				//****************** COPY PASTE CODE OF DELETE EVENT *******************************
		                				if(MyUtilityClass.isValidExtension(child.toFile().getAbsolutePath().toLowerCase()))
				                		{
				                			Thread.sleep(2);
				                			// It is a file, get its parentDir
				                			int positionOfLastSlash = child.toString().lastIndexOf('\\');
					                	    String parentDir = child.toFile().getAbsolutePath().substring(0,positionOfLastSlash).toLowerCase();
					                	    logger.info("parent directory -----> "+parentDir);
					                	    
					                	    //check if the parentDir is already indexed or not
					                	    if(MyUtilityClass.checkIsValidDirectory(parentDir))
					             			 {
					                	    		 //parentDir is valid directory
					                	    		 //checking if the parentDir is already indexed or not
					                	    		 
					                	    		 if(!MyUtilityClass.isAlreadyIndexed(parentDir, fIndexedList.hRetrieve))
					                	    		 {
					                	    			 // parentDir is not indexed already.
					                	    			 //indexing the parent directory
					                	    			 IndexTheDirectory.indexTheDirectoryGiven(parentDir, MyUtilityClass.FOLDER_C_INDEX_FOR_UPOLOAD_DIRECTORY, MyUtilityClass.FOLDER_PATH_TXT_FILE_FOR_UPLOAD_DIRECTORY, fIndexedList, exc);
					                	    			 logger.info("Indexed the unindexed folder in the watcher delete event completed .");
					                	    			 // here when parentDir is being getting indexed, deleted file will not get indexed
					                	    			 // as it is not present in the file system
					                	    		 }
					                	    		 else
					                	    		 {
					                	    			 // parentDir is already indexed
					                	    			 // load the parentDir obj
					                	    			 IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(parentDir, fIndexedList.hRetrieve);
					                	    			 obj.removeFromindexMap(child.toFile().getAbsolutePath().toLowerCase());
					                		    	     // Removing the file from Hmap
					                			         obj.removeFromHmap(child);  
					                	    		 }
					                	     }
				                		}
				                		else
				                		{
				                			// folder is being deleted
				                			// here simple remove the folder entry from fIndexedList and delete its indexingFile
				                			
				                			// first retreiveing indexingFileName from fIndexedList
				                			IndexTheDirectory.removeTheDirectoryGiven(child.toFile().getAbsolutePath().toLowerCase(), fIndexedList);
				                		}
		                				//****************** COPY PASTE CODE OF DELETE EVENT END *******************************
		                			} // end of else of modify code
		                		} // end of if kind == modify
		                	}// end of else of event == delete
		                } // end of else of event == create
		                
		                
		                
		                
		                
		                
		                
		                if (recursive && (kind == ENTRY_CREATE)) 
		                {
		                    try 
		                    {
			                     if (Files.isDirectory(child, NOFOLLOW_LINKS)) 
			                     {
			                         registerAll(child);
			                     }
			                        
		                    } catch (IOException x) 
		                      {
		                           logger.error(MyUtilityClass.CONTEXT, x);
		                      }
		                }
		                
		                
		                
		                
		            } // end of for loop for poll events
		
		            // reset key and remove from set if directory no longer accessible
		            boolean valid = key.reset();
		            if (!valid) 
		            {
		                keys.remove(key);
		
		                // all directories are inaccessible
		                if (keys.isEmpty()) 
		                {
		                    break;
		                }
		            }
		        }// end of while of run
		    	
		    	if(Thread.interrupted())
		    	{  
		    		logger.info("==========================================================================================> Exiting the watcher_thread");
		    		keys.clear();
		    		watcher.close();
		    		return;
		    	}  
         }catch (IOException e) 
         {
        	 logger.error("==========================================================================================> IOException thrown at Wathdir.",e);
		 }catch (InterruptedException e) 
          {
			 logger.error("==========================================================================================> InterruptedException thrown at Wathdir.",e);
		  } 
    	
    } // end of run() method
    
    
    //***************************************************************
	
}
