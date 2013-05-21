package com.alibaba.doris.dataserver.monitor.support;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.doris.common.PrefReportUnit;

public class PrefTrackerUnit {

    /**
     * 记录桶数量
     */
    private int                _buckets                = 1000;

    private PrefTrackerKey     key;

    private AtomicIntegerArray histogram;
    private AtomicInteger      histogramoverflow;
    private AtomicLong         operations;
    private AtomicLong         totalLatency;
    private AtomicLong         totalBytes;

    private int                min;
    private int                max;

    private long               timeStart;

    private int                maxConcurrencyLevel;

    AtomicInteger              currentConcurrencyLevel = new AtomicInteger(0);

    public PrefTrackerUnit(PrefTrackerKey key, Properties props) {
        this.key = key;

        init();
    }

    private void init() {
        histogram = new AtomicIntegerArray(_buckets);
        histogramoverflow = new AtomicInteger(0);

        operations = new AtomicLong(0);
        totalLatency = new AtomicLong(0);
        totalBytes = new AtomicLong(0);

        min = -1;
        max = -1;

        maxConcurrencyLevel = 0;

        timeStart = System.currentTimeMillis();
    }

    public void trackLatency(int latency) {
        if (latency >= _buckets) {
            histogramoverflow.incrementAndGet();
        } else {
            histogram.incrementAndGet(latency);
        }
        operations.incrementAndGet();
        totalLatency.addAndGet(latency);

        if ((min < 0) || (latency < min)) {
            min = latency;
        }

        if ((max < 0) || (latency > max)) {
            max = latency;
        }
    }

    public void incConcurrencyLevel() {
        int current = currentConcurrencyLevel.incrementAndGet();
        if (maxConcurrencyLevel < current) {
            maxConcurrencyLevel = current;
        }
    }

    public void decConcurrencyLevel() {
        currentConcurrencyLevel.decrementAndGet();
    }

    public PrefTrackerKey getKey() {
        return key;
    }

    public void trackBytes(int bytes) {
        totalBytes.addAndGet(bytes);
    }

    /**
     * 定时
     * 
     * @param needInit
     * @return
     */
    public PrefReportUnit report(boolean needInit) {
        long operationCount = operations.get();

        if (operationCount <= 0) {
            return null;
        }

        PrefReportUnit report = new PrefReportUnit();

        report.setTotalOperation(operationCount);
        report.setMinLatency(min);
        report.setMaxLatency(max);
        report.setTotalLatency(totalLatency.get());
        report.setTotalBytes(totalBytes.get());
        report.setActionName(key.getActionName());
        report.setNameSpace(key.getNameSpace());
        report.setTimeStart(timeStart);
        report.setTimeUsed(System.currentTimeMillis() - timeStart);
        report.setMaxConcurrencyLevel(maxConcurrencyLevel);
        report.setCurrentConcurrencyLevel(currentConcurrencyLevel.get());

        setPercentileLatency(report);

        if (needInit) {
            init();
        }

        return report;
    }

    private void setPercentileLatency(PrefReportUnit report) {

        int opcounter = 0;

        boolean done80th = false;
        boolean done95th = false;

        int the80thLatency = -1;
        int the95thLatency = -1;
        int the99thLatency = -1;

        long operationCount = operations.get();

        for (int i = 0; i < _buckets; i++) {

            opcounter += histogram.get(i);

            if ((!done80th) && (((double) opcounter) / operationCount >= 0.80)) {
                the80thLatency = i;
                done80th = true;
            }

            if ((!done95th) && (((double) opcounter) / operationCount >= 0.95)) {
                the95thLatency = i;
                done95th = true;
            }

            if (((double) opcounter) / ((double) operations.get()) >= 0.99) {
                the99thLatency = i;
                break;
            }

        }

        report.setThe80thLatency(the80thLatency);
        report.setThe95thLatency(the95thLatency);
        report.setThe99thLatency(the99thLatency);

    }

    @Override
    public String toString() {
        return String
                .format("OpTracker [_buckets=%s, histogram=%s, histogramoverflow=%s, operations=%s, totalLatency=%s, totalBytes=%s, min=%s, max=%s, timeStart=%s, name=%s]",
                        _buckets, histogram, histogramoverflow, operations, totalLatency,
                        totalBytes, min, max, timeStart, key.getActionName());
    }

}
