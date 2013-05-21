package com.alibaba.doris.dataserver.event.config;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ConfigEventListener extends EventListener {

    /**
     * 路由配置变更事件，当集群路由相关的信息发生改变后会触发该事件。<br>
     * <br>
     * 同时ConfigEvent会附带上变更后及变更前的路由信息。
     * 
     * @param event
     */
    public void onRoutingConfigureChanged(ConfigEvent event);
}
