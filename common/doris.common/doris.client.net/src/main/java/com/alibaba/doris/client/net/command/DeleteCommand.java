package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.DeleteProtocolParser;
import com.alibaba.doris.common.data.Key;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteCommand extends BaseCommand<Boolean> {

    public DeleteCommand(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public Boolean getResult() {
        return isSuccess();
    }

    private Key                         key;
    private static final ProtocolParser parser = new DeleteProtocolParser();
}
