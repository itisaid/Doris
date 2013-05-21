package com.alibaba.doris.admin.service.expansion;

import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.expansion.processor.ExpansionMigrateProcessor;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

public class ExpansionDemo {

	public static void main(String[] args) {
		NodeMork.morkNodes();

        try {
            ExpansionMigrateProcessor.getInstance().migerate(NodeMork.getNewPhysicalIdList(),
                                                             StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
           sleep(1);
           ReportActionMork.morkReport("normal1.0", null, "20",null,null);
           ReportActionMork.morkReport("normal1.1", null, "20",null,null);
           ReportActionMork.morkReport("normal1.2", null, "20",null,null);
           ReportActionMork.morkReport("normal1.3", null, "20",null,null);
           ReportActionMork.morkReport("normal1.4", null, "20",null,null);
           sleep(2);
           ReportActionMork.morkReport("normal1.0", null, "50",null,null);
           ReportActionMork.morkReport("normal1.1", null, "50",null,null);
           ReportActionMork.morkReport("normal1.2", null, "50",null,null);
           ReportActionMork.morkReport("normal1.3", null, "50",null,null);
           ReportActionMork.morkReport("normal1.4", null, "50",null,null);
           sleep(1);
           ReportActionMork.morkReport("normal1.0", null, "100",null,null);
           ReportActionMork.morkReport("normal1.1", null, "100",null,null);
           ReportActionMork.morkReport("normal1.2", null, "100",null,null);
           sleep(1);
           ReportActionMork.morkReport("normal1.3", null, "100",null,null);
           ReportActionMork.morkReport("normal1.4", null, "100",null,null);
        } catch (AdminServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		ReportActionMork.morkReport("normal2.0", null, "100",null,null);
        ReportActionMork.morkReport("normal2.1", null, "100",null,null);
        ReportActionMork.morkReport("normal2.2", null, "100",null,null);
        
        ReportActionMork.morkReport("normal2.3", null, "100",null,null);
        ReportActionMork.morkReport("normal2.4", null, "100",null,null);

        sleep(1);
        ReportActionMork.morkReport("normal1.0", null, "100",null,null);
        ReportActionMork.morkReport("normal1.1", null, "100",null,null);
        ReportActionMork.morkReport("normal1.2", null, "100",null,null);
        sleep(1);
/*        ReportActionMork.morkReport("normal2.0", null, "100");
        ReportActionMork.morkReport("normal2.1", null, "100");
        ReportActionMork.morkReport("normal2.2", null, "100");*/
//        ReportActionMork.morkReport("normal2.3", null, "100");
//        sleep(1);
//        ReportActionMork.morkReport("normal2.4", null, "100");
        
//        sleep(2);
//        ForeverFailoverProcessor.getInstance().failResolve("normal1.1");
//        sleep(2);
//        ReportActionMork.morkReport("normal2.1", "standby0", "20");
//        sleep(2);
//        ReportActionMork.morkReport("normal2.1", "standby0", "40");
//        ReportActionMork.morkReport("normal2.1", "standby0", "56");
//        ReportActionMork.morkReport("normal2.1", "standby0", "78");
//        sleep(1);
//        ReportActionMork.morkReport("normal2.1", "standby0", "89");
//        sleep(6);
//        ReportActionMork.morkReport("normal2.1", "standby0", "100");
	
	
		
//		try {
//			ExpansionMigrateProcessor.getInstance().migerate(
//					StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
//		} catch (AdminServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		sleep(1);
//		ReportActionMork.morkReport("normal1.0", null, "20");
//		ReportActionMork.morkReport("normal1.1", null, "20");
//		ReportActionMork.morkReport("normal1.2", null, "20");
//		ReportActionMork.morkReport("normal1.3", null, "20");
//		ReportActionMork.morkReport("normal1.4", null, "20");
//		
//        sleep(3);
//        ReportActionMork.morkReport("normal1.0", null, "50");
//        ReportActionMork.morkReport("normal1.1", null, "50");
//        ReportActionMork.morkReport("normal1.2", null, "50");
//        ReportActionMork.morkReport("normal1.3", null, "100");
//        ReportActionMork.morkReport("normal1.4", null, "100");
//        sleep(2);
//        ReportActionMork.morkReport("normal1.0", null, "100");
//        ReportActionMork.morkReport("normal1.1", null, "100");
//        ReportActionMork.morkReport("normal1.2", null, "100");
		 
    }

    private static void sleep(int t) {

        try {
            Thread.sleep(t * 1100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
