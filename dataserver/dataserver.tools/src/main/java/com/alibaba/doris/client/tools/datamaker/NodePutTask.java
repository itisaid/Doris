/**
 * 
 */
package com.alibaba.doris.client.tools.datamaker;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskImpl;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * @author raymond
 *
 */
public class NodePutTask extends  ParralelTaskImpl {
	
	private Connection connection;
	 private VirtualRouter virtualRouter ;
    private String kp;
    private String vp;
    private String operation;
    
	public NodePutTask() {
	
	}
	
	public VirtualRouter getVirtualRouter() {
		return virtualRouter;
	}

	public void setVirtualRouter(VirtualRouter virtualRouter) {
		this.virtualRouter = virtualRouter;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public String getKp() {
		return kp;
	}

	public void setKp(String kp) {
		this.kp = kp;
	}

	public String getVp() {
		return vp;
	}

	public void setVp(String vp) {
		this.vp = vp;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getOperation() {
		return operation;
	}
	
	@Override
	public void doRun(long index) {
		   String key = kp;
		   int vnode = virtualRouter.findVirtualNode(key);

	        int commaIndex = key.indexOf(":");
	        
	        if( commaIndex == -1) {
	        	throw new IllegalArgumentException("Invalid kp parameter: " + kp + ", Format 101:abc");
	        }
	        String ns = key.substring(0, commaIndex);
	        String logicKey = key.substring(commaIndex + 1);
	        int namespaceId = Integer.valueOf(ns);
	        
	        logicKey = logicKey + index;
	        Key key1 = new KeyImpl(namespaceId, logicKey,   vnode );
	        
	        String value = vp + index;
	        Value value1 = new ValueImpl(value.getBytes(), System.currentTimeMillis());
	        
		   try {
		        if( "get".equals( operation ))  {
					
		        	connection.get(key1).get();
					
				}else if ("put".equals( operation)) {
					
					connection.put(key1, value1).get();
				 }else  if ("delete".equals( operation ))  {
					 connection.delete(key1).get();
				 }else {
					 //get
					 connection.get(key1).get();
				 }
		        
			} catch (Exception e) {
				e.printStackTrace();
			}
	}		
}
