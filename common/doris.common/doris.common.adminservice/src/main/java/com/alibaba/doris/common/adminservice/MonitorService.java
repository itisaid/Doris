package com.alibaba.doris.common.adminservice;

import java.util.List;

import com.alibaba.doris.common.PrefReportUnit;

public interface MonitorService {

    String report(List<PrefReportUnit> report, int port);

}
