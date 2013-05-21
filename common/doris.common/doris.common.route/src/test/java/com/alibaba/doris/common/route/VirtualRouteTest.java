package com.alibaba.doris.common.route;

import junit.framework.TestCase;

/**
 * 
 * VirtualRouteTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-16
 */
public class VirtualRouteTest extends TestCase{

    public void testGet(){
    	VirtualRouter  virtualRouter = new MockVirtualRouter();
    	
    	int vNo = virtualRouter.findVirtualNode("a2");
        System.out.println("VNo of key 'a2':" + vNo);
    }
}
