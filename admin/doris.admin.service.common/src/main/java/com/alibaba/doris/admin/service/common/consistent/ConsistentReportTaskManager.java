package com.alibaba.doris.admin.service.common.consistent;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.ConsistentReportDO;
import com.alibaba.doris.common.AdminServiceConstants;

public class ConsistentReportTaskManager {

    private Log                                log            = LogFactory.getLog(ConsistentReportTaskManager.class);

    private static final int                   MAX_QUEUE_SIZE = 100;
    private RingQueue<Map<String, String>>     taskQueue      = new RingQueue<Map<String, String>>(MAX_QUEUE_SIZE);
    private static ConsistentReportTaskManager instance       = new ConsistentReportTaskManager();

    public static ConsistentReportTaskManager getInstance() {
        return instance;
    }

    private ConsistentReportTaskManager() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

            public void run() {
                Map<String, String>[] allTasks = consumeAllTask();
                for (int i = 0; i < allTasks.length; i++) {
                    Map<String, String> map = allTasks[i];
                    storeConsistentReport(map);
                }
            }

        }, 1, 1, TimeUnit.SECONDS);

    }

    /**
     * 放入队列，队列中只保持最近的100条
     * 
     * @param task
     */
    public synchronized void produceReportTask(Map<String, String> task) {
        taskQueue.put(task);
    }

    public synchronized Map<String, String>[] consumeAllTask() {
        Map<String, String>[] tasksSnapshot = new Map[taskQueue.getSize()];
        taskQueue.getAll(tasksSnapshot);
        return tasksSnapshot;
    }

    private void storeConsistentReport(Map<String, String> map) {
        if (map == null) {
            return;
        }

        try {
            ConsistentReportDO consistentReportDO = buildConsistentReportDO(map);
            AdminServiceLocator.getConsistentReportService().saveConsistentReport(consistentReportDO);
        } catch (Exception e) {
            log.error("store consistent report error, report content: " + map, e);
        }
    }

    private ConsistentReportDO buildConsistentReportDO(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        ConsistentReportDO consistentReportDO = new ConsistentReportDO();
        consistentReportDO.setKeyStr(params.get(AdminServiceConstants.CONSISTENT_KEY));
        consistentReportDO.setNamespaceId(Integer.valueOf(params.get(AdminServiceConstants.CONSISTENT_NAMESPACE_ID)));
        consistentReportDO.setPhisicalNodeIps(params.get(AdminServiceConstants.CONSISTENT_PHISICAL_IPS));
        consistentReportDO.setClientIp(params.get(AdminServiceConstants.CONSISTENT_CLIENT_IP));
        consistentReportDO.setExceptionMsg(params.get(AdminServiceConstants.CONSISTENT_EXCEPTION_MSG));
        consistentReportDO.setTimestamp(params.get(AdminServiceConstants.CONSISTENT_TIMESTAMP));
        consistentReportDO.setErrorType(params.get(AdminServiceConstants.CONSISTENT_ERROR_TYPE));
        return consistentReportDO;
    }

}
