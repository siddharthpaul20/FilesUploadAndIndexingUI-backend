package io.springbootstarter.SearchingCase;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import io.springbootstarter.DisplayRecords.DisplayRecords;
import io.springbootstarter.Records.Records;

 

public class SearchingCase

{

	 static final Logger logger = Logger.getLogger(SearchingCase.class);
	 static final String str = String.format("%n%60s %30s %20s %20s %10s %10s %10s %10s %40s%n", "AbsolutePathOfFile", "Filename","DateCreated","LastModDate","Filesize","NoOfChars","NoOfWords","NoOfLines", "ParentFolder");

	 private SearchingCase() 
	 {
		 
	 }
     public static void menu1(ConcurrentMap<String, Records> concurrentMap) throws IOException, ParseException
     {

      Scanner sc = new Scanner(System.in);
      int flagForExit =0;

      logger.info("Select any one of the option from below :"

                           + "\n1) Search by FileName."

                           + "\n2) Search by Size less than."

                           + "\n3) Search by Size more than."

                           + "\n4) Search by Number of words less than."

                           + "\n5) Search by Number of words more than."

                           + "\n6) Search by Number of lines less than."

                           + "\n7) Search by Number of lines more than."

                           + "\n8) Search by Created Date before."

                           + "\n9) Search by Created Date After."
                           
                           + "\n10)Back to previous menu.");

               

               

               int ch = sc.nextInt();

                switch(ch)

                {

                case 1:

                     logger.info("Enter the Filename = ");

                     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                     String fileNameInput = br.readLine();

                     searchingByFileName(fileNameInput,concurrentMap);

                     // write logic here

                    

                     break;

                    

                case 2:

                     logger.info("Enter the size of file in bits = ");

                    long fileSize = sc.nextLong();

                    searchingByFileSizeLess(fileSize,concurrentMap);


                     // write logic here

                    

                     break;

                    

                case 3:

                     logger.info("Enter the size of file in bits = ");

                    fileSize = sc.nextLong();

                    searchingByFileSizeMore(fileSize,concurrentMap);                    

                     // write logic here

                    

                     break;

                case 4:

                     logger.info("Enter the number of words in the file = ");

                     long fileNoOfWords = sc.nextLong();

                     searchingBynoOfWordsLess2(fileNoOfWords,concurrentMap);

                     // write logic here

                    

                     break;

                case 5:

                     logger.info("Enter the number of words in the file = ");

                     fileNoOfWords = sc.nextLong();

                     searchingBynoOfWordsMore(fileNoOfWords,concurrentMap);

                     // write logic here

                    

                     break;

                case 6:

                     logger.info("Enter the number of Lines in the file = ");

                     long fileNoOfLines = sc.nextLong();

                     searchingBynoOfLinesLess(fileNoOfLines,concurrentMap);

                     // write logic here

                    

                     break;

                case 7:

                     logger.info("Enter the number of Lines in the file = ");

                     fileNoOfLines = sc.nextLong();

                     searchingBynoOfLinesMore(fileNoOfLines,concurrentMap);

                     // write logic here

                    

                     break;

                case 8:

                     BufferedReader brrr = new BufferedReader(new InputStreamReader(System.in));

                     logger.info("Enter the Date when file is created in \"dd-MMM-yyyy\" format = ");

                     String fileDateCreated = brrr.readLine();

                     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                     Date dateObj = simpleDateFormat.parse( fileDateCreated ); 

                    searchingByCreatedDateBefore(dateObj,concurrentMap);

                     // write logic here

                    

                     break;

                case 9:

                     BufferedReader brr = new BufferedReader(new InputStreamReader(System.in));

                     logger.info("Enter the Date when file is created in \"dd-MMM-yyyy\" format = ");

                    fileDateCreated = brr.readLine();

                     simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                     dateObj = simpleDateFormat.parse( fileDateCreated ); 

                     searchingByCreatedDateAfter(dateObj,concurrentMap);

                     // write logic here

                    

                     break;

                case 10:
                	break;
                default:

                     logger.info("Invalid request made by user!!!");

                     SearchingCase.menu1(concurrentMap);

                     break;

                }// end of inner switch

     

     }
     
     public static void searchingByFileName(String fileNameInput,ConcurrentMap<String, Records> concurrentMap)
     {
	      logger.info(str);

	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	      {
	          if(entry.getValue().absolutePath.contains(fileNameInput))
	
	          {
	
	               DisplayRecords.displayRecord1(entry.getValue());
	
	          }
	      }
     }

  
     public static void searchingByFileName(String fileNameInput,ConcurrentMap<String, Records> concurrentMap,List<Records> list)
     {
          
	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	      {
	          if(entry.getValue().getfileName().contains(fileNameInput))
	          {
	        	   logger.info(entry.getValue().fileName+"  "+entry.getValue().absolutePath);
	               list.add(entry.getValue());
	          }
	      }
     }
     
     
     public static void searchingByFileSizeMore(long fileSize,ConcurrentMap<String, Records> concurrentMap)
     {
	      logger.info(str);
	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	      {
	    	if(entry.getValue().size >= fileSize)
		    {
		    DisplayRecords.displayRecord1(entry.getValue());
		    }
	      }
     }
     public static void searchingByFileSizeLess(long fileSize,ConcurrentMap<String, Records> concurrentMap)
     {
	      logger.info(str);
	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	      {
	    	if(entry.getValue().size <= fileSize)
		    {
		    DisplayRecords.displayRecord1(entry.getValue());
		    }
	      }
     }
     
     public static void searchingBynoOfWordsLess2(long fileNoOfWords,ConcurrentMap<String, Records> concurrentMap)
     {
	     logger.info(str);
	     for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	     {
		    if(entry.getValue().noOfWords <= fileNoOfWords)
		    {
		    DisplayRecords.displayRecord1(entry.getValue());
		    }
	     }
     }

     public static void searchingBynoOfWordsMore(long fileNoOfWords,ConcurrentMap<String, Records> concurrentMap)
     {
	     logger.info(str);
	     for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	     {
		    if(entry.getValue().noOfWords >= fileNoOfWords)
		    {
		    DisplayRecords.displayRecord1(entry.getValue());
		    }
	     }     }
     
     public static void searchingBynoOfLinesLess(long fileNoOfLines, ConcurrentMap<String, Records> concurrentMap)
     {
	      logger.info(str);
	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
		  {
			    if(entry.getValue().noOfLines <= fileNoOfLines)
			    {
			    DisplayRecords.displayRecord1(entry.getValue());
			    }
		  }
     }

     public static void searchingBynoOfLinesMore(long fileNoOfLines, ConcurrentMap<String, Records> concurrentMap)
     {
	      logger.info(str);
	      for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
		     {
			    if(entry.getValue().noOfLines >= fileNoOfLines)
			    {
			    DisplayRecords.displayRecord1(entry.getValue());
			    }
		     }
		    
     }

    public static void searchingByCreatedDateBefore(Date dateObj,ConcurrentMap<String, Records> concurrentMap) throws ParseException
    {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	    logger.info(str);
	    for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	     {
	    	Date dateObj2 = simpleDateFormat.parse(entry.getValue().creationDate );
	    	if(dateObj2.compareTo(dateObj) <=0)
            {
            DisplayRecords.displayRecord2(entry.getValue());
            }
	     }
    }

    public static void searchingByCreatedDateAfter(Date dateObj,ConcurrentMap<String, Records> concurrentMap) throws ParseException
    {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	    logger.info(str);

	    for(Map.Entry< String,Records> entry : concurrentMap.entrySet())
	     {
	    	Date dateObj2 = simpleDateFormat.parse(entry.getValue().creationDate );
	    	if(dateObj2.compareTo(dateObj) >=0)
            {
            DisplayRecords.displayRecord1(entry.getValue());
            }
	     }
    }
     

}