package com.alibaba.doris.common.trace.support;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.alibaba.doris.common.PrefReportUnit;
import com.alibaba.doris.dataserver.monitor.support.PrefTrackerKey;
import com.alibaba.doris.dataserver.monitor.support.PrefTrackerUnit;

public class PrefTrackerUnitTest {

    @Test
    public void testOpTracker_1() throws Exception {

        PrefTrackerUnit tracker = new PrefTrackerUnit(new PrefTrackerKey("aa", "bb"),
                new Properties());

        for (int i = 0; i < 100000; i++) {

            tracker.incConcurrencyLevel();

            tracker.trackLatency(i % 100 + 1);

            tracker.trackBytes(i % 100 + 1);

            tracker.decConcurrencyLevel();
        }

        PrefReportUnit report = tracker.report(false);

        assertEquals(report.getTotalBytes(), report.getTotalLatency());

        assertEquals(100, report.getMaxLatency());

        assertEquals(80, report.getThe80thLatency());
        assertEquals(95, report.getThe95thLatency());
        assertEquals(99, report.getThe99thLatency());
        assertEquals(1, report.getMaxConcurrencyLevel());
        assertEquals(0, report.getCurrentConcurrencyLevel());
    }

}
