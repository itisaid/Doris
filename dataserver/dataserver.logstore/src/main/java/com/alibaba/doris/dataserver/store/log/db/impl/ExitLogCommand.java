package com.alibaba.doris.dataserver.store.log.db.impl;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExitLogCommand extends BaseLogCommand {

    public ExitLogCommand() {
        super(false);
    }

    public Type getType() {
        return Type.EXIT;
    }
}
