package com.alibaba.doris.admin.web.monitor.module.screen;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.web.monitor.support.NodeViewForMonitor;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.common.RealtimeInfo;
import com.alibaba.fastjson.JSON;

public class Realtime {

    private AdminNodeService adminNodeService = AdminServiceLocator.getAdminNodeService();

    public void execute(Context context, @Param("physicalId") String physicalId) {
        PhysicalNodeDO physicalNodeDO = adminNodeService.queryPhysicalNodeByPhysicalId(physicalId);
        if (physicalNodeDO != null) {
            NodeViewForMonitor view = getInfo(physicalNodeDO);
            context.put("view", view);
        } else {
            NodeViewForMonitor view = new NodeViewForMonitor(new PhysicalNodeDO());
            view.setErrorInfo("node not found");
            context.put("view", view);
        }
        context.put("physicalId", physicalId);
    }

    private synchronized NodeViewForMonitor getInfo(PhysicalNodeDO physicalNodeDO) {
        NodeViewForMonitor view = new NodeViewForMonitor(physicalNodeDO);

        String ip = physicalNodeDO.getIp();
        int port = physicalNodeDO.getPort();
        InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);

        try {
            String infoString = doStat(remoteAddress);

            RealtimeInfo realtimeInfo = JSON.parseObject(infoString, RealtimeInfo.class);

            view.setRealtimeInfo(realtimeInfo);

        } catch (Exception e) {
            view.setErrorInfo(e.getMessage());
        }

        return view;
    }

    private String doStat(InetSocketAddress remoteAddress) throws InterruptedException,
            ExecutionException {
        Connection connection = ConnectionFactory.getInstance().getConnection(remoteAddress);

        String info = "";

        try {
            connection.open();

            info = connection.stats(null, -1).get();

        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                //do nothing
            }
        }
        return info;
    }

}
