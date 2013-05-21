package com.alibaba.doris.common.router.virtual;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * 虚拟节点路由器，根据key返回虚拟节点编号<br>
 * 用于data server，根据虚拟节点写不同存储文件
 * 
 * @author frank
 */
public class VirtualRouterImpl implements VirtualRouter {

	private static VirtualRouter instance = new VirtualRouterImpl();

	protected int virtualNum = 100;  //default.
	
	protected RouteAlgorithm algorithm;

	public static VirtualRouter getInstance() {
		return instance;
	}

	public VirtualRouterImpl() {
		virtualNum = AdminServiceFactory.getVirtualNumberService().getVirtualNumber();
		algorithm = new VpmRouterAlgorithm(1, virtualNum);
	}

	public int getVirtualNum() {
		return virtualNum;
	}

	public int findVirtualNode(String key) {
		return algorithm.getVirtualByKey(key);
	}
}
