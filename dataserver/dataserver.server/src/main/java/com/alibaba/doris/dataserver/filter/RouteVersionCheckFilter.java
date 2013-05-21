package com.alibaba.doris.dataserver.filter;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilter;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class RouteVersionCheckFilter implements RequestFilter {

    public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
        ActionData ad = request.getActionData();
        if (null != ad && ad instanceof BaseActionData) {
            BaseActionData bad = (BaseActionData) ad;
            long routeVersion = bad.getRouteVersion();
            // 当client带了有效的路由版本号才比较，否则忽略版本号比较功能。
            if (routeVersion > 0) {
                if (null == routeTableConfiger) {
                    synchronized (this) {
                        if (null == routeTableConfiger) {
                            Module module = request.getApplicationContext().getModuleByName(
                                                                                            ModuleConstances.ADMIN_CONFIGURE_MODULE);
                            ModuleContext mdContext = module.getModuleContext();
                            if (null != mdContext) {
                                routeTableConfiger = (RouteTableConfiger) mdContext.getAttribute("routeTableConfiger");
                                configManager = (ConfigManager) mdContext.getAttribute("configManager");
                            }
                        }
                    }
                }

                if (routeTableConfiger != null) {
                    RouteTable rt = routeTableConfiger.getRouteTable();

                    if (null != rt && routeVersion < rt.getVersion()) {
                        // 如果client的路由版本号过期，返回调用异常。
                        ErrorActionData error = new ErrorActionData(ErrorActionData.VERSION_OUT_OF_DATE);
                        // 设置server端的路由版本号
                        error.setErrorMessage(configManager.getCachedConfig(AdminServiceConstants.ROUTE_CONFIG_ACTION));
                        response.write(error);
                        // 跳过后续filter的执行。
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private RouteTableConfiger routeTableConfiger;
    private ConfigManager      configManager;
}
