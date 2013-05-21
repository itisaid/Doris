package com.alibaba.doris.dataserver.store.log.db;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface WriteWindowRef extends WriteWindow {

    public void incrementReference();

    public void releaseReference();

    public WriteWindow getWriteWindow();
}
