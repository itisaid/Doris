package com.alibaba.doris.dataserver.store.log.db.impl;


/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteLogByTimestampCommand extends BaseLogCommand {
    public DeleteLogByTimestampCommand(long timestampBefore) {
        super(true);
        this.timestampBefore = timestampBefore;
    }
    
    public Type getType() {
        return Type.DELETE_BY_TIMESTAMP;
    }

    public long getTimestamp() {
        return this.timestampBefore;
    }

    private long timestampBefore;
}
