package io.springbootstarter.ControllerClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.FolderPathsInput.FolderPathsInput;
import io.springbootstarter.Frequency.Frequency;
import io.springbootstarter.FrontEndSentDataFormatClasses.ByteSentFormat;
import io.springbootstarter.FrontEndSentDataFormatClasses.FileFolderSearchKeywordInput;
import io.springbootstarter.FrontEndSentDataFormatClasses.FileFolderSearchSpecificInputs;
import io.springbootstarter.FrontEndSentDataFormatClasses.FileFolderSpecificInput;
import io.springbootstarter.FrontEndSentDataFormatClasses.KeywordInput;
import io.springbootstarter.FrontEndSentDataFormatClasses.SearchAKeywordInCompleteSentFormat;
import io.springbootstarter.FrontEndSentDataFormatClasses.ShowAllIndexedDirectoryFormat;
import io.springbootstarter.FrontEndSentDataFormatClasses.TokenStatisticsSentFormat;
import io.springbootstarter.FrontEndSentDataFormatClasses.WordCloudSpecificSentFormat;
import io.springbootstarter.GetFileStatsSpecific.GetFileStatsSpecific;
import io.springbootstarter.GetTokensStatisticsSpecific.GetTokensStatisticsSpecific;
import io.springbootstarter.IndexTheDirectory.IndexTheDirectory;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.MyUtilityClass.MyUtilityClass;
import io.springbootstarter.PoolExecutor.PoolExecutor;
import io.springbootstarter.Records.Records;
import io.springbootstarter.Response.Response;
import io.springbootstarter.RetrievalObject.RetrievalObject;
import io.springbootstarter.SearchAFileFolderInSpecific.SearchAFileFolderInSpecific;
import io.springbootstarter.SearchAFileFolderInWhole.SearchAFileFolderInWhole;
import io.springbootstarter.SearchAKeywordInComplete.SearchAKeywordInComplete;
import io.springbootstarter.SearchByFileSize.SearchByFileSize;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.ThreadForIndexing.ThreadForIndexing;
import io.springbootstarter.TokenSentFormat.TokenSentFormat;
import io.springbootstarter.WebSocketController.WebSocketController;
import io.springbootstarter.WordCloudSpecific.WordCloudSpecific;
import io.springbootstarter.getTokensSpecificEfficient.GetTokensSpecificEfficient;


@RestController
@CrossOrigin(origins = "*")
public class ControllerClass 
{
	static final Logger logger = Logger.getLogger(ControllerClass.class);
	
	@RequestMapping(method = RequestMethod.POST,value = "/input")
	public void folderPathsInput(@RequestBody FolderPathsInput inp) 
	{
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		logger.info(inp.getFolderToIndex());
		logger.info(inp.getFolderCIndex());
		logger.info(inp.getPathTxtFile());
		PoolExecutor exc = PoolExecutor.getInstance();
			
		IndexTheDirectory.indexTheDirectoryGiven(inp.getFolderToIndex(), inp.getFolderCIndex(), inp.getPathTxtFile(), fIndexedList, exc);
			
	}
	

	@RequestMapping(method = RequestMethod.POST,value = "/getTokens")
	public List<TokenSentFormat> getTokens(@RequestBody TokenInputDirectory input)
	{
		logger.info("hi");
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<TokenSentFormat> list = new ArrayList<>();
		// check if the folerPathToIndex is indexed already or not
		if(MyUtilityClass.isAlreadyIndexed(input.getFolderToIndex(), fIndexedList.hRetrieve))
		{
			logger.info(input.getFolderToIndex());
			IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(input.getFolderToIndex(), fIndexedList.hRetrieve);
			obj.getAllTokens(fIndexedList,list);
		}
	    logger.info("bye");
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/searchAKeywordInComplete")
	public List<SearchAKeywordInCompleteSentFormat> getResultOfSearchAKeywordInComplete(@RequestBody KeywordInput input)
	{
		logger.info("hi in Keyword search---->"+input.getKeywordToSearch());
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<SearchAKeywordInCompleteSentFormat> list = new ArrayList<>();
		SearchAKeywordInComplete.searchAKeywordInWhole(input.getKeywordToSearch().toLowerCase(), fIndexedList,list);
		logger.info("bye");
		return list;
	}
	
	@RequestMapping("/getAllIndexedDirectories")
	public ShowAllIndexedDirectoryFormat[] getAllIndexedDirectories()
	{
		logger.info("hi in getAllIndexedDirectories---->");
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<ShowAllIndexedDirectoryFormat> list = new ArrayList<>();
		ShowAllIndexedDirectoryFormat dir;
		for(Map.Entry<String, RetrievalObject> entry: fIndexedList.hRetrieve.entrySet())
		{
			dir = new ShowAllIndexedDirectoryFormat(entry.getKey());
			list.add(dir);
		}
		ShowAllIndexedDirectoryFormat[] rec = list.toArray(new ShowAllIndexedDirectoryFormat[0]);
		Arrays.sort(rec, new Comparator<ShowAllIndexedDirectoryFormat>() {
		    @Override
		    public int compare(ShowAllIndexedDirectoryFormat emp1, ShowAllIndexedDirectoryFormat emp2) {
		        return emp1.getDirectory().compareTo(emp2.getDirectory());
		    }
		});
		return rec;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/searchFileFolderInComplete")
	public List<Records> getResultOfSearchFileFolderInComplete(@RequestBody FileFolderSearchKeywordInput input)
	{
		logger.info("hi in search a file folder in complete ----> "+input.getFileFolderToSearch());
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<Records> list = new ArrayList<>();
		SearchAFileFolderInWhole.startSearchAFileFolderInWhole(input.getFileFolderToSearch().toLowerCase(), fIndexedList, list);
		logger.info("bye");
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/searchFileFolderSpecific")
	public List<Records> getResultOfSearchFileFolderSpecific(@RequestBody FileFolderSearchSpecificInputs input)
	{
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<Records> list = new ArrayList<>();
		logger.info("hi in search a file folder in specific ----> "+input.getFileFolderToSearch()+"  "+input.getFolderCarryingSearch());
		SearchAFileFolderInSpecific.startSearchAFileFolderInSpecific(input.getFolderCarryingSearch().toLowerCase(),input.getFileFolderToSearch().toLowerCase(), fIndexedList, list);
		logger.info("bye");
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getFileStats")
	public List<Records> getFileStatsSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getFileStatsSpecific --> "+input.getIndexFolderName());
		List <Records> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
        GetFileStatsSpecific.getFileStats(input.getIndexFolderName(), list, fIndexedList);
        logger.info("bye");
        return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getTokensSpecific")
	public List<TokenSentFormat> getTokensSpecificEfficientMethod(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getTokensSpecificEfficientMethod --->  "+input.getIndexFolderName());
		List <TokenSentFormat> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		GetTokensSpecificEfficient.getTokensSpecificEfficientMethod(input.getIndexFolderName(), list, fIndexedList);
		logger.info("bye");
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getSortByFileNameSpecific")
	public Records[] getSortByFileNameSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getSortByFileNameSpecific --> "+input.getIndexFolderName());
		List <Records> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
        GetFileStatsSpecific.getFileStats(input.getIndexFolderName(), list, fIndexedList);
		Records[] rec = list.toArray(new Records[0]);
		Arrays.sort(rec, new Comparator<Records>() {
		    @Override
		    public int compare(Records emp1, Records emp2) {
		        return emp1.getfileName().compareTo(emp2.getfileName());
		    }
		});
		logger.info("bye");
		return rec;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getSortByFileSizeSpecific")
	public Records[] getSortByFileSizeSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getSortByFileSizeSpecific --> "+input.getIndexFolderName());
		List <Records> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
        GetFileStatsSpecific.getFileStats(input.getIndexFolderName(), list, fIndexedList);
		Records[] rec = list.toArray(new Records[0]);
		Arrays.sort(rec, Comparator.comparing(Records::getnoOfBits));
		logger.info("bye");
		return rec;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getSortByCreationDateSpecific")
	public Records[] getSortByCreationDateSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getSortByCreationDateSpecific --> "+input.getIndexFolderName());
		List <Records> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
        GetFileStatsSpecific.getFileStats(input.getIndexFolderName(), list, fIndexedList);
		Records[] rec = list.toArray(new Records[0]);
		Arrays.sort(rec, Comparator.comparing(t -> {
			try {
				return t.getCreationDate();
			} catch (ParseException e) {

				e.printStackTrace();
			}
			return null;
		}));
		logger.info("bye");
		return rec;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getSortByFileLastModifiedTimeSpecific")
	public Records[] getSortByFileLastModifiedTimeSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getSortByFileLastModifiedTimeSpecific --> "+input.getIndexFolderName());
		List <Records> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
        GetFileStatsSpecific.getFileStats(input.getIndexFolderName(), list, fIndexedList);
		Records[] rec = list.toArray(new Records[0]);
		Arrays.sort(rec, Comparator.comparing(t -> {
			return t.getLastModifiedDate();
		}));
		logger.info("bye");
		return rec;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getTokensStatisticsSpecific")
	public List<TokenStatisticsSentFormat> getTokensStatisticsSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getTokensStatisticsSpecific --> "+input.getIndexFolderName());
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<TokenStatisticsSentFormat> list = new ArrayList<>();
		GetTokensStatisticsSpecific.getTokensStatisticsSpecific(input.getIndexFolderName(), list, fIndexedList);
	    logger.info("bye");
	    logger.info("bye2");
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/getWordCloudSpecific")
	public List<WordCloudSpecificSentFormat> getWordCloudSpecific(@RequestBody FileFolderSpecificInput input)
	{
		logger.info("inside getWordCloudSpecific --> "+input.getIndexFolderName());
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		List<WordCloudSpecificSentFormat> list = new ArrayList<>();
		WordCloudSpecific.getWordCloudSpecific(input.getIndexFolderName(), list, fIndexedList);
	    logger.info("bye");
		return list;
	}
	
	@GetMapping("/getAllTokensWhole")
	public List<TokenSentFormat> getAllTokensWhole()
	{
		TokenSentFormat tokenDetail;
		List <TokenSentFormat> list = new ArrayList();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		for(Map.Entry<String,RetrievalObject> entry : fIndexedList.hRetrieve.entrySet())
		{
			try 
 			{
				IndexingTheFolder obj = SerializingTheObject.deserializingIndexingTheFolderObject(entry.getKey(), fIndexedList.hRetrieve);
 			      if(obj != null)
 			      {
 			    	 logger.info("Inside getAllTokensWhole ---> "+obj.getFolderToIndex());
 			    	 for(Map.Entry<String,ConcurrentHashMap<String, Frequency>> objectIndexMap : obj.getIndex().entrySet())
 			    	 {
 			    		for(Map.Entry<String, Frequency> innerMapReference: objectIndexMap.getValue().entrySet())
 			    		{
 			    			tokenDetail = new TokenSentFormat(objectIndexMap.getKey(), innerMapReference.getKey(), innerMapReference.getValue().getFreq());
 			    			list.add(tokenDetail);
 			    		}
 			    	 }
 			      }
 			} catch(OutOfMemoryError o)
			{
 				logger.info("out of memory error thrown in controller class getAllTokensWhole");
 				logger.error(o);
		    }catch(NullPointerException e)
	  		{
	  			logger.error("NullPointerException thrown at SearchAKeywordInComplete --> ");
	  		}
		}
		logger.info("bye from getAllTokensWhole");
		return list;
	}
	
	@PostMapping(path = "/getFileContent",consumes = "multipart/form-data")
    public List<ByteSentFormat> getFileContent(@RequestParam("file") String fileName,@RequestParam("pointerFrom") String pointerFrom)
    {
		//WebSocketController.template.convertAndSend("/topic/server-broadcaster", "");
		ByteSentFormat character;
		logger.info("inside getFileContent ---> "+ fileName+" ******* ===> "+pointerFrom);
		int readFrom= Integer.parseInt(pointerFrom);
		byte[] bytes = null;
		try(RandomAccessFile file = new RandomAccessFile(fileName, "r");)
		{
			file.seek(readFrom);  
	        bytes = new byte[2048];  
	        if(file.read(bytes)==-1)
	        {
	        	//end of file is reached
	        	logger.info("end of file is reached, returning -1 ");
	            bytes = null;
	        }
	        else
	        {
	        	logger.info("returning  half bytes --> "+bytes);
	        }
		} catch (FileNotFoundException e) {
			logger.error(MyUtilityClass.CONTEXT,e);
		} catch (IOException e) {
			logger.error(MyUtilityClass.CONTEXT,e);
		} 
        
		if(bytes == null)
			return null;
		String actual = new String(bytes, StandardCharsets.UTF_8);
		//logger.info("message ---> "+actual);
	    List <ByteSentFormat> list = new ArrayList<>();
	    character = new ByteSentFormat(actual.trim());
	    //logger.info(" worked till here"+character);
	    list.add(character);
	 
	    	
		return list;
    }
	
	@GetMapping("/getAllDirectoriesOfDdrive")
	public List<ShowAllIndexedDirectoryFormat> getAllDirectoriesOfDdrive()
	{
		List <ShowAllIndexedDirectoryFormat> list = new ArrayList<ShowAllIndexedDirectoryFormat>();
		ShowAllIndexedDirectoryFormat directory;
		File f = new File("d:\\");
		File[] arr = f.listFiles();
		logger.info("hi in getAllDirectoriesOfDdrive");
		if(arr != null)
		{
			for( File file : arr)
			{	
				logger.info(file.getAbsolutePath());
				if(file.isDirectory())
				{
					//file is directory
					if(file.getAbsolutePath().toLowerCase().equalsIgnoreCase("d:\\$RECYCLE.BIN")|| file.getAbsolutePath().toLowerCase().equalsIgnoreCase("d:\\System Volume Information"))
						continue;
					directory = new ShowAllIndexedDirectoryFormat(file.getAbsolutePath().toLowerCase());
					list.add(directory);
				}
			}
		}
		
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "/serverFolderIndexInput")
	public void serverFolderPathsInput(@RequestBody FolderPathsInput inp) 
	{
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		logger.info(inp.getFolderToIndex());
		logger.info(inp.getFolderCIndex());
		logger.info(inp.getPathTxtFile());
		int positionOfFirstSpace = 0;
		
		positionOfFirstSpace = inp.getFolderToIndex().indexOf(' ');
		String folderToIndex = inp.getFolderToIndex().substring(positionOfFirstSpace+1);
		
		positionOfFirstSpace = inp.getFolderCIndex().indexOf(' ');
		String folderCIndex = inp.getFolderCIndex().substring(positionOfFirstSpace+1);
		
		positionOfFirstSpace = inp.getPathTxtFile().indexOf(' ');
		String pathTxtFile = inp.getPathTxtFile().substring(positionOfFirstSpace+1);
		
		logger.info("folderToIndex --> "+folderToIndex);
		logger.info("folderCIndex --> "+folderCIndex);
		logger.info("pathTxtFile --> "+pathTxtFile);
		PoolExecutor exc = PoolExecutor.getInstance();
		
		
		IndexTheDirectory.indexTheDirectoryGiven(folderToIndex, folderCIndex, pathTxtFile, fIndexedList, exc);
		
	}
	
	@PostMapping(path = "/searchAKeywordSpecific",consumes = "multipart/form-data")
    public List<SearchAKeywordInCompleteSentFormat> getKeywordInSpecificSearchResult(@RequestParam("folderCarryingSearch") String folderCarryingSearch,@RequestParam("keywordToSearch") String keywordToSearch)
    {
		List<SearchAKeywordInCompleteSentFormat> list = new ArrayList<>();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		SearchAKeywordInComplete.searchAKeywordInSpecific(keywordToSearch.toLowerCase(), folderCarryingSearch.toLowerCase(), fIndexedList, list);
		
		return list;
    }
	
	@PostMapping(path = "/searchByFileSizeFolderSpecific",consumes = "multipart/form-data")
    public List<Records> getSearchByFileSizeFolderSpecificResult(@RequestParam("folderCarryingSearch") String folderCarryingSearch,@RequestParam("type") String type, @RequestParam("limitInput") String limitInput)
    {
		logger.info("getSearchByFileSizeFolderSpecificResult ---> "+ folderCarryingSearch+" ******* ===> "+limitInput+" ****** ===> "+type);
		int limitSizeInput= Integer.parseInt(limitInput);
		logger.info("limitInput --> "+ limitInput);
		List <Records> list = new ArrayList<>();
		FolderIndexedList fIndexedList = FolderIndexedList.getInstance();
		if(type.equalsIgnoreCase("less"))
		{
			// write logic here
			SearchByFileSize.searchByFileSizeLessSpecific(limitSizeInput, folderCarryingSearch, fIndexedList, list);
			
		}
		
		if(type.equalsIgnoreCase("more"))
		{
			// write logic here
			SearchByFileSize.searchByFileSizeMoreSpecific(limitSizeInput, folderCarryingSearch, fIndexedList, list);
		}
		
		return list;
    }
}
