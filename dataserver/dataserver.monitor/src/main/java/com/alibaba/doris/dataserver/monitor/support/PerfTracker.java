package com.alibaba.doris.dataserver.monitor.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.common.PrefReportUnit;

public class PerfTracker {

    private static ConcurrentHashMap<String, PrefTrackerUnit> trackers = new ConcurrentHashMap<String, PrefTrackerUnit>();

    public static PrefTrackerUnit getTraker(PrefTrackerKey operation) {
        String key = operation.getkey();

        PrefTrackerUnit unit = trackers.get(key);

        if (unit == null) {
            unit = new PrefTrackerUnit(operation, new Properties());
            trackers.put(key, unit);
        }

        return unit;
    }

    /**
     * 获取统计报表
     * 
     * @param init 是否重新开始计数
     * @return
     */
    public static List<PrefReportUnit> report(boolean init) {
        List<PrefReportUnit> statsLoggers = new ArrayList<PrefReportUnit>();
        for (PrefTrackerUnit trace : trackers.values()) {
            PrefReportUnit report = trace.report(init);
            if (report != null) {
                statsLoggers.add(report);
            }
        }

        return statsLoggers;
    }

}
