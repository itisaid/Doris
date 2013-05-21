package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.store.Storage;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class GetAction extends BaseAction {

    public void execute(Request request, Response response) {
        CommonActionData getActionData = (CommonActionData) request.getActionData();
        Storage storage = getStorage(request);
        Key key = request.getKey();
        Value value = storage.get(key);
        if (value != null) {
            getActionData.setKeyBytes(key.getPhysicalKeyBytes());
            getActionData.setVnode(key.getVNode());
            getActionData.setValue(value);
            getActionData.setSuccess(true);
        } else {
            getActionData.setSuccess(false);//
        }

        response.write(getActionData);
    }
}
