package com.alibaba.doris.dataserver;

import com.alibaba.doris.dataserver.action.data.CheckActionData;

/**
 * 模块状态检查接口，当Admin Server检查服务器运行状态时，<br>
 * 系统会调用实现了本接口的Module来监测本Module运行状态。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ModuleStatusChecker {

    boolean isReady(CheckActionData checkActionData);
}
