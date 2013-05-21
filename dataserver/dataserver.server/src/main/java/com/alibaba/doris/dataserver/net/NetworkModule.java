package com.alibaba.doris.dataserver.net;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.ModuleContextAware;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.action.CatchCommandErrorAction;
import com.alibaba.doris.dataserver.action.CheckAction;
import com.alibaba.doris.dataserver.action.DeleteAction;
import com.alibaba.doris.dataserver.action.ExitServerAction;
import com.alibaba.doris.dataserver.action.GetAction;
import com.alibaba.doris.dataserver.action.SetAction;
import com.alibaba.doris.dataserver.action.ShutdownAction;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.config.data.FilterConfigure;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.core.RequestFilterChainFactory;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.net.netty.CollectConnectionInfoHandler;
import com.alibaba.doris.dataserver.net.netty.NettyDataServerChannelPipelineFactory;

/**
 * 服务器端网络通信模块，负责对Client发出的请求进行响应，<br>
 * 并将请求包装成相应的Request和Response对象。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NetworkModule extends BaseModule implements ModuleContextAware {

    public void load(ModuleConfigure conf) {
        networkModuleConfigure = conf;
        initAction();
        initFilter(conf);
        initNetwork();
    }

    protected void initNetwork() {
        // 看看命令行输入参数是否带有Port端口，如果带有Port端口，则优先使用。
        Properties commandLine = networkModuleConfigure.getDataServerConfigure().getCommandLine();
        String commandLineOfPort = commandLine.getProperty("port");
        if (StringUtils.isBlank(commandLineOfPort)) {
            port = networkModuleConfigure.getParamAsInt("port", DEFAULT_SERVER_PORT);
        } else {
            port = Integer.valueOf(commandLineOfPort);
        }

        maxConnections = networkModuleConfigure.getParamAsInt(MAX_ACTIVE_CONNECTIONS, DEFAULT_MAX_ACTIVE_CONNECTIONS);

        int threads = networkModuleConfigure.getParamAsInt("threads", DEFAULT_SERVER_THREADS);
        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                                                                          Executors.newCachedThreadPool(), threads));

        // Set up the event pipeline factory.
        NettyDataServerChannelPipelineFactory pipelineFactory = new NettyDataServerChannelPipelineFactory(appContext);

        bootstrap.setPipelineFactory(pipelineFactory);
        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));

        ModuleContext moduleContext = getModuleContext();
        if (null != moduleContext) {
            moduleContext.setAttribute("serverPort", getPort());
            appContext.setAttribute("serverPort", getPort());
        }

        System.out.println("DataServer is listening on port :" + port);
    }

    protected void initAction() {
        // May be we should load these actions from configure file.
        ActionFactory.registAction(BaseActionType.SET, new SetAction());
        ActionFactory.registAction(BaseActionType.CAS, new SetAction());
        ActionFactory.registAction(BaseActionType.GET, new GetAction());
        ActionFactory.registAction(BaseActionType.DELETE, new DeleteAction());
        ActionFactory.registAction(BaseActionType.CAD, new DeleteAction());
        ActionFactory.registAction(BaseActionType.ERROR, new CatchCommandErrorAction());
        ActionFactory.registAction(BaseActionType.EXIT, new ExitServerAction());
        ActionFactory.registAction(BaseActionType.SHUTDOWN, new ShutdownAction());
        ActionFactory.registAction(BaseActionType.CHECK, new CheckAction());
    }

    /**
     * 从配置文件中装载所有的RequestFilter
     * 
     * @param conf
     */
    protected void initFilter(ModuleConfigure conf) {
        List<FilterConfigure> filterConfigList = conf.getFilterConfigList();
        RequestFilterChainFactory factory = new RequestFilterChainFactory();
        factory.loadRequestFiltersFromConfigure(filterConfigList);
        filterChian = factory.getFilterChian();
    }

    public RequestFilterChian getRequestFilterChain() {
        return filterChian;
    }

    public void unload() {
        NettyDataServerChannelPipelineFactory factory = (NettyDataServerChannelPipelineFactory) bootstrap.getPipelineFactory();
        CollectConnectionInfoHandler handler = factory.getCollectConnectionInfoHandler();
        if (null != handler) {
            ChannelGroup group = handler.getActiveChannels();
            group.disconnect().awaitUninterruptibly();
            group.close().awaitUninterruptibly();
        }
        bootstrap.releaseExternalResources();
    }

    public void setModuleContext(ModuleContext moduleContext) {
        super.setModuleContext(moduleContext);
        this.appContext = moduleContext.getApplicationContext();
    }

    public int getPort() {
        return port;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public String toString() {
        return getName() + " DataServer Listening on port:" + port;
    }

    private int                port;
    private int                maxConnections;
    private ServerBootstrap    bootstrap;

    private ModuleConfigure    networkModuleConfigure;
    private ApplicationContext appContext;
    private RequestFilterChian filterChian;
    private static final int   DEFAULT_SERVER_PORT            = 8000;
    private static final int   DEFAULT_SERVER_THREADS         = 20;
    private static final int   DEFAULT_MAX_ACTIVE_CONNECTIONS = 10000;
    public static final String MAX_ACTIVE_CONNECTIONS         = "maxConnections";
}
