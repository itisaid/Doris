package com.alibaba.doris.admin.web.configer.module.mock;

import java.util.Random;

import com.alibaba.doris.admin.service.failover.node.check.NodeAdminStatusWrapper;
import com.alibaba.doris.admin.service.failover.node.check.NodeHealth;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @project :
 * @author : len.liu
 * @datetime : 2011-5-26 下午04:21:23
 * @version :
 * @Modification:
 */
public class NodeAdminStatusWrapperMock {

    /**
     * @param sequence
     * @return
     */
    public static NodeAdminStatusWrapper getNodeAdminStatus(StoreNodeSequenceEnum sequence) {
        NodeAdminStatusWrapper wrapper = new NodeAdminStatusWrapper();
        if (StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.equals(sequence)) {
            wrapper.setMigrateStatus(MigrateStatusEnum.MIGERATING);
            wrapper.setMigrateStatusDetail("Befor 10ms, completed 20%");

        } else if (StoreNodeSequenceEnum.NORMAL_SEQUENCE_2.equals(sequence)) {
            wrapper.setMigrateStatus(null);
            wrapper.setMigrateStatusDetail(null);
        }

        wrapper.setNodeHealth(getNodeHealth());
        wrapper.setNodeRouteStatus(getNodeRouteStatus());

        return wrapper;
    }

    public static MigrateStatusEnum getMigrateStatus(int sequence) {
        if (StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.getValue() == sequence) {
            return MigrateStatusEnum.MIGERATING;
        } else {
            return null;
        }
    }

    public static String getMigrateStatusDetail(int sequence) {
        if (StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.getValue() == sequence) {
            return "Befor " + new Random().nextInt(80) + "ms, migrate action is complietd " + new Random().nextInt(100)
                   + "%";
        } else {
            return null;
        }
    }

    public static NodeHealth getNodeHealth() {
        long now = System.currentTimeMillis();
        if (now % 2 == 0) return NodeHealth.OK;
        else return NodeHealth.NG;
    }

    public static NodeRouteStatus getNodeRouteStatus() {
        long now = System.currentTimeMillis();
        if (now % 7 != 0) return NodeRouteStatus.OK;
        else return NodeRouteStatus.TEMP_FAILED;
    }
}
