package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.CompareAndDeleteProtocolParser;
import com.alibaba.doris.common.data.Key;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndDeleteCommand extends DeleteCommand {

    public CompareAndDeleteCommand(Key key, long timestamp) {
        super(key);
        this.timestamp = timestamp;
    }

    @Override
    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private long                  timestamp;
    private static ProtocolParser parser = new CompareAndDeleteProtocolParser();
}
