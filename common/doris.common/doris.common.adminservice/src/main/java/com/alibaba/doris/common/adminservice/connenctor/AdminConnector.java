package com.alibaba.doris.common.adminservice.connenctor;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.util.IPAddressUtil;

public class AdminConnector {

    private static final Log      logger            = LogFactory.getLog(AdminConnector.class);
    private static AdminConnector instance          = new AdminConnector();

    //fetcher to acess remote admin server for configuration changes.(main)
    private ConfigFetcher         mainAdminFetcher           = new ConfigFetcher();
    
    
    //The back up admin fetcher
    private ConfigFetcher         backupAdminFetcher           = new ConfigFetcher();

    public static AdminConnector getInstance() {
        return instance;
    }

    private AdminConnector() {

    }

    public void init(Properties properties) {
        // initialize main fetcher
        String mainAdminUrl = properties.getProperty("doris.config.adminserver.main.url");
        if (mainAdminUrl != null) {
            properties.put("doris.config.adminserver.url", mainAdminUrl);
        }
        mainAdminFetcher.init(properties);
         
        // initialize backup fetcher
        String backupAdminUrl = properties.getProperty("doris.config.adminserver.backup.url");
        if (backupAdminUrl != null) {
            properties.put("doris.config.adminserver.url", backupAdminUrl);
        }
        
        backupAdminFetcher.init(properties);
    }

    public String requst(Map<String, String> paramMap) {
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            NameValuePair para = new NameValuePair();
            para.setName(entry.getKey());
            para.setValue(entry.getValue());
            paras.add(para);
        }
        
        String result = null;
        NameValuePair[] parasArray = paras.toArray(new NameValuePair[] {});
        try {
            result = this.mainAdminFetcher.fetch(parasArray);
        } catch (AdminConnectionException e) {
            try {
                result = this.backupAdminFetcher.fetch(parasArray);
            } catch (Exception e1) {
                // Swallow all exception
                logger.error("Cannot acess both main admin server and back admin server.", e1);
            }
        }
        return result;
    }

    public boolean isConnected() {
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("test", "test");
        return this.mainAdminFetcher.checkConnection(params)
                || this.backupAdminFetcher.checkConnection(params);
    }

    private class ConfigFetcher {

        public String                                    accessUrl;

        private final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        private final HttpClient                         httpClient        = new HttpClient(
                                                                                   connectionManager);

        private String                                   hostAddress;
        private String                                   hostName;

        private int                                      retry             = 3;

        public void init(Properties properties) {

            if (logger.isInfoEnabled()) {
                logger.info("admin connector initization starts.");
            }

            if (properties == null) {
                throw new IllegalArgumentException("The connector properties could not be null");
            }
            // admin server access url
            this.accessUrl = properties.getProperty("doris.config.adminserver.url");
            if (StringUtils.isEmpty(accessUrl)) {
                throw new IllegalArgumentException("confg 'doris.config.adminserver.url' is not valid.");
            }

            // fetch retry times.
            String retryTimesConfig = properties.getProperty("doris.config.fetch.retrytimes", "3");

            this.retry = Integer.parseInt(retryTimesConfig.trim());

            // connection time out
            String connectionTimeoutConfig = properties.getProperty("doris.config.connection.timeout");
            if (StringUtils.isEmpty(connectionTimeoutConfig)) {
                throw new IllegalArgumentException(
                        "confg 'doris.config.connection.timeout' is not valid.");
            }
            Integer connectionTimeout = Integer.parseInt(connectionTimeoutConfig.trim());

            // socket time out
            String soTimeoutConfig = properties.getProperty("doris.config.connection.socket.timeout");
            if (StringUtils.isEmpty(soTimeoutConfig)) {
                throw new IllegalArgumentException(
                        "confg 'doris.config.connection.socket.timeout' is not valid.");
            }
            Integer soTimeout = Integer.parseInt(soTimeoutConfig.trim());
            
            // 设置连接超时时间
            connectionManager.getParams().setConnectionTimeout(connectionTimeout);

            // 设置读数据超时时间
            connectionManager.getParams().setSoTimeout(soTimeout);

            // 抓取客户端的主机信息
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                hostName = localhost.getHostName();
            } catch (UnknownHostException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Fail to get host name:" + e.getMessage());
                }
            }

            if (hostName == null) {
                hostName = "unknown";
            }

            try {
                hostAddress = IPAddressUtil.getIPAddress();
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Error while getting host address:" + e.getMessage());
                }
            }
            if (hostAddress == null) {
                hostAddress = "unknown";
            }

            if (logger.isInfoEnabled()) {
                logger.info("admin connector client hostName:" + hostName + ",hostAddress:" + hostAddress);
            }
        }

        private String fetch(NameValuePair[] params) throws AdminConnectionException {
            PostMethod method = new PostMethod(accessUrl);
            String result = null;
            try {
                method.addParameters(params);
                String errMsg = null;
                int retried = retry;
                while (retried > 0) {
                    try {
                        int statusCode = httpClient.executeMethod(method);
                        if (statusCode != HttpStatus.SC_OK) {
                            errMsg = "Fail to request from server: status code(" + statusCode
                                    + "), status line(" + method.getStatusLine() + ")";
                            logger.warn(errMsg);
                        }
                        result = method.getResponseBodyAsString();
                        retried = 0;
                    } catch (Exception e) {
                        retried--;
                        errMsg = "Fail to request from server: " + accessUrl;
                        logger.error(errMsg, e);

                        if (retried == 0) {
                            throw new AdminConnectionException("Cannot connect to Admin:"
                                    + accessUrl);
                        }
                    }
                }

            } finally {
                method.releaseConnection();
            }

            return result;
        }

        private boolean checkConnection(NameValuePair[] params) {
            HttpMethodBase method = new PostMethod(accessUrl);
            boolean connected = false;
            try {
                method.setQueryString(params);
                String errMsg = null;
                int retried = retry;
                while (retried > 0) {
                    if (connected == true) {
                        break;
                    }
                    boolean hasException = false;
                    try {
                        int statusCode = httpClient.executeMethod(method);
                        if (statusCode != HttpStatus.SC_OK) {
                            errMsg = "Fail to request from server: status code(" + statusCode
                                    + "), status line(" + method.getStatusLine() + ")";
                            logger.warn(errMsg);
                        }
                        retried = 0;
                    } catch (ConnectException e) {
                        retried--;
                        errMsg = "Fail to request from admin server: " + accessUrl;
                        logger.error(errMsg, e);
                        hasException = true;
                    } catch (Exception e) {
                        retried = 0;
                        errMsg = "Fail to request from server: "  + accessUrl;
                        hasException = true;
                        logger.error(errMsg, e);
                    }
                    if (!hasException) {
                        connected = true;
                    }
                }
            } finally {
                method.releaseConnection();
            }

            if (logger.isInfoEnabled()) {
                logger.info("Admin Server \"" + accessUrl + "\" can be connected? "  + connected);
            }
            return connected;
        }
    }
}
