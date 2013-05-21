package com.alibaba.doris.common.adminservice.factory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import com.alibaba.doris.common.ConsistentErrorType;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.adminservice.CheckFailService;
import com.alibaba.doris.common.adminservice.CommonConfigService;
import com.alibaba.doris.common.adminservice.ConsistentErrorReportService;
import com.alibaba.doris.common.adminservice.MigrateReportService;
import com.alibaba.doris.common.adminservice.MonitorService;
import com.alibaba.doris.common.adminservice.NamespaceService;
import com.alibaba.doris.common.adminservice.RouterConfigService;
import com.alibaba.doris.common.adminservice.StoreNodeService;
import com.alibaba.doris.common.adminservice.VirtualNumberService;
import com.alibaba.doris.common.adminservice.connenctor.AdminConnector;

public class AdminServiceFactoryTest extends TestCase {

    CheckFailService             cfs               = AdminServiceFactory.getCheckFailService();
    CommonConfigService          ccs               = AdminServiceFactory.getCommonConfigService();
    MigrateReportService         mrs               = AdminServiceFactory.getMigrateReportService();
    MonitorService               ms                = AdminServiceFactory.getMonitorService();
    NamespaceService             nss               = AdminServiceFactory.getNamespaceService();
    RouterConfigService          rcs               = AdminServiceFactory.getRouterConfigService();
    StoreNodeService             sns               = AdminServiceFactory.getStoreNodeService();
    VirtualNumberService         vms               = AdminServiceFactory.getVirtualNumberService();
    ConsistentErrorReportService consistentService = AdminServiceFactory.getConsistentErrorReportService();

    public void setUp() {
        Properties properties = loadProperties("dorisconfig.properties");
        AdminConnector conn = AdminConnector.getInstance();
        conn.init(properties);

    }

    private Properties loadProperties(String location) {
        Properties properties = null;
        // load config & parse config
        URL url = getClass().getClassLoader().getResource(location);
        InputStream is = null;

        if (url == null) {
            throw new IllegalArgumentException("failed to load properties not set!" + location);
        }

        try {
            is = url.openStream();
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                fail(e.getMessage());
                e.printStackTrace();
            }
        }

        return properties;
    }

    // public void testCheckFailService() {
    // StoreNode sn = new StoreNode();
    // sn.setPhId("1.1.1.1:80");
    // boolean c = cfs.checkFailNode(sn);
    // print("check result:" + c);
    // }
    //
    // public void testCommonConfigService() {
    // List<String> paras = new ArrayList<String>();
    // paras.add(AdminServiceConstants.ROUTE_CONFIG_ACTION);
    // Map<String, String> map = ccs.getConfig(paras);
    // print("common config:" + map);
    // }
    //
    // public void testMigrateReportService() {
    // String r = mrs.report("1.1.1.1:80", "1.2.2.2:81", 0, MigrateStatusEnum.COMMAND_FAIL, "w");
    // print("migrate:" + r);
    // }
    //
    // public void testMonitorService() {
    // System.out.println("Monitor: "+ms.report(null, 9000));
    // }

    public void testConsistentErrorReportService() {
        String exceptionMsg = "test error";
        int namespaceId = 2;
        String key = "test key";
        String phisicalIps = "10.0.0.1:8080;10.0.0.2:9090";
        Boolean result = consistentService.report(namespaceId, key, phisicalIps, exceptionMsg,
                                                  ConsistentErrorType.read, Long.toString(System.currentTimeMillis()));
        // assertEquals(true, result.booleanValue());

    }

    // XXU reopen this case later
    /*
     * public void testNamespaceService() { JettyServer server = new JettyServer(); try { server.startServer(new
     * NamespaceServlet()); } catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
     * Map<String, Namespace> nsm = nss.fetchNameSpace(); System.out.println("all name space: " + nsm); String name =
     * "x"; Namespace ns = nss.fetchNameSpace(name); print("name space of " + name + " is : " + ns); name = "mao"; ns =
     * nss.fetchNameSpace(name); print("name space of " + name + " is : " + ns); try { server.stopServer(); } catch
     * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); } }
     */

    // XXU reopen this case later
    /*
     * public void testRouterConfigService() { String routeConfig = rcs.getRouteConfig(); RouteConfigInstance
     * configInstance = JSON.parseObject(routeConfig, RouteConfigInstance.class); // assertNotNull(configInstance); //
     * assertNotNull(configInstance.getStoreNodes()); long version = configInstance.getVersion() ;
     * assertTrue(configInstance.getVersion() > 0);
     * System.out.println("==========Testing method: RouterConfigService#getRouteConfig()========>");
     * System.out.println("route config instancde version:" + configInstance.getVersion()); for (StoreNode storeNode :
     * configInstance.getStoreNodes()) { StringBuilder nodeDetail = new StringBuilder();
     * nodeDetail.append("node ip:").append(storeNode.getIp()).append(";");
     * nodeDetail.append("node logical id:").append(storeNode.getLogicId()).append(";");
     * nodeDetail.append("node physical id:").append(storeNode.getPhId()).append(";");
     * nodeDetail.append("node port:").append(storeNode.getPort()).append(";");
     * nodeDetail.append("node url:").append(storeNode.getURL()).append(";");
     * nodeDetail.append("node sequence:").append(storeNode.getSequence()).append(";");
     * nodeDetail.append("node status:").append(storeNode.getStatus()).append(";");
     * System.out.println(nodeDetail.toString()); } routeConfig = rcs.getRouteConfig(0L); configInstance =
     * JSON.parseObject(routeConfig, RouteConfigInstance.class);
     * System.out.println("==========Testing method: RouterConfigService#getRouteConfig(0)========>");
     * System.out.println("route config instancde version:" + configInstance.getVersion()); // assertEquals(version,
     * configInstance.getVersion()); routeConfig = rcs.getRouteConfig(version); configInstance =
     * JSON.parseObject(routeConfig, RouteConfigInstance.class); // assertNull(configInstance); routeConfig =
     * rcs.getRouteConfig(version + 1); configInstance = JSON.parseObject(routeConfig, RouteConfigInstance.class); //
     * assertNull(configInstance); routeConfig = rcs.getRouteConfig(version -1); configInstance =
     * JSON.parseObject(routeConfig, RouteConfigInstance.class); // assertEquals(version, configInstance.getVersion());
     * }
     */

    // public void testStoreNodeService() {
    // StoreNode sn = sns.getStoreNode("10.20.30.50:8086");
    // print(sn);
    // }
    /**
     * XXU:reopen this case later
     */
    /*
     * public void testVirtualNumberService() { int v = vms.getVirtualNumber(); print("virtual number :" + v); }
     */

    private void print(Object s) {
        System.out.println(s);
    }

}
