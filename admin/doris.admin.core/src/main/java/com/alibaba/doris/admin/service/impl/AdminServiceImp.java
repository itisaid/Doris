package com.alibaba.doris.admin.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.common.util.IPAddressUtil;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-7-4 下午06:14:43
 * @version :
 * @Modification:
 */
public class AdminServiceImp implements AdminService {

	private static final Log logger = LogFactory.getLog(AdminServiceImp.class);

    private String masterIP;

    public boolean isMasterAdmin() {
        String ip = IPAddressUtil.getIPAddress();
        return isMasterAdmin(ip);
    }

    public boolean isMasterAdmin(String ip) {
    	
    	if( StringUtils.isBlank(ip)  || StringUtils.isBlank(masterIP)) {
    		return false;
    	}
    	
    	InetAddress address;
		try {
			address = InetAddress.getByName( masterIP );
			String aIP = address.getHostAddress();
	        return  StringUtils.equals(ip, aIP);
		} catch (UnknownHostException e) {
			logger.error("Invalid master ip/domain: " + masterIP, e);
			throw new IllegalArgumentException("Invalid master ip/domain: " + masterIP, e);
		}
    
    }

    public void setMasterIP(String masterIP) {
        this.masterIP = masterIP;
    }
    
    public static void main(String[] args) {
    	AdminServiceImp adminServiceImp = new AdminServiceImp();
    	
    	adminServiceImp.setMasterIP("doris-test.alibaba-inc.com");
    	logger.info("Local is master ?  " + adminServiceImp.isMasterAdmin() ) ;
    	
	}

}
