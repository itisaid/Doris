package com.alibaba.doris.dataserver.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleStatusChecker;
import com.alibaba.doris.dataserver.action.data.CheckActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckAction implements Action {

    public void execute(Request request, Response response) {
        CheckActionData actionData = (CheckActionData) request.getActionData();

        ApplicationContext appContext = request.getApplicationContext();
        List<Module> moduleList = appContext.getModules();

        actionData.setSuccess(true);

        for (Module m : moduleList) {
            if (m instanceof ModuleStatusChecker) {
                if (!((ModuleStatusChecker) m).isReady(actionData)) {
                    actionData.setSuccess(false);
                    String moduleName = m.getName();
                    moduleName = StringUtils.replace(moduleName, " ", "_");
                    actionData.setMessage(moduleName + "_failed");
                    break;
                }
            }
        }

        response.write(actionData);
    }
}
