package com.alibaba.doris;

import java.net.BindException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

public class JettyServer {
	private static final Log log = LogFactory.getLog(JettyServer.class);

	private int port = 8118;
	private String host = "127.0.0.1";
	private final int lowThreads = 12;
	private int maxThreads = 15;
	private Server server;
	private String rootUrl;

	public synchronized String getRootUrl() {
		return rootUrl;
	}

	public synchronized boolean isServerStarted() {
		if (server != null) {
			return server.isStarted();
		}
		return false;
	}

	private void tryToStart(Server server, int reTryCount) throws Exception{
		final Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setHost(host);
		server.setConnectors(new Connector[] { connector });
		try{
			server.start();
		}catch (final BindException e) {
			if(reTryCount > 0){
				connector.close();
				port++;
				tryToStart(server, reTryCount - 1);
			}else{
				throw e;
			}
		}
	}

	public synchronized void startServer(HttpServlet servlet) throws Exception {
		stopServer();
		server = new Server();
		
		final ExecutorThreadPool threadPool = new ExecutorThreadPool(lowThreads, maxThreads, 60, TimeUnit.SECONDS);
		server.setThreadPool(threadPool);
		
		HandlerCollection hc = new HandlerCollection(true);

		ServletContextHandler root = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		root.setContextPath("/");
		hc.addHandler(root);
		
		server.setHandler(hc);
		
        root.addServlet(new ServletHolder(servlet), "/doris.config");

		server.setSendServerVersion(false);
		tryToStart(server, 10);
		rootUrl = "http://" + host + (getPort() == 80 ? "" : ":" + getPort()) + "/";
		if(log.isInfoEnabled()){
			log.info("Jetty Http server start in:" + rootUrl);
		}
	}

	public synchronized boolean stopServer() throws Exception {
		if (server != null && !server.isFailed() && !server.isStarting() && !server.isStopped()) {
			server.stop();
			server.join();
			server.destroy();
			server = null;
			rootUrl = null;
	        if(log.isInfoEnabled()){
	            log.info("Jetty Http server stop in:" + rootUrl);
	        }
			return true;
		}
		return false;
	}

	public String getServerName() {
		return "Jetty Http Server";
	}

	/**
	 * @return the port
	 */
	public synchronized int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public synchronized void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * @param maxThreads the maxThreads to set
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}
}
