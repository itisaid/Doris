package com.alibaba.doris.admin.service.common;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.VirtualNodeService;
import com.alibaba.doris.admin.service.common.virtual.VirtualNumberAction;

import junit.framework.Assert;
import junit.framework.TestCase;


public class VirtualNumberActionTest extends TestCase{
    AdminServiceAction virtualNumberAction;
    public void setUp() {
//        new ClassPathXmlApplicationContext("classpath:/spring/doris_service_context.xml");
//        virtualNumberAction= VirtualNumberAction.getInstance();
    }
    
    public void testAction(){
       //Assert.assertEquals("10000", virtualNumberAction.execute(null));
    }
}
