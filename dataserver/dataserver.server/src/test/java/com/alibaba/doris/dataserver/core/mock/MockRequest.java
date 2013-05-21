package com.alibaba.doris.dataserver.core.mock;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.action.Action;
import com.alibaba.doris.dataserver.core.BaseRequest;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MockRequest extends BaseRequest {

    public MockRequest(Action action) {
        super(null, null);
    }

    public String getClientAddress() {
        return null;
    }

    public Key getKey() {
        return null;
    }

    public Value getValue() {
        return null;
    }

    public String getServerAddress() {
        return null;
    }

}
