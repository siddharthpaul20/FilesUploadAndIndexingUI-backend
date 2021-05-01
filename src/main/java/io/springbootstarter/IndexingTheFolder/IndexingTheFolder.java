package io.springbootstarter.IndexingTheFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import io.springbootstarter.CountFilesGettingIndexed.CountFilesGettingIndexed;
import io.springbootstarter.DisplayRecords.DisplayRecords;
import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.FrontEndSentDataFormatClasses.SearchAKeywordInCompleteSentFormat;
import io.springbootstarter.IndexTheDirectory.IndexTheDirectory;
import io.springbootstarter.IndexingAFile.IndexingAFile;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.PoolExecutor.PoolExecutor;
import io.springbootstarter.Records.Records;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.Sresult.Sresult;
import io.springbootstarter.TimeAttributeOfFile.TimeAttributeOfFile;
import io.springbootstarter.TokenSentFormat.TokenSentFormat;
import io.springbootstarter.WebSocketController.WebSocketController;

public class IndexingTheFolder implements java.io.Serializable
{
    private String folderToIndex;
    private String folderCIndex;
    private String pathTxtFile;
    private String indexFileName;
    private Map<String, ConcurrentHashMap<String,Frequency>> index ;
    private ConcurrentMap<String,Records> hMap ;
    public ArrayList <String> stopwords;
	private BigInteger totalTokens;
	 
	static final Logger logger = Logger.getLogger(IndexingTheFolder.class);
	private static List<String> punctuation = new ArrayList<>(Arrays.asList(".", 
																            ",",
																            ";",
																            "\"",
																            "!",
																            "@",
																            "#",
																            "$",
																            "%",
																            "^",
																            "&",
																            "*",
																            "(",
																            ")",
																            "-",
																            "+",
																            "=",
																            "{",
																            "}",
																            ":",
																            "\\",
																            "|",
																            "?",
																            "<",
																            "~",
																            "`",
																            ">",
																            "[",
																            "]")); 
    
	public BigInteger getTotalTokens() {
		return totalTokens;
	}

	public void setTotalTokens(BigInteger totalTokens) {
		this.totalTokens = totalTokens;
	}
	
	public Map<String, ConcurrentHashMap<String, Frequency>> getIndex() {
		return index;
	}

	public void setIndex(Map<String, ConcurrentHashMap<String, Frequency>> index) {
		this.index = index;
	}
	
	public IndexingTheFolder(String folderToIndex,String folderCIndex,String pathTxtFile,String indexFileName)
    {
    	this.folderToIndex = folderToIndex.toLowerCase();
    	this.folderCIndex = folderCIndex.toLowerCase();
    	this.pathTxtFile = pathTxtFile.toLowerCase();
    	this.indexFileName = indexFileName.toLowerCase();
    	this.totalTokens = BigInteger.valueOf(0);
    	index = new ConcurrentHashMap<>();
		sethMap(new ConcurrentHashMap<>());
		stopwords = new ArrayList<>();
		
		if( this.pathTxtFile.endsWith(".txt"))
		{
			 try(BufferedReader reader = new BufferedReader(new FileReader(pathTxtFile));)
			 {
		    	String currentLine = reader.readLine();
		    	while (currentLine != null)
		    	{
		    		String[] words = currentLine.split(",");
		    		Collections.addAll(stopwords, words);
		    		currentLine = reader.readLine();
		    	}
			 }catch (IOException e) 
		     {
					 logger.error(MyUtilityClass.CONTEXT,e);
		     }
		}
		else
		{
			File f = new File(this.pathTxtFile);
			if(this.pathTxtFile.endsWith(".ser")&& f.exists())
			{
				HashSet<String> stopWordsHashSet = SerializingTheObject.deserializingStopWordsHashSet();
				stopwords.addAll(stopWordsHashSet);
			}
		}
    }
    
    public ConcurrentMap<String, Records> gethMap() {
		return hMap;
	}

	public void sethMap(ConcurrentMap<String, Records> hMap) {
		this.hMap = hMap;
	}

	public String getFolderToIndex() {
		return folderToIndex;
	}
	public void setFolderToIndex(String folderToIndex) {
		this.folderToIndex = folderToIndex;
	}
	public String getFolderCIndex() {
		return folderCIndex;
	}
	public void setFolderCIndex(String folderCIndex) {
		this.folderCIndex = folderCIndex;
	}
	public String getPathTxtFile() {
		return pathTxtFile;
	}
	public void setPathTxtFile(String pathTxtFile) {
		this.pathTxtFile = pathTxtFile;
	}
	public String getIndexFileName() {
		return indexFileName;
	}
	public void setIndexFileName(String indexFileName) {
		this.indexFileName = indexFileName;
	}
	
	public void addToHmap(String fPath,Records ob)
	{
		 // write logic here
		 gethMap().put(fPath.toLowerCase(),ob); //always convert fPath toLowerCase
	}
	
	public void addToindexMap(String word,String filePath)
	 {
		 
		 ConcurrentHashMap<String,Frequency> idx = index.get(word);
		 if(idx==null)
		 {
			 idx = new ConcurrentHashMap<>();
			 Frequency f = new Frequency(1);
			 idx.put(filePath.toLowerCase(),f);
			 index.put(word,idx);
			 synchronized (this)
			 {
				 totalTokens = totalTokens.add(BigInteger.valueOf(1));	 
			 }
			 
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
		    	synchronized (this)
				{
					 totalTokens = totalTokens.add(BigInteger.valueOf(1));	 
				}
		    }
		    else
		    {
		    	idx.put(filePath.toLowerCase(),new Frequency(1));
		    	synchronized (this)
				{
					 totalTokens = totalTokens.add(BigInteger.valueOf(1));	 
				}
		    }
		 }
		 
	 }

	public boolean containsHmap(Path fpath)   // this method is never used
	{
		 logger.info("Inside contains here - "+fpath.toFile().getAbsolutePath().toLowerCase()+"  */"+gethMap().containsKey(fpath.toFile().getAbsolutePath().toLowerCase()));
		 return gethMap().containsKey(fpath.toString().toLowerCase());
	}
	public void removeFromHmap(Path fPath)
	{
		 gethMap().remove(fPath.toString().toLowerCase());
	}
	public void removeFromindexMap(String filePath)
	{
		
         Iterator<Map.Entry<String, ConcurrentHashMap<String,Frequency>>> itr = index.entrySet().iterator(); 
        
	     while(itr.hasNext()) 
	     { 
	          Map.Entry<String, ConcurrentHashMap<String,Frequency>> entry = itr.next(); 
	          removeFromindexMap(entry.getKey(), filePath.toLowerCase());
	     } 
		 
	}
	public void removeFromindexMap(String word,String filePath)
	{
		  
		  ConcurrentHashMap <String,Frequency> idx = index.get(word);
		  if(idx!=null)
		  {
			  if(idx.get(filePath.toLowerCase())!=null)
			  {
				  synchronized (this)
					 {
					  totalTokens = totalTokens.subtract(BigInteger.valueOf(idx.get(filePath.toLowerCase()).getFreq()));
					 }  
			  }
			  
			  idx.remove(filePath.toLowerCase());
		  }
	}
	
	public void displayInnerHmap(String word, String filePath,int freq)
	{
		 String str= String.format("%20s %90s %30s%n",word,filePath,freq);
		 logger.info(str);
	}
	
	public void displayInnerHmap(String filePath,int freq)
	{
		 String str = String.format("%90s %20s%n",filePath,freq);
		 logger.info(str);
	}
	
	public void startShowingIndexMap(FolderIndexedList folderIndexedList)
	{
		String str = String.format("%20s %90s %30s%n","token","AbsolutePath","Frequency");
		logger.info(str);
	}
	
	public void showindexMap(FolderIndexedList fIndexedList)
	{
		  
		  // first showing tokens inside this directory
		  for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> entry : index.entrySet())  
		  {
			  for(Map.Entry<String, Frequency> ent: entry.getValue().entrySet())
			  {
				  displayInnerHmap(entry.getKey(),ent.getKey(),ent.getValue().getFreq());
			  }
		  }
		  // Now showing tokens inside its subdirectory
		  for(Map.Entry<String, Records> entry: hMap.entrySet())
		  {
			  if(new File(entry.getKey()).isDirectory())
			  {
				  IndexingTheFolder subDirectoryObject = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
				  subDirectoryObject.showindexMap(fIndexedList);
			  }
		  }
		  /*  Never do like this, Error comes
		   for (Records ob : gethMap().values())  
		  {
			  if(ob.getDirectoryType())
			  {
				  IndexingTheFolder subDirectoryObject=null; 
				  //load its object
				  try
				  {
					  subDirectoryObject = SerializingTheObject.deserializingIndexingTheFolderObject(ob.getfileName().toLowerCase(), fIndexedList.hRetrieve);
				  }catch(Exception e)
				  {
					  logger.error("Exception occured while loading subDirectory object",e);
					  continue;
				  }
				  if(subDirectoryObject!=null)
				  {
					  subDirectoryObject.showindexMap(fIndexedList);
				  }
				  
			  }
		  }*/ 
	}
	
	public void getAllTokens(FolderIndexedList fIndexedList,List<TokenSentFormat> list)
	{
		logger.info("hello");
		TokenSentFormat tokenDetail;
		for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> entry : index.entrySet())  
		{
			  for(Map.Entry<String, Frequency> ent: entry.getValue().entrySet())
			  {
				  tokenDetail = new TokenSentFormat(entry.getKey(), ent.getKey(), ent.getValue().getFreq());
				  list.add(tokenDetail);
			  }
		}
	    sendToCollectTokenFromAllSubdirectory(list,fIndexedList);
	}
	
	public void sendToCollectTokenFromAllSubdirectory(List<TokenSentFormat> list,FolderIndexedList fIndexedList)
	{
		TokenSentFormat tokenDetail;
		for(Map.Entry<String, Records> entry: hMap.entrySet())
		{
			if(new File(entry.getKey()).isDirectory())
			{
				IndexingTheFolder subDirectoryObject = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
				for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> temp : subDirectoryObject.index.entrySet())  
				{
					  for(Map.Entry<String, Frequency> ent: temp.getValue().entrySet())
					  {
						  tokenDetail = new TokenSentFormat(temp.getKey(), ent.getKey(), ent.getValue().getFreq());
						  list.add(tokenDetail);
					  }
				}
				subDirectoryObject.sendToCollectTokenFromAllSubdirectory(list, fIndexedList);
			}
		}
	}
	
	public void showTokenStatistics()
	{
		 logger.info("Total frequency of all tokens = "+totalTokens);
		 String str = String.format("%60s %20s %20s","Token","Token Frequency","Token Percentage");
		 String str1;
		 String cTokenCount="";
		 logger.info(str);
		 for (Map.Entry<String,ConcurrentHashMap<String, Frequency>> entry : index.entrySet())  
		 {
			  BigDecimal currentTokenCount = new BigDecimal("0");
			  BigDecimal tTokens = new BigDecimal(totalTokens);
			  for(Map.Entry<String, Frequency> ent: entry.getValue().entrySet())
			  {
				  currentTokenCount = currentTokenCount.add(BigDecimal.valueOf(ent.getValue().getFreq()));
			  }
			  try
			  {
				  cTokenCount = currentTokenCount.toString();
				  currentTokenCount = currentTokenCount.multiply(BigDecimal.valueOf(100));
				  currentTokenCount = currentTokenCount.divide(tTokens,10,RoundingMode.DOWN);				    
			  }catch(ArithmeticException e)
			  {
				  logger.error("Arithmetic Exception at showTokenStatistics.");
			  }
			  str1 = String.format("%60s %20s %20s",entry.getKey(),cTokenCount,currentTokenCount);
			  logger.info(str1);
		 }
	 }
	 
	 public void displayLinkedList(Sresult ob)
	 {
		 String str = String.format("%90s %20s%n",ob.getfilePath(),ob.getfreq());
		 logger.info(str);
	 }
	 public void displayLinkedList(String word,Sresult ob)
	 {
		 
		 String str= String.format("%20s %90s %30s%n",word,ob.getfilePath(),ob.getfreq());
		 logger.info(str);
		 
	 }
	 public void searchAKeyword() 
	 {
			
		        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		        String word;
		        String wrd;
		        logger.info("Enter the keyword to search for in the current indexed folder = ");
		        try {
					word = br.readLine();
				} catch (IOException e) {

					logger.error(MyUtilityClass.CONTEXT,e);
					logger.info("Not able to collect keyword.");
					word="#@#$#%#&#";
				}
				wrd = word.toLowerCase();
				if(index.containsKey(wrd))
				{
				    
					ConcurrentHashMap<String, Frequency> idx = index.get(wrd);
					logger.info("\n");
					logger.info("Search result found : "+word+" found in following files");
					String str = String.format("%n%90s %20s%n","AbsolutePath","Frequency");
					logger.info(str);
					for(Map.Entry< String,Frequency> entry : idx.entrySet())
				      {
				          displayInnerHmap(entry.getKey(), entry.getValue().getFreq());
				      }
					
				}
				else
				{
					logger.info("No such word exist in the folder.");
				}
				logger.info("\n");
			
	}
	 
	 public void searchAKeyword(String word)
	 {
		 if(index.containsKey(word))
		 {
			    ConcurrentHashMap<String, Frequency> idx = index.get(word);
				for(Map.Entry< String,Frequency> entry : idx.entrySet())
			      {
			          displayInnerHmap(entry.getKey(), entry.getValue().getFreq());
			      }
				 
		 }
	 }
	 public void searchAKeyword(String word,List<SearchAKeywordInCompleteSentFormat> list)
	 {
		 SearchAKeywordInCompleteSentFormat obc ;
		 if(index!=null)
			 if(index.containsKey(word))
			 {
				    logger.info("inside the search a keyword");
				    ConcurrentHashMap<String, Frequency> idx = index.get(word);
				    if(idx!=null)
				    {
				    	  logger.info("inside the final if condition");
						  for(Map.Entry< String,Frequency> entry : idx.entrySet())
					      {
							  obc = new SearchAKeywordInCompleteSentFormat(entry.getKey(),entry.getValue().getFreq());
					          logger.info(obc);
					          logger.info("adding in the list");
							  list.add(obc);
					      }
				    }	 
			 }
	 }

	
    
    
    
    public void publishStatsOfListFiles()
    {
   	 String str = String.format("%n%60s %30s %20s %20s %10s %10s %10s %10s %40s%n", "AbsolutePathOfFile", "Filename","DateCreated","LastModDate","Filesize","NoOfChars","NoOfWords","NoOfLines", "ParentFolder");
   	 logger.info(str);

   	 for (Records ob : gethMap().values())  
             DisplayRecords.displayRecord1(ob);
    }
    
    public void startIndexingAndRegisteringFiles(PoolExecutor exc,FolderIndexedList fIndexedList)
    {
   	 File fTIndex = new File(folderToIndex);
   	 if(fTIndex.exists() && fTIndex.isDirectory())
   	 {
   		 File[] arr = fTIndex.listFiles();
   		 if(arr!=null)
   		 {
   			 for(File file : arr)
       		 {
       				 if(file.isFile())
           			 {
           				 if(MyUtilityClass.isValidExtension(file.toString().toLowerCase()))
           				 {
           					 IndexingAFile fileThread = new IndexingAFile(this,file.toPath());
           					 exc.executor.execute(fileThread); // this file is indexed in multithreading environment
           				 }
           			 }
           			 else
           				 if(file.isDirectory())
           				 {
           					addEntryOfFolderAtRuntime(file.toPath());
           					SerializingTheObject.serializingIndexingTheFolderObject(this);
           					IndexTheDirectory.indexTheDirectoryGiven(file.getAbsolutePath().toLowerCase(), folderCIndex, pathTxtFile, fIndexedList, exc);
           					// checking if the folder is already indexed or not
//           					if(!MyUtilityClass.isAlreadyIndexed(file.getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve))
//           					{
//           						logger.info(file.getAbsolutePath().toLowerCase());
//           						// need to index folderPathToIndex
//           						// creating the fileName in which indexes of this folder will be stored
//           						String indexFileName = folderCIndex+"\\"+MyUtilityClass.getIndexFileName(file.getAbsolutePath().toLowerCase(), folderCIndex);
//           						IndexingTheFolder obj = new IndexingTheFolder(file.getAbsolutePath().toLowerCase(), folderCIndex,pathTxtFile,indexFileName);
//           						SerializingTheObject.serializingIndexingTheFolderObject(obj);
//           						// adding the folderPath to index in the fIndexedList.hRetrieve here
//           						RetrievalObject xyz = new RetrievalObject(folderCIndex,indexFileName,pathTxtFile);
//           						MyUtilityClass.addTohRetrieve(obj.getFolderToIndex(), xyz, fIndexedList); //folderToIndex comes in string lowercase format only
//           						// starting doing indexing of the folder
//           						logger.info("Creating indexes of --> "+file.getAbsolutePath().toLowerCase()+"  ,kindly wait for a moment...");
//           					   ThreadForIndexing indexingObj = new ThreadForIndexing(obj,exc,fIndexedList);
//           					   exc.executor.execute(indexingObj);
//           					}
//           					else
//           						logger.info("Already Indexed --> "+file.getAbsolutePath());
//           					 
           				 }
       			 
       		 }
   			 
   			 
   		 }
   		 
   	 
   	 }
    }
    
    
    //offline sync code starts
    
    public void offlineSync(String folderToIndex,FolderIndexedList fIndexedList)
    {
	   	 RetrievalObject obc = fIndexedList.hRetrieve.get(folderToIndex.toLowerCase());
	   	 if(obc!=null)
	   	 {
	   	     File fi = new File(obc.getfileName());
			 long lastModDtOfIndexedFile = fi.lastModified();
			 File indexedDirectory = new File(folderToIndex.toLowerCase());
			 long mainDirlstModDt = indexedDirectory.lastModified();
			 //logger.info(lastModDtOfIndexedFile+"  ------  "+mainDirlstModDt);
			 if(mainDirlstModDt>lastModDtOfIndexedFile)
			 {
				 // then do the processing
				 logger.info("mainDirlstModDt > lastModDtOfIndexedFile");
			     offlineSyncDirectories(indexedDirectory,lastModDtOfIndexedFile, fIndexedList);
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
				    			 logger.info("Made change of Valid Extensions here, and it is working.");
				    			 if(file.lastModified() > lastModDtOfIndexedFile)
				    			 {
				    				 // This file is either created new or modified
				    				 //logger.info(" going for synching file");
				    				 synchingFile(file);
				    				 
				    			 }   			  
				    			  
				    		 }
				    		  else if(file.isDirectory()&&file.lastModified() > lastModDtOfIndexedFile) 
				    	       { 
					    			   // This directory is either created new or modified
				    			       //logger.info("going for synching directory");
					    			   synchingDirectory(file); 
				    				   // after processing this directory
					    			   //offlineSyncDirectories(file, lastModDtOfIndexedFile);
					    			   offlineSync(file.getAbsolutePath().toLowerCase(), fIndexedList);
				    	       } 
				    	         
				    	  }
				    	
			    	  }catch(NullPointerException e)
			    	  {
			    		  logger.error("startingIndexingAndRegisteringFiles throwing nullpointer exception. check here.",e);
			    	  }
				 }
				 
			 } 
	   	 }
   	  
		 
    }
    
    public void offlineSyncDirectories(File fTIndex,long lastModDtOfIndexedFile, FolderIndexedList fIndexedList)
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
		    		  for (Map.Entry<String,Records> entry : gethMap().entrySet())  
		    		  {
		    			  File fil = new File(entry.getKey());
		    			  if(!fil.exists())
		    			  {
		    				  // this particular entry in Hmap is deleted, i.e,now it does not exists
		    				  if(fil.isDirectory())
		    				  {
		    					  removeFromHmap(fil.toPath());
		    					  //remove its entry from hRetrieve and delete its index file
		    					  IndexTheDirectory.removeTheDirectoryGiven(fil.getAbsolutePath().toLowerCase(), fIndexedList);
		    				  }
		    				  else
		    				  {
		    					  Path pth = fil.toPath();
		    					  removeFromindexMap(pth.toString().toLowerCase());
		    					  removeFromHmap(pth);
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
			    				 synchingFile(file);
			    				 
			    			 }   			  
			    			  
			    		 }
			    		  else if(file.isDirectory()&&file.lastModified() > lastModDtOfIndexedFile) 
			    	       {
			    			       //logger.info(file.toString());
			    			       //logger.info(file.lastModified() +" ---- "+lastModDtOfIndexedFile);
				    			   // This directory is either created new or modified
				    			   synchingDirectory(file); 
			    				   // after processing this directory
				    			   //offlineSyncDirectories(file, lastModDtOfIndexedFile);
				    			   
				    			   // check if this subdirectory is indexed or not
				    			   if(MyUtilityClass.isAlreadyIndexed(file.getAbsolutePath().toLowerCase(), fIndexedList.hRetrieve))
				    			       offlineSync(file.getAbsolutePath().toLowerCase(), fIndexedList);
				    			   else
				    			   {
				    				   //this directory is not already indexed, we need to index this directory
				    				   PoolExecutor exc = PoolExecutor.getInstance();
				    				   IndexTheDirectory.indexTheDirectoryGiven(file.getAbsolutePath().toLowerCase(), getFolderCIndex(), getPathTxtFile(),fIndexedList , exc);
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
    
    public void synchingFile(File f)
    {
   	    Path child = f.toPath();
	    removeFromindexMap(child.toString().toLowerCase());
        removeFromHmap(child);
        IndexingAFile fileThread = new IndexingAFile(this,child);
        PoolExecutor exc = PoolExecutor.getInstance();
        exc.executor.execute(fileThread);
        //addEntryOfFileAtRuntime(child);
    }
    
    public void synchingDirectory(File folder)
    {
   	    Path child = folder.toPath();
   	    removeFromHmap(child);
   	    addEntryOfFolderAtRuntime(child);
    }
    
    
    //offline sync code ends
    
    public String processingLine2(String currentLine)
	{
		StringBuilder word = new StringBuilder();
		char[] chars = currentLine.toCharArray();
		int temp;
		for(int i=0;i<chars.length;i++)
		{
					if(chars[i]=='.'||chars[i]=='>'||chars[i]==','||chars[i]=='<'||chars[i]=='/'||chars[i]=='?'||chars[i]=='\''
							||chars[i]=='"'||chars[i]==';'||chars[i]==':'||chars[i]=='{'||chars[i]=='}'||chars[i]=='['||chars[i]==']'
							||chars[i]=='`'||chars[i]=='~'||chars[i]=='!'||chars[i]=='@'||chars[i]=='#'||chars[i]=='$'
							||chars[i]=='%'||chars[i]=='^'||chars[i]=='&'||chars[i]=='*'||chars[i]=='('
							||chars[i]==')'||chars[i]=='-'||chars[i]=='_'||chars[i]=='='||chars[i]=='+')
						   word.append(' ');
					else
					{
						if(chars[i]==' ')
						{
							word.append(chars[i]);
							temp = i+1;
							while(temp<chars.length&&chars[temp]==' ')
							{
								temp++;
							}
							i = temp-1;
						}
						else
						  word.append(chars[i]);
					}
		}
		return word.toString();
	}
    
    public void addEntryOfFolderAtRuntime(Path path)
    {	
    	try 
    	{
    	   File file = new File(path.toFile().getAbsolutePath().toLowerCase()); // test here that lowercase is required or not
           Records ob = new Records(file.isDirectory(),file.getName().toLowerCase(),file.getAbsolutePath().toLowerCase(),0,0,0,0,file.toPath().getParent().toString().toLowerCase(),TimeAttributeOfFile.getFileDateCreated(file.toPath()),file.lastModified());
           addToHmap(file.getAbsolutePath().toLowerCase(),ob);
    	}catch(Exception e)
    	{
    		logger.error("Exception thrown at IndexingTheFolder addEntryOfFolderAtRuntime due to SimpleDAteFormat.",e);
    	}
		
    }
    
    
    public void addEntryOfFileAtRuntime(Path path)
    {
    	  if(Files.isReadable(path))
    	  {
    		  Instant start,finish;
              long timeElapsed;
	    	  File file= new File(path.toString());
	    	  // Now reading each file and creating index for it
			  //Initializing charCount, wordCount and lineCount to 0
			  long charCount = 0;
			  long wordCount = 0;
			  long lineCount = 0;
			  String currentLine;
			  String wrd;
			  String wrdCleaned;
			  logger.info("inside readable, indexing file : --> "+path.toString());
			  
			/*
			 * try( BufferedReader reader = new BufferedReader(new InputStreamReader( new
			 * FileInputStream(file), StandardCharsets.UTF_8)); )
			 */
			  
			  try(BufferedReader reader = new BufferedReader(new FileReader(file));)
				 {
		    		 currentLine = reader.readLine();
		    		 while (currentLine != null)
		    		 {
		    			 
		    			//Updating the lineCount
	                     lineCount++;
	                     //Updating the Charcount
	                     charCount = charCount + currentLine.length();
		    			 //String[] words = currentLine.split(" ");
	                     currentLine = processingLine2(currentLine);
		    			//Updating the wordCount
	                     String[] words = currentLine.split(" ");
	                     wordCount = wordCount + words.length;
	                     
	                     for(String word : words)
		    			 {
		    				
		    				if(stopwords.contains(word))
		    					continue;
		    				wrd =word.toLowerCase();
		    				// now indexing logic to be applied on wrd
		    				addToindexMap(wrd,file.getAbsolutePath().toLowerCase());
		    				
		    			 }// end of for 
		    			 currentLine = reader.readLine(); // modification for while loop variable
		    		 }//end of while
		    		 
		    	     // Now storing the stats of file in the Hmap
		    	     
					 
	
			         Records ob = new Records(file.isDirectory(),file.getName().toLowerCase(),file.getAbsolutePath().toLowerCase(),lineCount,wordCount,charCount,Files.size(file.toPath()),file.toPath().getParent().toString(),TimeAttributeOfFile.getFileDateCreated(file.toPath()),file.lastModified());
					 
			         addToHmap(file.getAbsolutePath().toLowerCase(),ob);
			         
		    		 //Serializing the file, it is successfully indexed
			         SerializingTheObject.serializingIndexingTheFolderObject(this);
	
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
			  		   logger.info("Indexing failed for file : "+file.getAbsolutePath());
			  		   logger.debug("hashmap size : "+index.size() );
			  		   logger.debug("Hmap size : "+hMap.size());
			  		   logger.info("hashmap size : "+index.size() );
			  		   logger.info("Hmap size : "+hMap.size());
			  		   removeFromindexMap(file.getAbsolutePath().toLowerCase());
			  		   
			  		   
			  		   // we need to write the logic for serializing the current hashmap and then process this file
			      }catch(NullPointerException e)
			      { 
			    	 logger.error("Nullpointer exception thrown at addEntryOfFileAtRuntime.",e);
	
			      }
			      
    	  }
     
    }
    
    public void addEntryOfFileAtRuntime2(Path path)
    {
    	if(Files.isReadable(path))
    	{
    		// Now reading each file and creating index for it
    	   	File file= new File(path.toString());
			//Initializing charCount, wordCount and lineCount to 0
			long charCount = 0;
			long wordCount = 0;
			long lineCount = 0;
			String currentLine;
			StringBuilder wrd;
			String wrdCleaned;
			logger.info("inside readable, indexing file : --> "+path.toString());
			try( BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(file), StandardCharsets.UTF_8)); )
			{
				currentLine = reader.readLine();
				while (currentLine != null)
				{
					currentLine = currentLine.toLowerCase();
					lineCount++;
					charCount = charCount + currentLine.length();
					StringTokenizer tokens = new StringTokenizer(currentLine);
					//logger.info(tokens.countTokens());
					wordCount = wordCount + tokens.countTokens();
					while( tokens.hasMoreTokens())
					{
						wrd= new StringBuilder();
						for(Character c : tokens.nextToken().toCharArray())
						{
							/*
							 * if(c=='.'||c=='>'||c==','||c=='<'||c=='/'||c=='?'||c=='\''
							 * ||c=='"'||c==';'||c==':'||c=='{'||c=='}'||c=='['||c==']'
							 * ||c=='`'||c=='~'||c=='!'||c=='@'||c=='#'||c=='$'
							 * ||c=='%'||c=='^'||c=='&'||c=='*'||c=='('
							 * ||c==')'||c=='-'||c=='_'||c=='='||c=='+') continue; else wrd.append(c);
							 */
							if(Character.isLetter(c))
								wrd.append(c);
						}
						wrdCleaned = wrd.toString();
						addToindexMap(wrdCleaned,file.getAbsolutePath().toLowerCase());
					}
					
					//upadating currentLine
					currentLine = reader.readLine();
				}
				// now remove the stopwords here at once
				for(String stopword : stopwords)
				{
				    if(index.containsKey(stopword.toLowerCase()))
				    {
				    	index.remove(stopword.toLowerCase());
				    }
				}
				
				index.remove("");
				
				Records ob = new Records(file.isDirectory(),file.getName().toLowerCase(),file.getAbsolutePath().toLowerCase(),lineCount,wordCount,charCount,Files.size(file.toPath()),file.toPath().getParent().toString(),TimeAttributeOfFile.getFileDateCreated(file.toPath()),file.lastModified());
				 
		        addToHmap(file.getAbsolutePath().toLowerCase(),ob);
		         
	    		//Serializing the file, it is successfully indexed
		        SerializingTheObject.serializingIndexingTheFolderObject(this);
				  
			}catch(AccessDeniedException e)
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
		  		   logger.info("Indexing failed for file : "+file.getAbsolutePath());
		  		   logger.debug("hashmap size : "+index.size() );
		  		   logger.debug("Hmap size : "+hMap.size());
		  		   logger.info("hashmap size : "+index.size() );
		  		   logger.info("Hmap size : "+hMap.size());
		  		   removeFromindexMap(file.getAbsolutePath().toLowerCase());
		  		   
		  		   
		  		   // we need to write the logic for serializing the current hashmap and then process this file
		      }catch(NullPointerException e)
		      { 
		    	 logger.error("Nullpointer exception thrown at addEntryOfFileAtRuntime.",e);

		      }
    	}
    }
    
    
	//********************************* End of class
}
