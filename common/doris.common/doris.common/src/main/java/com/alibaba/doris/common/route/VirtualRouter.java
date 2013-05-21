package com.alibaba.doris.common.route;


/**
 * 虚拟节点路由器，根据key返回虚拟节点编号<br>
 * 用于data server，根据虚拟节点写不同存储文件
 * 
 * @author frank, 
 * @author kun.hek Abstract a interface.
 */
public interface VirtualRouter {

    public int getVirtualNum();
    
    public int findVirtualNode(String key);
}
