package com.alibaba.doris.dataserver.monitor.support;

import com.alibaba.doris.common.RealtimeInfo;
import com.alibaba.doris.dataserver.monitor.action.StatsActionData;

/**
 * 统计实时信息
 * <ul>
 * <li>uptime:sdfsd</li>
 * <ul>
 * 
 * @author helios
 */
public class RealtimeInfoBuilder {

    StatsActionData statsActionData;

    public RealtimeInfoBuilder(StatsActionData statsActionData) {
        this.statsActionData = statsActionData;
    }

    public RealtimeInfo buildRealtimeInfo() {
        RealtimeInfo realtimeInfo = new RealtimeInfo();

        realtimeInfo.setUpTime(UpTime.getUpTimeInSeconds());
        realtimeInfo.setPrefReports(PerfTracker.report(false));

        return realtimeInfo;
    }

}
