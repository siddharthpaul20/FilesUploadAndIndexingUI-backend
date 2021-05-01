package io.springbootstarter.SerializingTheObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.springbootstarter.FileStorageProperties.FileStorageProperties;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.RetrievalObject.RetrievalObject;

public class SerializingTheObject 
{
	private static final long serialversionUID = 12669877L;
	static final Logger logger = Logger.getLogger(SerializingTheObject.class);
		
	public synchronized static void serializingIndexingTheFolderObject(IndexingTheFolder obj)
	{
	
	        File ft = new File(obj.getIndexFileName());
	        try( FileOutputStream f= new FileOutputStream(ft);
	        	 ObjectOutputStream out = new ObjectOutputStream(f);)
	        {    
	            //Saving of object in a file 
	            // Method for serialization of object 
	            out.writeObject(obj);
	            logger.info("Writing IndexingTheFolderObject in file ---->  "+obj.getIndexFileName());
	        }   
	        catch(IOException ex) 
	        { 
	            logger.error(MyUtilityClass.CONTEXT,ex); 
	        }       
	}
	
	public synchronized static IndexingTheFolder deserializingIndexingTheFolderObject(String folderToIndex,Map<String,RetrievalObject> hRetrieve) 
	{	
		   //deserialize using retrieve
		   IndexingTheFolder obj = null;
		   RetrievalObject obc = MyUtilityClass.getHRetrieve(folderToIndex, hRetrieve);
		   try ( FileInputStream fis = new FileInputStream(obc.getfileName());
			     ObjectInputStream ois = new ObjectInputStream(fis);
			   )
		      {
		         obj = (IndexingTheFolder) ois.readObject();
		         
		         // changes made here for .ser file, so that we can load the latest stopwords hashset for folder that
		         // are indexed by uploading
		         
		         if( obj.getPathTxtFile().endsWith(".ser"))
		         {
		        	 File f = new File(obj.getPathTxtFile());
		        	 if(f.exists())
		        	 {
		        		 HashSet<String> stopWordsHashSet = SerializingTheObject.deserializingStopWordsHashSet();
		        		 obj.stopwords.clear();
		        		 obj.stopwords.addAll(stopWordsHashSet);
		        	 }
		         }
		         
		      }catch(IOException ioe)
		      {
		         logger.error(MyUtilityClass.CONTEXT,ioe);
		         logger.info("Closing the application.");
		         return null;
		      }catch(ClassNotFoundException c)
		      {
		         logger.error(MyUtilityClass.CONTEXT,c);
		         logger.info("Closing the application.");
		         return null;
		      }catch(NullPointerException e)
		      {
		    	  logger.info("null pointer exception is coming while deserializing  PAULS -->  "+ folderToIndex,e);
		    	  obj = null;
		      }
		   return obj;
	}
	
	public synchronized static void serializingFolderIndexedListObject(FolderIndexedList fIndexedList)
	{
		logger.info("writing on fIndexedList.hRetrieve");
		File ft = new File("d:\\MainProgramObjectHashmap.ser");
        try( FileOutputStream f= new FileOutputStream(ft);
        	 ObjectOutputStream out = new ObjectOutputStream(f);)
        {    
            //Saving of object in a file 
            // Method for serialization of object 
            out.writeObject(fIndexedList.hRetrieve);
        }   
        catch(IOException ex) 
        { 
            logger.error(MyUtilityClass.CONTEXT,ex); 
        }  
	}
	
	public static void serializingStopWordsHashSet(HashSet<String> stopWords)
	{
		logger.info("writing on HashSet stopWords");
		File ft = new File("d:\\StopWordsHashSetForUpload.ser");
		try( FileOutputStream f= new FileOutputStream(ft);
	         ObjectOutputStream out = new ObjectOutputStream(f);)
	    {    
	         //Saving of object in a file 
	         // Method for serialization of object 
	         out.writeObject(stopWords);
	    }   
	    catch(IOException ex) 
	    { 
	         logger.error(MyUtilityClass.CONTEXT,ex); 
	    } 
		logger.info("successfully serialized the stopWords Hashset");
	}
	
	public static HashSet<String> deserializingStopWordsHashSet()
	{
		HashSet<String> stopWords = new HashSet<>();
		File f = new File("d:\\StopWordsHashSetForUpload.ser");
		   if(f.exists())
			{
			   logger.info("Stopwords file already exist ");
			   try ( FileInputStream fis = new FileInputStream(f);
				     ObjectInputStream ois = new ObjectInputStream(fis);
				   )
				   {
				       stopWords = (HashSet<String>) ois.readObject();
				   }catch(IOException ioe)
				   {
				       logger.error(MyUtilityClass.CONTEXT,ioe);
				       logger.info("Closing the application.");
				       stopWords = new HashSet<String>();
				   }catch(ClassNotFoundException c)
				   {
				       logger.error(MyUtilityClass.CONTEXT,c);
				       logger.info("Closing the application.");
				       stopWords = new HashSet<String>();
				   }catch(NullPointerException e)
				   {
				       logger.info("null pointer exception is coming while deserializing  PAULSed -->  ",e);
				       stopWords = new HashSet<String>();
				   }
				   return stopWords;   
	        }
		   else
			   return stopWords;
	
    }
}
