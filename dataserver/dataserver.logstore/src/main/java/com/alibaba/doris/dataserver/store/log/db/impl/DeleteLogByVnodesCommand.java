package com.alibaba.doris.dataserver.store.log.db.impl;

import java.util.List;

import com.alibaba.doris.dataserver.store.log.db.LogClump;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteLogByVnodesCommand extends BaseLogCommand {

    public DeleteLogByVnodesCommand(List<Integer> vnodeList) {
        super(true);
        this.vnodeList = vnodeList;
    }

    public Type getType() {
        return Type.DELETE_BY_VNODES;
    }

    public List<Integer> getVnodeList() {
        return vnodeList;
    }

    public LogClump[] getLogClumps() {
        return logClumps;
    }

    public void setLogClumps(LogClump[] logClumps) {
        this.logClumps = logClumps;
    }

    private List<Integer> vnodeList;
    private LogClump[]    logClumps;
}
