package com.alibaba.doris.dataserver.monitor;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.ModuleContextAware;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.monitor.action.StatsAction;
import com.alibaba.doris.dataserver.monitor.action.StatsActionType;
import com.alibaba.doris.dataserver.monitor.support.PrefReportThread;
import com.alibaba.doris.dataserver.monitor.support.UpTime;

/**
 * 定时向admin server 报告状态
 * 
 * @author helios
 */
public class MonitorMudule extends BaseModule implements ModuleContextAware {

    private static final PrefReportThread thread = new PrefReportThread();

    public void load(ModuleConfigure conf) {
        initThread(conf);

        ActionFactory.registAction(StatsActionType.STATS, new StatsAction());

        thread.start();

        // 设置服务器启动时间
        UpTime.init();
    }

    public void unload() {
        thread.setStop();
        thread.interrupt();
    }

    private void initThread(ModuleConfigure conf) {
        ModuleContext moduleContext = getModuleContext();
        if (null != moduleContext) {
            ApplicationContext appContext = moduleContext.getApplicationContext();
            
            Module module = appContext.getModuleByName(ModuleConstances.NETWORK_MODULE);
            
            ModuleContext netWorkModuleContext = module.getModuleContext();
            
            if (netWorkModuleContext == null) {
                throw new RuntimeException("netWorkModuleContext not found");
            }
            
            int port = (Integer) netWorkModuleContext.getAttribute("serverPort");

            thread.setPort(port);
        }

        int reportIntervalInMinite = conf.getParamAsInt("reportIntervalInMinite", 5);
        thread.setIntervalInMinites(reportIntervalInMinite);
    }
}
