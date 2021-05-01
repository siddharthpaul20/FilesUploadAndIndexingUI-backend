package io.springbootstarter.ThreadForIndexing;

import java.util.concurrent.ThreadPoolExecutor;

import io.springbootstarter.FolderIndexedList.FolderIndexedList;
import io.springbootstarter.IndexingTheFolder.IndexingTheFolder;
import io.springbootstarter.PoolExecutor.PoolExecutor;

public class ThreadForIndexing implements Runnable
{
	private IndexingTheFolder obj;
	private PoolExecutor exc;
	private FolderIndexedList fIndexedList;
	
	private ThreadForIndexing() {
		
	}
    public ThreadForIndexing(IndexingTheFolder obj,PoolExecutor exc,FolderIndexedList fIndexedList)
    {
    	this.obj = obj;
    	this.exc = exc;
    	this.fIndexedList = fIndexedList;
    }
    @Override
    public void run()
    {
    	obj.startIndexingAndRegisteringFiles(exc,fIndexedList);
    }
}
