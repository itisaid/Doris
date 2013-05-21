/**
 * 
 */
package com.alibaba.doris.client.tools.datamaker;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.tools.concurrent.ParralelExecutor;
import com.alibaba.doris.client.tools.concurrent.ParralelExecutorImpl;
import com.alibaba.doris.client.tools.concurrent.ParralelTask;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskFactory;
import com.alibaba.doris.client.tools.concurrent.PermMeter;
import com.alibaba.doris.common.route.MockVirtualRouter;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * @author raymond
 *
 */
public class NodeDataMakerTool  extends CommandLineHandler {
	
	private String ip;
    private int    port;
    private int    vn;
    private String kp;
    private String vp;
    private int vl;
    private String o;
    private int s;
    private int e;
    private int c;
    private String p;   //performance anlyze and print result.

    private Connection connection = null ;
    
    public NodeDataMakerTool() {
        options.add(new Option("-ip", "IP", "DataServer IP"));
        options.add(new Option("-port", "Port", "DataServer Port"));
        options.add(new Option("-vn", "VirtualNumber", "Virtual Number to routing."));
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
        ip = commandLine.getValue("-ip");
        port = commandLine.getInt("-port");
        vn = commandLine.getInt("-vn");
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
    	 try {
    		 final VirtualRouter virtualRouter = new MockVirtualRouter(vn);
    	        
    	        ConnectionFactory factory = ConnectionFactory.getInstance();
    	        InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
    	        final Connection connection0  = factory.getConnection(remoteAddress);
    	        connection0.open();
    	        
    	        connection = connection0;
    	        
    	    	boolean needProfiling = "true".equals(p);
    	    	
    	    	ParralelExecutor executor = new ParralelExecutorImpl( s ,e, c, needProfiling,  NodePutTask.class );
    			
    	    	executor.setParralelTaskFactory( new ParralelTaskFactory() {
    	    			
    	    		@Override
    	    		public ParralelTask createTask(Class<? extends ParralelTask> parralelTaskClass, int i, long start, long end,
    	    				CountDownLatch countDownLatch,
    	    				CountDownLatch resultCountDownLatch) {
    	    			NodePutTask nodePutTask =  (NodePutTask) super.createTask( parralelTaskClass, i, start, end, countDownLatch, resultCountDownLatch);
    	    			
    	    			nodePutTask.setConnection( connection0 );
    	    			nodePutTask.setVirtualRouter(virtualRouter);
    	    			nodePutTask.setKp(kp);
    	    			nodePutTask.setVp(vp);
    	    			nodePutTask.setOperation(o );
    	    			return nodePutTask;
    	    		}
    	    	});
    	    	
    	    	executor.start();
    			
    			Object result;
    			try {
    				result = executor.getResult();
    				System.out.println("result: " + result );
    			
    			} catch(Exception e) {
    			}
    			
    			if( "true".endsWith(p)) {
					executor.getPermMeter().printReport();
				}
    	 }finally {
    		 if( connection!=null) connection.close();
    	 }
        
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Command e.g : clientDataMaker -config doris-client.properties -ns StringName -kp abc -vp vvv -s 0 -e 1000 -c 10 -p true
		
		NodeDataMakerTool nodeDataMakerTool = new NodeDataMakerTool();
		nodeDataMakerTool.handle(args);
		System.exit( 0);
	}
}
