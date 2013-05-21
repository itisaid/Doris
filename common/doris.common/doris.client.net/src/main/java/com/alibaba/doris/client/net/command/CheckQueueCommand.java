package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.CheckQueueProtocolParser;

/**
 * 清除当前发送队列中的所有数据；模拟连续发送两个check命令；
 * 
 * @author ajun
 */
public class CheckQueueCommand extends CheckCommand {

    public CheckQueueCommand() {
        super(null);
    }

    @Override
    public ProtocolParser getProtocolParser() {
        if (null == parser) {
            parser = new CheckQueueProtocolParser();
        }
        return parser;
    }

    public long getDiscardBytes() {
        if (null == parser) {
            return 0;
        }

        return parser.getDiscardBytes();
    }

    private CheckQueueProtocolParser parser;
}
