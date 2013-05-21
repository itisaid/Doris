package com.alibaba.doris.admin.service.common.migrate.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.migrate.MigrateSubCommand;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.fastjson.JSON;

/**
 * 迁移命令
 * 
 * @author frank
 */
public class MigrateCommand {

    private static final Log    log = LogFactory.getLog(MigrateCommand.class);
    private static final String E   = "execute";
    private static final String F   = "finish";

    /**
     * 执行迁移命令
     * 
     * @param srourcePhysicalId 迁出节点物理Id
     * @param targetPhysicalIdList 迁入节点物理Id列表，失效恢复的时候size为1
     * @param migerateType 迁移类型
     * @return true:发送成功，false：发送失败，null：不需要发送
     */
    public static MigrateCommandResult executeMigerate(String sourcePhysicalId, List<String> targetPhysicalIdList,
                                                       MigrateTypeEnum migrateType) {
        String param = buildParam(sourcePhysicalId, targetPhysicalIdList, migrateType);
        MigrateCommandResult mcr = new MigrateCommandResult();
        mcr.setCommandParam(param);
        if (param != null) {
            if (log.isInfoEnabled()) {
                log.info("send migerate command " + migrateType + " about " + sourcePhysicalId + " -----> "
                         + targetPhysicalIdList);
            }

            boolean migReturn = sendMigrateCommand(sourcePhysicalId, migrateType, param, E);
            mcr.setResult(migReturn);
            if (!migReturn) {
                SystemLogMonitor.error(MonitorEnum.MIGRATION, MonitorWarnConstants.FAIL_TO_SEND_MIGRATE_COMMAND + ":"
                                                              + sourcePhysicalId + " -> " + targetPhysicalIdList);
            }

        } else {
            if (log.isInfoEnabled()) {
                log.info("NO PRARMETER NEED send migerate command " + migrateType + " about " + sourcePhysicalId
                         + " -----> " + targetPhysicalIdList);

            }

        }
        return mcr;
    }

    private static boolean sendMigrateCommand(String sourcePhysicalId, MigrateTypeEnum migrateType, String param,
                                              String ef) {

        try {

            Connection con = NodesManager.getInstance().getNodeConnection(sourcePhysicalId, false);

            if (con == null) {
                return false;
            }

            if (log.isDebugEnabled()) {
                log.debug("The connection got for source node :" + sourcePhysicalId + " is " + con.toString()
                          + ", para =" + param);
            }
            OperationFuture<String> future = null;

            if (MigrateTypeEnum.EXPANSION.equals(migrateType)) {
                if (E.equals(ef)) {
                    future = con.migrate(MigrateSubCommand.EXPANSION_START.getValue(), param);
                }
                if (F.equals(ef)) {
                    future = con.migrate(MigrateSubCommand.EXPANSION_ALL_FINISHED.getValue(), param);
                }
            }
            if (MigrateTypeEnum.TEMP_FAILOVER.equals(migrateType)) {
                if (E.equals(ef)) {
                    future = con.migrate(MigrateSubCommand.TEMP_FAILOVER_START.getValue(), param);
                }
                if (F.equals(ef)) {
                    future = con.migrate(MigrateSubCommand.TEMP_FAILOVER_ALL_FINISHED.getValue(), param);
                }
            }
            if (MigrateTypeEnum.FOREVER_FAILOVER.equals(migrateType)) {
                if (E.equals(ef)) {
                    future = con.migrate(MigrateSubCommand.FOREVER_FAILOVER_START.getValue(), param);
                }
                if (F.equals(ef)) {
                    cleanTempData(param);//clean 临时节点失效期间的数据，临时节点还担负一个使命：更新路由，而且一定要先于对等节点更新（这行代码在下面代码的上面，㤯）
                    future = con.migrate(MigrateSubCommand.FOREVER_FAILOVER_ALL_FINISHED.getValue(), param);
                }
            }
            if (future == null) {
                return false;
            }
            String result;
            try {
                result = future.get(3000, TimeUnit.MILLISECONDS);
                if (log.isInfoEnabled()) {
                    log.info("message from data server after sent migrate command:" + result);
                }
            } catch (TimeoutException e) {
                if (log.isErrorEnabled()) {
                    log.error(e);
                }
                return false;
            }

        } catch (InterruptedException e) {
            if (log.isErrorEnabled()) {
                log.error(e);
            }
            return false;
        } catch (ExecutionException e) {
            if (log.isErrorEnabled()) {
                log.error(e);
            }
            return false;
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error(e);
            }
            return false;
        }

        return true;
    }

    /**
     * 清除永久失效恢复完成后对应的临时节点的数据
     * 
     * @param param
     * @return
     */
    public static boolean cleanTempData(String param) {
        boolean result = true;
        List<PhysicalNodeDO> tempNodeList = NodesManager.getInstance().getAllTempNodeList();
        for (PhysicalNodeDO node : tempNodeList) {
            String nodeId = node.getPhysicalId();
            try {
                Connection con = NodesManager.getInstance().getNodeConnection(nodeId, false);
                OperationFuture<String> future = con.migrate(MigrateSubCommand.DATACLEAN.getValue(), param);
                future.get(3000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e);
                }
                result = false;
                SystemLogMonitor.error(MonitorEnum.MIGRATION, MonitorWarnConstants.FAIL_TO_SEND_CLEAN_DATA_COMMAND
                                                              + ":" + nodeId, e);
                continue;
            }
        }
        return result;
    }

    /**
     * 迁移结束命令
     * 
     * @param sourcePhysicalId
     * @param targetPhysicalIdList
     * @param migrateType
     * @return
     */
    public static boolean finishMigerate(String sourcePhysicalId, List<String> targetPhysicalIdList, String param,
                                         MigrateTypeEnum migrateType) {
        if (param != null) {
            if (log.isInfoEnabled()) {
                log.info("send migerate finish command " + migrateType + " about " + sourcePhysicalId + " -----> "
                         + targetPhysicalIdList);
            }

            boolean migReturn = sendMigrateCommand(sourcePhysicalId, migrateType, param, F);

            if (!migReturn) {
                SystemLogMonitor.error(MonitorEnum.MIGRATION, MonitorWarnConstants.FAIL_TO_SEND_MIGRATE_FINISH_COMMAND
                                                              + ":" + sourcePhysicalId + " -> " + targetPhysicalIdList);
            }
            return migReturn;
        }
        if (log.isInfoEnabled()) {
            log.info("NO PRARMETER NEED send migerate finish command " + migrateType + " about " + sourcePhysicalId
                     + " -----> " + targetPhysicalIdList);

        }
        return false;
    }

    private static String buildParam(String sourcePhysicalId, List<String> targetPhysicalIdList,
                                     MigrateTypeEnum migrateType) {
        List<MigrationRoutePair> pairList = null;
        if (migrateType.equals(MigrateTypeEnum.EXPANSION)) {
            pairList = CommandParser.parseExpansionCommand(sourcePhysicalId, targetPhysicalIdList);
        }
        if (migrateType.equals(MigrateTypeEnum.TEMP_FAILOVER) || migrateType.equals(MigrateTypeEnum.FOREVER_FAILOVER)) {
            pairList = CommandParser.parseFailCommand(sourcePhysicalId, targetPhysicalIdList.get(0));
        }
        if (pairList == null || pairList.isEmpty()) {
            return null;
        }
        String param = JSON.toJSONString(pairList);
        if (log.isInfoEnabled()) {
            log.info("command ---" + param);
        }
        return param;
    }

    public static void main(String[] args) {
        List<MigrationRoutePair> l = new ArrayList<MigrationRoutePair>();
        MigrationRoutePair p = new MigrationRoutePair();
        p.setTargetPhysicalId("x");
        p.setVnode(2);
        l.add(p);
        p = new MigrationRoutePair();
        p.setTargetPhysicalId("y");
        p.setVnode(4);
        l.add(p);
        String s = JSON.toJSONString(l);
        System.out.println(s);
        List<MigrationRoutePair> l1 = JSON.parseArray(s, MigrationRoutePair.class);
        System.out.println(l1);

    }
}
