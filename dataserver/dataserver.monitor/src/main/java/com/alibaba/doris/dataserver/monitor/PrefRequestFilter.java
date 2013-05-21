package com.alibaba.doris.dataserver.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilter;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.monitor.support.PerfTracker;
import com.alibaba.doris.dataserver.monitor.support.PrefTrackerKey;
import com.alibaba.doris.dataserver.monitor.support.PrefTrackerUnit;

public class PrefRequestFilter implements RequestFilter {

    public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
        if (!needStat(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        PrefTrackerUnit trackerWithNameSpace = null;
        long start = 0;
        int bytes = 0;
        try {
            String operation = request.getActionData().getActionType().getName();

            Key keyObject = request.getKey();
            String key = keyObject.getKey();
            String namespace = String.valueOf(keyObject.getNamespace());

            bytes = key.length() + namespace.length();

            if (request.getValue() != null && request.getValue().getValueBytes() != null) {
                bytes += request.getValue().getValueBytes().length;
            }

            // 基于name space的跟踪器
            trackerWithNameSpace = PerfTracker.getTraker(new PrefTrackerKey(operation, namespace));

            start = System.currentTimeMillis();

            // 增加并发数 用于统计最大并发数
            trackerWithNameSpace.incConcurrencyLevel();
        } catch (Exception ignorPrefException) {
            logger.error("doFilter", ignorPrefException);
        }

        // 处理具体业务
        filterChain.doFilter(request, response);

        try {
            // 减少并发数
            trackerWithNameSpace.decConcurrencyLevel();

            // 记录延迟
            int latency = (int) (System.currentTimeMillis() - start);
            trackerWithNameSpace.trackLatency(latency);

            // 记录字节数valueOf
            trackerWithNameSpace.trackBytes(bytes);
        } catch (Exception ignorPrefException) {
            logger.error("doFilter", ignorPrefException);
        }
    }

    private boolean needStat(Request request) {
        ActionType operation = request.getActionData().getActionType();

        if (BaseActionType.SET.equals(operation) || BaseActionType.GET.equals(operation)
            || BaseActionType.DELETE.equals(operation) || BaseActionType.CAD.equals(operation)
            || BaseActionType.CAS.equals(operation)) {
            return request != null && request.getActionData() != null && request.getKey() != null
                   && !"null".equals(String.valueOf(request.getKey().getNamespace()));
        } else {
            return false;
        }
    }

    protected final Logger logger = LoggerFactory.getLogger(PrefRequestFilter.class);
}
