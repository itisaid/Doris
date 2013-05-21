package com.alibaba.doris.common;

import java.util.List;

public class RealtimeInfo {

    private int                  upTime;

    private List<PrefReportUnit> prefReports;

    public int getUpTime() {
        return upTime;
    }

    public void setUpTime(int upTime) {
        this.upTime = upTime;
    }

    public List<PrefReportUnit> getPrefReports() {
        return prefReports;
    }

    public void setPrefReports(List<PrefReportUnit> prefReports) {
        this.prefReports = prefReports;
    }

}
