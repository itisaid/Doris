package com.alibaba.doris.dataserver.monitor.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.PrefReportUnit;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.adminservice.MonitorService;

/**
 * 在每个data server 单线程 运行
 * 
 * @author helios
 */
public class PrefReportThread extends Thread {

    private static final Log     logger                  = LogFactory
                                                                 .getLog(PrefReportThread.class);

    private static final int     QUEUE_MAX               = 1000;
    private static final long    DEFAULT_REPORT_INTERVAL = 5 * 60 * 1000L;

    private MonitorService       monitorService          = AdminServiceFactory.getMonitorService();

    private long                 reportInterval          = DEFAULT_REPORT_INTERVAL;
    private int                  serverPort              = -1;

    private List<PrefReportUnit> reports                 = new ArrayList<PrefReportUnit>();
    protected volatile boolean   stopped                 = false;

    public PrefReportThread() {
        super("monitor_pref_report_thread");
    }

    public void run() {
        while (!stopped && !Thread.interrupted()) {

            try {
                Thread.sleep(reportInterval);
            } catch (InterruptedException e) {
                System.out.println("PrefReportThread is interrupted ," + Thread.interrupted());
                setStop();
            }

            List<PrefReportUnit> report = PerfTracker.report(true);

            if (reports.size() < QUEUE_MAX) {
                reports.addAll(report);
            }

            boolean success = true;

            try {
                // 调用 report 接口
                if (reports.size() > 0) {
                    monitorService.report(reports, serverPort);
                }
            } catch (RuntimeException e) {
                logger.error(" pref report failed ", e);
                success = false;
            }

            if (success == true) {
                reports.clear();
            }
        }
    }

    public synchronized void setStop() {
        stopped = true;
    }

    public void setPort(int port) {
        this.serverPort = port;
    }

    public void setIntervalInMinites(int reportIntervalInMinite) {
        if (60 % reportIntervalInMinite == 0) {
            reportInterval = reportIntervalInMinite * 60 * 1000L;
        }
    }

}
