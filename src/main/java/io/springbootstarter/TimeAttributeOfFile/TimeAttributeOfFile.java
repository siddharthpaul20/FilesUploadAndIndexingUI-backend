package io.springbootstarter.TimeAttributeOfFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


public class TimeAttributeOfFile 
{
	static final Logger logger = Logger.getLogger(TimeAttributeOfFile.class);
	private TimeAttributeOfFile()
	{
		
	}
	
	public static String getFileDateCreated(Path path) 
	{
		try 
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			BasicFileAttributeView faView = Files.getFileAttributeView(path,
					BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
			BasicFileAttributes attributes = faView.readAttributes();
			FileTime fileTime = attributes.creationTime();
			return sdf.format(new Date(fileTime.toMillis()));
		} catch (IOException e)
		{
			return "";
		}
	}
	
	// below method is used in IndexingTheFolder offlineSync method
	public static long getFileDateCreatedInMillisLong(Path path) 
	{
		try 
		{
			BasicFileAttributeView faView = Files.getFileAttributeView(path,
					BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
			BasicFileAttributes attributes = faView.readAttributes();
			FileTime fileTime = attributes.creationTime();
			return fileTime.toMillis();
		} catch (IOException e)
		{
			return 0;
		}
	}
	
	
	
}
   
