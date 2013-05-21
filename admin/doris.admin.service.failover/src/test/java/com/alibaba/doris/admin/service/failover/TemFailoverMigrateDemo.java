package com.alibaba.doris.admin.service.failover;

import org.junit.Test;

import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.failover.processor.TempFailoverProcessor;

public class TemFailoverMigrateDemo {

    /**
     * @param args
     * @throws AdminServiceException 
     */
    @Test
    public void test() throws AdminServiceException {
        NodeMork.morkNodes();

        TempFailoverProcessor.getInstance().failResolve("normal1.1");
        sleep(2);
        ReportActionMork.morkReport("temp0", "normal1.1", "20",null,null);
        sleep(2);
        ReportActionMork.morkReport("temp0", "normal1.1", "100",null,null);
        ReportActionMork.morkReport("temp1", "normal1.1", "2",null,null);
        ReportActionMork.morkReport("temp1", "normal1.1", "100",null,null);
        sleep(1);
        ReportActionMork.morkReport("temp2", "normal1.1", "10",null,null);
        sleep(6);
        ReportActionMork.morkReport("temp2", "normal1.1", "100",null,null);


    }

    private static void sleep(int t) {

        try {
            Thread.sleep(t * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
