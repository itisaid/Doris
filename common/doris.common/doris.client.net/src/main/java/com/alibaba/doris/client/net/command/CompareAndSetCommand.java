package com.alibaba.doris.client.net.command;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndSetCommand extends SetCommand {

    public CompareAndSetCommand(Key key, Value value) {
        super(key, value);
    }

    @Override
    public boolean isCas() {
        return true;
    }
}
