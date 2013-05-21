package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.dataserver.action.data.CompareAndDeleteActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.action.data.SimpleActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteAction extends BaseAction {

    public void execute(Request request, Response response) {
        SimpleActionData actionData = (SimpleActionData) request.getActionData();
        try {
            Storage storage = getStorage(request);
            Key key = request.getKey();
            boolean bResult = false;
            if (actionData.isCas()) {
                CompareAndDeleteActionData deleteCas = (CompareAndDeleteActionData) actionData;
                Value value = new ValueImpl(null, deleteCas.getTimestamp());
                bResult = storage.delete(key, value);
            } else {
                bResult = storage.delete(key);
            }
            actionData.setSuccess(bResult);
        } catch (VersionConflictException e) {
            actionData.setSuccess(false);
            if (actionData.isCas()) {
                CompareAndDeleteActionData deleteCas = (CompareAndDeleteActionData) actionData;
                deleteCas.setDeleteFailed(true);
            }
        } catch (Exception e) {
            ErrorActionData ad = new ErrorActionData(ErrorActionData.SERVER_ERROR);
            ad.setErrorMessage("Execute " + actionData + " failed. Error message:" + e.getMessage());
            response.write(ad);
            return;
        }

        response.write(actionData);
    }

}
