/**
 * Project: doris.admin.config.service-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-13
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.admin.service.main;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author mian.hem
 *
 */
public class DorisConfigUtil {

    /**
     * <p>
     * 取客户端ip，处理客户端经过代理服务器访问我们的service的情况，我们希望拿到客户端的真实IP，
     * 即使它经过多层代理.
     * <p>
     * request.getRemoteAddr()只能拿到直接请求Service的IP
     * <p>
     * 如果客户端的请求经过代理，那么在其Http头信息中会添加x-forwarded-for字段，记录原始IP，如果经过多级代理，
     * 那么会按照先后顺序将原始IP记录下来
     * <p>
     * 有的服务器可能会将forwarded-for关掉，这时要通过Proxy-Client-IP或WL-Proxy-Client-IP来取值，
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;

        ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String[] temp = StringUtils.split(ip, ',');
        if (temp.length > 1) {
            for (int i = 0; i < temp.length; i++) {// 找到第一个不为unknown的值
                if (!"unknown".equalsIgnoreCase(temp[i])) {
                    ip = temp[i];
                    break;
                }
            }
        }

        return ip;
    }
    
}
