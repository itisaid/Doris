package com.alibaba.doris.admin.service.failover;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.failover.processor.ForeverFailoverProcessor;

public class ForeverFailoverMigrateDemo extends TestCase {

    public void setUp() {
        //new ClassPathXmlApplicationContext("classpath:/spring/doris_service_context.xml");
    }

    /**
     * @param args
     * @throws AdminServiceException
     */
    @Test
    public void test() throws AdminServiceException {
        NodeMork.morkNodes();

        ForeverFailoverProcessor.getInstance().failResolve("normal1.1");
        sleep(2);
        ReportActionMork.morkReport("normal2.1", "standby0", "20", null, null);
        sleep(2);
        ReportActionMork.morkReport("normal2.1", "standby0", "40", null, null);
        ReportActionMork.morkReport("normal2.1", "standby0", "56", null, null);
        ReportActionMork.morkReport("normal2.1", "standby0", "78", null, null);
        sleep(1);
        ReportActionMork.morkReport("normal2.1", "standby0", "89", null, null);
        sleep(6);
        ReportActionMork.morkReport("normal2.1", "standby0", "100", null, null);
    }

    private void sleep(int t) {

        try {
            Thread.sleep(t * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
