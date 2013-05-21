package com.alibaba.doris.common.adminservice.connenctor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

import com.alibaba.doris.common.util.PropertiesLoadUtil;

public class AdminConnectorTest extends TestCase {

    @Test
    public void test() {
        //XXU reopen this case later
        /*
        JettyServer server = new JettyServer();
        try {
            server.startServer(new RouteConfigServlet());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        AdminConnector connector = AdminConnector.getInstance();
        Properties properties = PropertiesLoadUtil.loadProperties("dorisconfig.properties");

        connector.init(properties);

        Map<String, String> paramMap = new HashMap<String, String>();
        String result = connector.requst(paramMap);
        assertEquals("OK", result);*/
    }
}
