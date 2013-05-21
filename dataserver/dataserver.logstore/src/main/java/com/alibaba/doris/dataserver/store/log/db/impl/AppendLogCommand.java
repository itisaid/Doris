package com.alibaba.doris.dataserver.store.log.db.impl;

import com.alibaba.doris.dataserver.store.log.entry.LogEntry;


/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class AppendLogCommand extends BaseLogCommand {
    public AppendLogCommand(LogEntry logEntry) {
        this(logEntry, true);
    }

    public AppendLogCommand(LogEntry logEntry, boolean isWaitingForCommandCompleted) {
        super(isWaitingForCommandCompleted);
        this.logEntry = logEntry;
    }
    
    public Type getType() {
        return Type.APPEND;
    }
    
    public LogEntry getLogEntry() {
        return logEntry;
    }

    private LogEntry  logEntry;
   
}
