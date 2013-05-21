/**
 * 
 */
package com.alibaba.doris.client.tools.datamaker;

import java.util.concurrent.CountDownLatch;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;
import com.alibaba.doris.client.tools.concurrent.ParralelExecutor;
import com.alibaba.doris.client.tools.concurrent.ParralelExecutorImpl;
import com.alibaba.doris.client.tools.concurrent.ParralelTask;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskFactory;

/**
 * @author raymond
 *
 */
public class ClientDataMakerTool  extends CommandLineHandler {
	
    private String config;
    private String ns;
    private String kp;
    private String vp;
    private int vl;
    private String o;
    private int s;
    private int e;
    private int c;
    private String p;   //performance anlyze and print result.

    public ClientDataMakerTool() {
        options.add(new Option("-config", "config", "Location of the doris-client.properties config file."));
        options.add(new Option("-ns", "Namespace", "Namespace name on AdminServer, e.g. ITBU_Product"));
        options.add(new Option("-kp", "Key prefix", "Key prefix, e.g. abc"));
        options.add(new Option("-vp", "Value prefix", "Key prefix "));
        options.add(new Option("-vl", "Value Len", "Value Len "));
        options.add(new Option("-o", "KV Operation", "get( default) , put, or delete", false, true));
        options.add(new Option("-s", "Key start suffix number", "Key start suffix number, e.g. abc0, abc1, ..."));
        options.add(new Option("-e", "Key end suffix number ", "Key end suffix number "));
        options.add(new Option("-c", "Concurrent", "number, Concurrent worker thread,default =1 ", false, true));
        options.add(new Option("-p", "Performance", "true/false, performance anlyze and print result. ", false, true));
    }
    
    @Override
    public void prepareParameters() {
    	config = commandLine.getValue("-config");
    	ns = commandLine.getValue("-ns");
    	kp = commandLine.getValue("-kp");
    	vp = commandLine.getValue("-vp");
    	vl = commandLine.getInt("-vl");
    	o = commandLine.getValue("-o");
    	s = commandLine.getInt("-s");
    	e = commandLine.getInt("-e");
    	c = commandLine.getInt("-c");
    	p = commandLine.getValue("-p");
    }
    
    
    @Override
    public void handleCommand() {
    	
    	DataStoreFactory dataStoreFactory = null;
    	try {
    		dataStoreFactory = new DataStoreFactoryImpl(config);
    	     final  DataStore dataStore = dataStoreFactory.getDataStore( ns );
    	        
    	    	boolean needProfiling = "true".equals(p);
    	    	
    	    	ParralelExecutor executor = new ParralelExecutorImpl( s ,e, c, needProfiling,  ClientPutTask.class );
    			
    	    	executor.setParralelTaskFactory( new ParralelTaskFactory() {
    	    			
    	    		@Override
    	    		public ParralelTask createTask(Class<? extends ParralelTask> parralelTaskClass, int i, long start, long end,
    	    				CountDownLatch countDownLatch,
    	    				CountDownLatch resultCountDownLatch) {
    	    			ClientPutTask clientPutTask =  (ClientPutTask) super.createTask( parralelTaskClass, i, start, end, countDownLatch, resultCountDownLatch);
    	    			
    	    			clientPutTask.setDataStore(dataStore);
    	    			clientPutTask.setKp(kp);
    	    			clientPutTask.setVp(vp);
    	    			clientPutTask.setVl(vl);
    	    			clientPutTask.setOperation(o);
    	    			return clientPutTask;
    	    		}
    	    	});
    	    	
    	    	executor.start();
    			
    			Object result;
    			try {
    				result = executor.getResult();
    				System.out.println("result: " + result );
    			} catch(Exception e) {
    			}
    			
    			if( needProfiling ) {
					executor.getPermMeter().printReport();
				}
    	}finally {
    		try {
    			dataStoreFactory.close();
    		}catch(Exception e) { }
    	}
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Command e.g : clientDataMaker -config doris-client.properties -ns StringName -kp abc -vp vvv -s 0 -e 1000 -c 10 -p true
		
		ClientDataMakerTool clientDataMakerTool = new ClientDataMakerTool();
		clientDataMakerTool.handle(args);
		System.exit( 0);
	}
}
