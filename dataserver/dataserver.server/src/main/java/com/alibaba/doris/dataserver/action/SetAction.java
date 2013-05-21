package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetAction extends BaseAction {

    public void execute(Request request, Response response) {
        CommonActionData actionData = (CommonActionData) request.getActionData();
        Storage storage = getStorage(request);
        try {
            Key key = request.getKey();
            Value value = request.getValue();
            storage.set(key, value, actionData.isCas());
            actionData.setSuccess(true);
        } catch (VersionConflictException e) {
            actionData.setSuccess(false);
        }
        response.write(actionData);
    }
}
