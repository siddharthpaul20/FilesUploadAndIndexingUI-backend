package io.springbootstarter.FolderIndexedList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.RetrievalObject.RetrievalObject;


public class FolderIndexedList implements java.io.Serializable
{
   private static FolderIndexedList singleInstance = null;
   public Map<String, RetrievalObject> hRetrieve;
   static final Logger logger = Logger.getLogger(FolderIndexedList.class);
   
   private FolderIndexedList()
   {
	   File f = new File("d:\\MainProgramObjectHashmap.ser");
	   if(f.exists())
		{
			try(FileInputStream fis = new FileInputStream(f);
			    ObjectInputStream ois = new ObjectInputStream(fis);)
		      {
		         this.hRetrieve = (Map<String, RetrievalObject>) ois.readObject();
		      }catch(IOException ioe)
		      {
		         logger.error(MyUtilityClass.CONTEXT,ioe);
		      }catch(ClassNotFoundException c)
		      {
		         
		    	  logger.error(MyUtilityClass.CONTEXT,c);
		      }
			logger.info("hRetrieve is loaded into the memory");
		}
	   else
	   {
			//main running for first time.
			this.hRetrieve = new ConcurrentHashMap<>();
			logger.info("New hRetrieve created");
		}
   }
   
   public static FolderIndexedList getInstance()
   {
	   if(singleInstance==null)
		   singleInstance = new FolderIndexedList();
	   
	   return singleInstance;
   }
}
