package com.alibaba.doris;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 * XXU reopen all test cases.
 */
public class ConfigManagerImplTest extends TestCase {
    
    public void test() {
        
    }

//    private static boolean pushed = false;
//    private JettyServer    server = new JettyServer();
//
//    public void testLoadProperties() throws Exception {
//
//        startServlet(new MockDorisConfigServletWithConfigChange());
//
//        ConfigManagerImpl configManager = createConfigManager();
//
//        Properties configProperties = configManager.getProperties();
//
//        assertNotNull(configProperties);
//        assertEquals("http://127.0.0.1:8118/doris.config",
//                configProperties.get("doris.config.adminserver.url"));
//
//        stopServlet();
//
//    }
//
//    private ConfigManagerImpl createConfigManager() {
//        ConfigManagerImpl configManager = new ConfigManagerImpl();
//        configManager.setConfigLocation("com/alibaba/doris/dorisconfig.properties");
//
//        try {
//            configManager.initConfig();
//        } catch (ConfigException e) {
//            fail("failed to init config");
//        }
//        return configManager;
//    }
//
//    private void startServlet(HttpServlet servlet) throws Exception {
//        server.startServer(servlet);
//        assertTrue(server.isServerStarted());
//    }
//
//    private void stopServlet() throws Exception {
//        server.stopServer();
//        pushed = false;
//    }
//
//    public void testPush() throws Exception {
//
//        startServlet(new MockDorisConfigServletWithConfigChange());
//
//        ConfigManagerImpl configManager = createConfigManager();
//
//        ConfigListener configListener = new ConfigListener() {
//
//            public String getConfigListenerName() {
//                return "routeConfig";
//            }
//
//            public void onConfigChange(String configContent) {
//                System.out.println(configContent);
//                pushed = true;
//            }
//
//            public Long getConfigVersion() {
//                return 0L;
//            }
//
//        };
//
//        configManager.addConfigListener(configListener);
//
//        assertTrue(pushed);
//
//        stopServlet();
//    }
//
//    public void testNotPushed() throws Exception {
//
//        startServlet(new MockDorisConfigServletWithoutConfigChange());
//
//        ConfigManagerImpl configManager = createConfigManager();
//
//        ConfigListener configListener = new ConfigListener() {
//
//            public String getConfigListenerName() {
//                return "routeConfig";
//            }
//
//            public void onConfigChange(String configContent) {
//                System.out.println(configContent);
//                pushed = true;
//            }
//
//            public Long getConfigVersion() {
//                // TODO Auto-generated method stub
//                return 0L;
//            }
//
//        };
//
//        configManager.addConfigListener(configListener);
//
//        assertFalse(pushed);
//
//        stopServlet();
//    }
}
