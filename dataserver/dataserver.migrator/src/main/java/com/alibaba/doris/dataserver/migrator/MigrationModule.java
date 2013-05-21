/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.route.VirtualRouter;
import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.ModuleContextAware;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.migrator.action.MigrationAction;
import com.alibaba.doris.dataserver.migrator.report.DefaultMigrationReporter;
import com.alibaba.doris.dataserver.migrator.report.MigrationReporter;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageModule;

/**
 * MigrationModule,迁移模块.
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-24
 */
public class MigrationModule extends BaseModule implements ModuleContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    /**
     * 加载配置和初始化迁移Module
     */
    public void load(ModuleConfigure conf) {
        // extensible action
        MigrationAction migrationAction = new MigrationAction();

        MigrationManager migrationManager = new MigrationManager();

        MigrationReporter migrationReporter = null;

        if (conf.getParam("migration.reporter.class") != null) {
            String className = conf.getParam("migration.reporter.class");
            Class<?> clazz;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                migrationReporter = (MigrationReporter) clazz.newInstance();

            } catch (Exception e) {
                logger.warn("MigrationReporter class not found or invalid! Use default instead." + className);
            }
        }

        Properties commandLine = conf.getDataServerConfigure().getCommandLine();
        if (commandLine != null) {
            String migrateThreads = commandLine.getProperty("migrateThreads");
            if (StringUtils.isNotBlank(migrateThreads)) {
                migrationManager.setMigrateThreads(Integer.valueOf(migrateThreads));
            }
        }

        if (migrationReporter == null) {
            migrationReporter = new DefaultMigrationReporter();

            migrationReporter.setMigrateReportService(AdminServiceFactory.getMigrateReportService());
            migrationReporter.setPostMigrateReportService(AdminServiceFactory.getPostMigrateReportService());
        }

        VirtualRouter virtualRouter = null;
        String virtualRouterClass = conf.getParam("vitualrouter.class");
        if (virtualRouterClass != null) {
            Class<?> clazz;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(virtualRouterClass);
                virtualRouter = (VirtualRouter) clazz.newInstance();

            } catch (Exception e) {
                logger.warn("VirtualRouter class not found or invalid! Use default instead." + virtualRouterClass
                            + ". Cause: " + e);
            }
        }

        if (virtualRouter == null) {
            virtualRouter = new VirtualRouterImpl();
        }

        ModuleContext moduleContext = getModuleContext();
        ApplicationContext appContext = moduleContext.getApplicationContext();

        // 获取存储模块
        StorageModule storageModule = (StorageModule) appContext.getModuleByName(ModuleConstances.STORAGE_MODULE);
        Storage storage = storageModule.getStorage();

        migrationManager.setMigrationReporter(migrationReporter);
        migrationManager.setVirtualRouter(virtualRouter);
        migrationManager.setStorage(storage);

        // 获取配置管理器
        ConfigManager configManager = (ConfigManager) appContext.getAttribute("configManager");
        migrationManager.setConfigManager(configManager);

        // 获取路由表管理器
        RouteTableConfiger routeTableConfiger = (RouteTableConfiger) appContext.getAttribute("routeTableConfiger");
        migrationManager.setRouteTableConfiger(routeTableConfiger);

        // 在模块容器中，保存迁移管理器；
        moduleContext.setAttribute(MigrationManager._MigrationManager, migrationManager);

        // 注册迁移指令
        ActionFactory.registAction(MigrateActionType.MIGRATE, migrationAction);
    }

    /**
     * 卸载
     */
    public void unload() {
        ModuleContext moduleContext = getModuleContext();
        logger.info("Unload MigrationModule. Port:" + moduleContext.getApplicationContext().getAttribute("serverPort"));
    }
}
