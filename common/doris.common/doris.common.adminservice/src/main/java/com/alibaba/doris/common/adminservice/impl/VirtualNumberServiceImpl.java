package com.alibaba.doris.common.adminservice.impl;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.VirtualNumberService;

public class VirtualNumberServiceImpl extends BaseAdminService<Integer>
		implements VirtualNumberService {

	private static VirtualNumberServiceImpl instance = new VirtualNumberServiceImpl();

	private Integer virtualNumber;

	private VirtualNumberServiceImpl() {
	}

	public static VirtualNumberServiceImpl getInstance() {
		return instance;
	}

	public int getVirtualNumber() {
		Integer virtualNumber = requestForce(null);
		if (virtualNumber != null) {
			this.virtualNumber = virtualNumber;
			return virtualNumber;
		}
		if (virtualNumber == null && this.virtualNumber != null) {
			return this.virtualNumber;
		}
		throw new RuntimeException("Could not initilize virtual node number!");
	}

	@Override
	public Integer convert(String response) {
		if (StringUtils.isEmpty(response) || !StringUtils.isNumeric(response)) {
			return null;
		}

		return Integer.valueOf(response);
	}

	@Override
	public String getActionName() {
		return AdminServiceConstants.VIRTUAL_NUMBER_ACTION;
	}

}
