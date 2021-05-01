package io.springbootstarter.IndexingAFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import io.springbootstarter.CountFilesGettingIndexed.CountFilesGettingIndexed;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.Records.Records;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.TimeAttributeOfFile.TimeAttributeOfFile;
import io.springbootstarter.WebSocketController.WebSocketController;

public class IndexingAFile  implements Runnable
{
	private static final long serialversionUID = 1266985938L;
	private Path path;
	private IndexingTheFolder obj;
	static final Logger logger = Logger.getLogger(IndexingAFile.class);
	
	public IndexingAFile(IndexingTheFolder obj,Path path)
	{
		this.obj = obj;
		this.path = path;
		
	}
	
	public void run()
	{
		Instant start = Instant.now();
		logger.info("Now indexing file ---->  "+path);
		CountFilesGettingIndexed.incrementTotalFilesSubmitted();
	    WebSocketController.template.convertAndSend("/topic/server-broadcaster", WebSocketController.buildNextMessage());
		obj.addEntryOfFileAtRuntime2(path);
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).getSeconds();  //in seconds
		logger.info("\n************Time taken for indexing the : "+path.toString()+"   file************* "+timeElapsed+" seconds");
		CountFilesGettingIndexed.incrementTotalFilesIndexed();
  	    WebSocketController.template.convertAndSend("/topic/server-broadcaster", WebSocketController.buildNextMessage());
		CountFilesGettingIndexed.showProgress();
	}
    
	public void doIndependentIndexingOfFile(String fileName)
	{
	    if(Files.isReadable(path))
   	    {
	    	  BigInteger totalTokens = new BigInteger("0");
	    	  Map<String, ConcurrentHashMap<String,Frequency>> index = new ConcurrentHashMap<>() ;
	          ConcurrentMap<String,Records> hMap = new ConcurrentHashMap<>();
	    	  File file= new File(path.toString());
	    	  // Now reading each file and creating index for it
			  //Initializing charCount, wordCount and lineCount to 0
			  long charCount = 0;
			  long wordCount = 0;
			  long lineCount = 0;
			  String currentLine;
			  String wrd;
			  String wrdCleaned;
			  HashSet<String> stopwords = SerializingTheObject.deserializingStopWordsHashSet();
			  try(BufferedReader reader = new BufferedReader(new FileReader(file));)
				 {
		    		 currentLine = reader.readLine();
		    		 while (currentLine != null)
		    		 {
		    			//Updating the lineCount
	                     lineCount++;
	                     //Updating the Charcount
	                     charCount = charCount + currentLine.length();
	                     currentLine = processingLine2(currentLine);
		    			//Updating the wordCount
	                     String[] words = currentLine.split(" ");
	                     wordCount = wordCount + words.length;
		    			 for(String word : words)
		    			 {
		    				if(stopwords.contains(word))
		    					continue;
		    				wrd =word.toLowerCase();
		    				
		    				logger.info(wrd+"  ========================   "+file.getAbsolutePath());
		    				totalTokens = addToindexMap(wrd,file.getAbsolutePath().toLowerCase(),index,totalTokens);
		    				
		    			 }// end of for 
		    			 currentLine = reader.readLine(); // modification for while loop variable
		    		 }//end of while
		    		 
		    	     // Now storing the stats of file in the Hmap
		    	     
					 
	
			         Records ob = new Records(file.isDirectory(),file.getName().toLowerCase(),file.getAbsolutePath().toLowerCase(),lineCount,wordCount,charCount,Files.size(file.toPath()),file.toPath().getParent().toString(),TimeAttributeOfFile.getFileDateCreated(file.toPath()),file.lastModified());
					 
			         hMap.put(file.getAbsolutePath().toLowerCase(),ob);
			         
		    		
		    		 
	
	             }
			      catch(AccessDeniedException e)
		 		  {
			    	 logger.error("AccessDeniedException thrown at addEntryOfFileAtRuntime.",e);
		 		  }
			      catch(FileNotFoundException e)
		 		  {
			    	 logger.error("FileNotFoundException thrown at addEntryOfFileAtRuntime.",e);
		 		  }
			      catch(IOException e)
			  	  {
					   logger.error("IOException thrown at addEntryOfFileAtRuntime.",e);
			  	  }catch(OutOfMemoryError o)
			      {
			  		   logger.error("Out of memory error at startingindexing",o); 
			  		   logger.error("Indexing failed for file : "+file.getAbsolutePath());
			  		    
			      }catch(NullPointerException e)
			      { 
			    	 logger.error("Nullpointer exception thrown at addEntryOfFileAtRuntime.",e);
	
			      }
   	    }   		  

	}
	
	
	public String processingLine2(String currentLine)
	{
		StringBuilder word = new StringBuilder();
		char[] chars = currentLine.toCharArray();
		for(int i=0;i<chars.length;i++)
		{
					if(chars[i]=='.'||chars[i]=='>'||chars[i]==','||chars[i]=='<'||chars[i]=='/'||chars[i]=='?'||chars[i]=='\''
							||chars[i]=='"'||chars[i]==';'||chars[i]==':'||chars[i]=='{'||chars[i]=='}'||chars[i]=='['||chars[i]==']'
							||chars[i]=='`'||chars[i]=='~'||chars[i]=='!'||chars[i]=='@'||chars[i]=='#'||chars[i]=='$'
							||chars[i]=='%'||chars[i]=='^'||chars[i]=='&'||chars[i]=='*'||chars[i]=='('
							||chars[i]==')'||chars[i]=='-'||chars[i]=='_'||chars[i]=='='||chars[i]=='+')
						   continue;
					else
					{
						word.append(chars[i]);
					}
		}
		return word.toString();
	}
	
	public BigInteger addToindexMap(String word,String filePath,Map<String,ConcurrentHashMap<String, Frequency>> index,BigInteger totalTokens)
	 {
		 
		 ConcurrentHashMap<String,Frequency> idx = index.get(word);
		 if(idx==null)
		 {
			 idx = new ConcurrentHashMap<>();
			 Frequency f = new Frequency(1);
			 idx.put(filePath.toLowerCase(),f);
			 index.put(word,idx);
			 totalTokens = totalTokens.add(BigInteger.valueOf(1));
			 return totalTokens;
		 }
		 else
		 {
		    if(idx.containsKey(filePath.toLowerCase()))
		    {
		    	Frequency f =idx.get(filePath.toLowerCase());
		    	int cnt = f.getFreq();
		    	cnt++;
		    	f.setFreq(cnt);
		    	idx.put(filePath.toLowerCase(),f);
				totalTokens = totalTokens.add(BigInteger.valueOf(1));
				return totalTokens;
		    }
		    else
		    {
		    	idx.put(filePath.toLowerCase(),new Frequency(1));
				totalTokens = totalTokens.add(BigInteger.valueOf(1));
				return totalTokens;
		    }
		 } 
	 }
	
}
