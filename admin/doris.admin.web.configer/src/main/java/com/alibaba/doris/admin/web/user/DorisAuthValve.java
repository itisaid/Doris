package com.alibaba.doris.admin.web.user;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.PipelineException;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.uribroker.URIBrokerService;
import com.alibaba.citrus.service.uribroker.uri.URIBroker;
import com.alibaba.doris.admin.dataobject.UserDO;
import com.alibaba.doris.admin.web.configer.util.WebConstant;
import com.alibaba.citrus.turbine.TurbineRunDataInternal;
import com.alibaba.citrus.turbine.util.TurbineUtil;

/**
 * 用于权限的验证
 * 
 * @author chenchao.yecc
 * @since 0.1.4
 */
public class DorisAuthValve extends AbstractValve {
    private static final Log    logger      = LogFactory.getLog(DorisAuthValve.class);
    private static final String authNeed    = System.getProperty("login.auth");
    @Autowired
    private HttpSession         session;
    @Autowired
    private URIBrokerService    uriBrokerService;
    @Autowired
    private HttpServletRequest  request;
    private final Set<String>   ignoreList  = new HashSet<String>();
    private static String[]     ignoreNames = { "/ok", "/error", "/login" };

    public void invoke(PipelineContext context) throws Exception {

        if (needAuthValve()) {
            doAuthValve();
        }
        context.invokeNext();
    }

    /*
     * 判断URL是否需要认证
     */
    private boolean needAuthValve() {

        // 在dev模式下不需要验证
        if (authNeed != null && authNeed.equals("false")) {
            return false;
        }
        TurbineRunDataInternal rundata = getRunData();
        String target = rundata.getTarget();
        return !ignoreList.contains(target);
    }

    /*
     * 执行认证的逻辑
     */
    private void doAuthValve() {
        UserDO userDO = (UserDO) session.getAttribute(WebConstant.DORIS_USER_SESSION_KEY);
        // 失败重定向到登录页面
        TurbineRunDataInternal rundata = getRunData();
        if (userDO == null || !userDO.isLogined()) {
            URIBroker broker = uriBrokerService.getURIBroker(WebConstant.LOGIN_LINK);
            if (broker == null) {
                logger.error(String.format("no URI Broker named {}!", WebConstant.LOGIN_LINK));
                throw new PipelineException("no URI Broker.");
            }
            rundata.setRedirectLocation(broker.render());
        }
        rundata.getContext().put("userDO", userDO);
    }

    @Override
    protected void preInit() throws Exception {
        super.preInit();
        if (uriBrokerService == null) {
            throw new Exception("uriBrokerService required.");
        }
        for (int i = 0; i < ignoreNames.length; i++) {
            ignoreList.add(ignoreNames[i]);
        }
    }

    private TurbineRunDataInternal getRunData() {
        return (TurbineRunDataInternal) TurbineUtil.getTurbineRunData(request);
    }
}
