package com.alibaba.doris.admin.service.common.migrate.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.admin.service.common.migrate.status.PostMigrateStatus;
import com.alibaba.doris.common.MigrateStatusEnum;

/**
 * 迁移后处理过程状态管理器
 * 
 * @author frank
 */
public class PostMigrateStatusManager {

    private static PostMigrateStatusManager instance             = new PostMigrateStatusManager();
    private Map<String, PostMigrateStatus>  postMigrateStatusMap = new ConcurrentHashMap<String, PostMigrateStatus>();

    private PostMigrateStatusManager() {
    }

    public static PostMigrateStatusManager getInstance() {
        return instance;
    }

    public void updatePostMigrateStatus(String physicalId, int schedule, MigrateStatusEnum status, String message) {
        if (MigrateStatusEnum.DATACLEAN_FINISH.equals(status)) {
            postMigrateStatusMap.remove(physicalId);
            return;
        }
        PostMigrateStatus postMigrateStatus = new PostMigrateStatus();
        postMigrateStatus.setMessage(message);
        postMigrateStatus.setPhysicalId(physicalId);
        postMigrateStatus.setSchedule(schedule);
        postMigrateStatus.setStatus(status);
        postMigrateStatusMap.put(physicalId, postMigrateStatus);
    }

    public PostMigrateStatus getPostMigrateStatus(String physicalId) {
        return postMigrateStatusMap.get(physicalId);
    }
}
