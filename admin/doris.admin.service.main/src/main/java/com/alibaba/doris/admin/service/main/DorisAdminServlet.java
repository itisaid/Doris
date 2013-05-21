/**
 * Project: doris.config.server-1.0-SNAPSHOT File Created at 2011-4-27 $Id$ Copyright 1999-2100 Alibaba.com Corporation
 * Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.service.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;

/**
 * @author mian.hem
 */
public class DorisAdminServlet extends HttpServlet {

    /**
     * 
     */
    private static final long     serialVersionUID = -3890876783641703055L;

    private static final Log      logger           = LogFactory.getLog(DorisAdminServlet.class);
    private AdminServiceBootStrap bootstrap        = null;

    @Override
    public void init() throws ServletException {
        if (logger.isInfoEnabled()) {
            logger.info("DorisConfigServiceServlet initialize starts.");
        }

        super.init();

        if (logger.isInfoEnabled()) {
            logger.info("DorisConfigServiceServlet initialize ends.");
        }

        // start administration service
        bootstrap = new AdminServiceBootStrap();
        bootstrap.start();
    }

    @SuppressWarnings("unchecked")
    private void doResponse(HttpServletRequest request, HttpServletResponse response) throws IOException,
                                                                                     ServletException {
        String ip = DorisConfigUtil.getIpAddr(request);
        String uuid = UUID.randomUUID().toString();
        long begin = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("The request is from :" + ip + ", new assigned id is :" + uuid);
        }
        String resTxt = AdminServiceConstants.ADMIN_SERVICE_ERROR;
        try {

            String actionName = request.getParameter(AdminServiceConstants.ADMIN_SERVICE_ACTION_NAME);

            if (StringUtils.isNotEmpty(actionName)) {
                // get Action
                AdminServiceAction serviceAction = AdminServiceActionFactory.getAdminServiceAction(actionName);
                if (logger.isDebugEnabled()) {
                    logger.debug("The action for request\"" + uuid + "\" is " + actionName);
                }
                Map<String, String[]> paramsMap = request.getParameterMap();
                Map<String, String> paras = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
                    String paraKey = entry.getKey();
                    String[] values = entry.getValue();
                    if (values.length > 0) {
                        String paraValue = entry.getValue()[0];
                        paras.put(paraKey, paraValue);
                    }
                }
                // used in some action
                paras.put(AdminServiceConstants.REMOTE_IP, ip);

                resTxt = serviceAction.execute(paras);

            } else {
                // return empty string for request that is not specified action name.
                resTxt = AdminServiceConstants.ADMIN_SERVICE_ERROR;
            }

        } catch (Exception e) {
            resTxt = AdminServiceConstants.ADMIN_SERVICE_ERROR;
            logger.error(e.getMessage(), e);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(resTxt);

        writer.close();
        
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug("The time cost for request\"" + uuid + "\" is " + (end - begin));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doResponse(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doResponse(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
        bootstrap.stop();
    }

}
