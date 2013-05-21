package com.alibaba.doris.dataserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.config.ConfigureLoaderException;
import com.alibaba.doris.dataserver.config.DataServerConfigure;
import com.alibaba.doris.dataserver.config.XMLDataServerConfigureLoader;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.core.DefaultApplicationContext;
import com.alibaba.doris.dataserver.core.DefaultModuleContext;
import com.alibaba.doris.dataserver.event.EventListenerManager;
import com.alibaba.doris.dataserver.event.server.DataServerEventListener;
import com.alibaba.doris.dataserver.event.server.ShutdownEvent;
import com.alibaba.doris.dataserver.event.server.StartupEvent;
import com.alibaba.doris.dataserver.tools.DataServerJVMShutdownHook;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerApp implements DataServerEventListener {

    public DataServerApp() {

    }

    public DataServerApp(String[] args) {
        try {
            DataServerConfigure conf = processArguments(args);
            loadModules(conf);
            initApplicationContext(conf);
            // initializing all modules.
            initModules(conf);
            EventListenerManager eventListenerManager = appContext.getEventListenerManager();
            eventListenerManager.registEventListener(this);
            // Everything is okay, now we send an event to notify all listener that the data server has started.
            eventListenerManager.fireEvent(new StartupEvent());
            addJVMHook();
        } catch (Throwable e) {
            logger.error("Start DataServer failed!", e);
            // To kill all threads.
            System.exit(0);
        } finally {
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new DataServerApp(args);
    }

    /**
     * 从配置文件中装载所有的Module
     * 
     * @param conf
     */
    public void loadModules(DataServerConfigure conf) {
        List<ModuleConfigure> moduleConfigList = conf.getModuleConfigList();
        for (ModuleConfigure moduleConfig : moduleConfigList) {
            Module module = createModule(moduleConfig);
            if (module instanceof BaseModule) {
                ((BaseModule) module).setName(moduleConfig.getName());
            }
            modules.add(module);
        }
    }

    private DataServerConfigure processArguments(String[] args) {
        Properties commandLine = parseCommandLineArguments(args);
        DataServerConfigure conf = loadDataServerConfigure(commandLine.getProperty(CONFIG_ITEM_CONFIG_FILE));
        conf.setCommandLine(commandLine);
        return conf;
    }

    private Properties parseCommandLineArguments(String[] args) {
        String configureFile = DEFAULT_CONFIGURE_FILE;
        Properties commandLine = new Properties();
        try {
            String logRoot = DEFAULT_LOG_ROOT;
            String logLevel = DEFAULT_LOGLEVEL;
            if (args.length > 0) {
                for (int i = 0; i < args.length; i += 2) {
                    String name = args[i];
                    String value = null;
                    if (args.length > (i + 1)) {
                        value = args[i + 1];
                    }

                    if (StringUtils.isBlank(name)) {
                        continue;
                    }

                    // -help 去掉前面的“-”
                    name = name.substring(1);
                    commandLine.setProperty(name, value);
                    if (CONFIG_ITEM_CONFIG_FILE.equalsIgnoreCase(name)) {
                        configureFile = value;
                    } else if (CONFIG_ITEM_HELP.equalsIgnoreCase(name)) {
                        printUsage();
                    } else if (CONFIG_ITEM_LOG_ROOT.equals(name)) {
                        logRoot = value;
                        System.out.println("Use logConfig:" + logRoot);
                    } else if (CONFIG_ITEM_LOG_LEVEL.equals(name)) {
                        logLevel = value;
                        System.out.println("Use logLevel:" + logLevel);
                    }
                }
            }

            System.setProperty("logRoot", logRoot);
            System.setProperty("logLevel", logLevel);
            logger = LoggerFactory.getLogger(DataServerApp.class);
        } catch (Throwable e) {
            printUsage();
            System.exit(0);
        }

        commandLine.setProperty(CONFIG_ITEM_CONFIG_FILE, configureFile);
        return commandLine;
    }

    private DataServerConfigure loadDataServerConfigure(String configureFile) {
        try {
            XMLDataServerConfigureLoader loader = new XMLDataServerConfigureLoader(configureFile);
            return loader.load();
        } catch (DocumentException e) {
            throw new ConfigureLoaderException(e);
        }
    }

    private void addJVMHook() {
        DataServerJVMShutdownHook hook = new DataServerJVMShutdownHook(appContext);
        appContext.getEventListenerManager().registEventListener(hook);
        Runtime.getRuntime().addShutdownHook(hook);
    }

    private void initApplicationContext(DataServerConfigure conf) {
        this.appContext = new DefaultApplicationContext(modules);
        this.appContext.setEventListenerManager(new EventListenerManager());
        this.appContext.setDataServerConfigure(conf);
    }

    private static void printUsage() {
        System.out.println("Usage: -configFile, setting the config file of Data server.");
        System.out.println("       -port , setting the listening port. Forexample: -port 8080");
        System.exit(0);
    }

    private Module createModule(ModuleConfigure moduleConfig) {
        String moduleClassName = moduleConfig.getClassName();
        Module module = null;
        try {
            if (StringUtils.isNotBlank(moduleClassName)) {
                ClassLoader classLoader = this.getClass().getClassLoader();
                Class<?> clazz = classLoader.loadClass(moduleClassName);
                module = (Module) clazz.newInstance();
            }
        } catch (Exception e) {
            throw new FatalModuleInitializationException("Creating module failed! Module name:" + moduleClassName, e);
        }
        return module;
    }

    /**
     * Initializing all modules.
     * 
     * @param conf
     */
    public void initModules(DataServerConfigure conf) {
        for (Module module : modules) {
            long startTime = System.currentTimeMillis();
            ModuleContext moduleContext = new DefaultModuleContext(appContext);

            appContext.addModuleContext(module, moduleContext);
            if (module instanceof ModuleContextAware) {
                ModuleContextAware mca = (ModuleContextAware) module;
                mca.setModuleContext(moduleContext);
            }

            module.load(conf.getModuleConfigure(module.getClass().getName()));
            if (logger.isInfoEnabled()) {
                logger.info("Loading " + module.getName() + " SUCCESS. Time consuming(ms):"
                            + (System.currentTimeMillis() - startTime));
            }
        }
    }

    public void destoryModules() {
        int moduleCount = modules.size();
        for (int index = moduleCount; index > 0; index--) {
            long startTime = System.currentTimeMillis();
            Module module = modules.get(index - 1);
            try {
                module.unload();
                if (logger.isInfoEnabled()) {
                    logger.info("Unloading " + module.getName() + " SUCCESS. Time consuming(ms):"
                                + (System.currentTimeMillis() - startTime));
                }
            } catch (Throwable e) {
                // 出现异常，只影响某个模块的卸载。
                logger.error("Unloading " + module.getName() + " FAILURE!", e);
            }
        }
    }

    public void onShutdown() {
        destoryModules();
    }

    public void onStartup() {
        System.out.println("DataServer is ready!");

        if (logger.isDebugEnabled()) {
            logger.debug("DataServer is ready!");
        }
    }

    public void shutdown() {
        EventListenerManager eventListenerManager = appContext.getEventListenerManager();
        eventListenerManager.fireEvent(new ShutdownEvent());
    }

    private static Logger             logger;
    private DefaultApplicationContext appContext;
    private List<Module>              modules                 = new ArrayList<Module>();
    private static final String       DEFAULT_CONFIGURE_FILE  = "dataserver.xml";
    private static final String       DEFAULT_LOG_ROOT        = "log";
    private static final String       DEFAULT_LOGLEVEL        = "INFO";
    private static final String       CONFIG_ITEM_CONFIG_FILE = "configFile";
    private static final String       CONFIG_ITEM_LOG_ROOT    = "logRoot";
    private static final String       CONFIG_ITEM_LOG_LEVEL   = "logLevel";
    private static final String       CONFIG_ITEM_HELP        = "help";
}
