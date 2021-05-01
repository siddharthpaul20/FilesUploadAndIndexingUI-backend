package io.springbootstarter.PoolExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PoolExecutor 
{
     private static PoolExecutor singleInstance = null;
     public ThreadPoolExecutor executor;
     
     private PoolExecutor()
     {
    	 this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
     }
     
     public static PoolExecutor getInstance()
     {
    	 if(singleInstance == null)
    		 singleInstance = new PoolExecutor();
    	 
    	 return singleInstance;
     }
}
