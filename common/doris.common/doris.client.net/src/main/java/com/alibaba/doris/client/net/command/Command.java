package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.protocol.ProtocolParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Command<V> {

    public void complete();

    public OperationFuture<V> getResultFuture();

    public ProtocolParser getProtocolParser();

    public V getResult();
}
