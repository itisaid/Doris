package com.alibaba.doris.admin.web.configer.module.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;

public class WebConsoleAction {

    public void doSendCommand(HttpServletRequest request, Navigator nav, Context context) {
        String command = request.getParameter("command");
        if (StringUtils.isBlank(command)) {
            context.put("errorResult", "请输入操作命令");
            return;
        }
        String[] comm = command.split(" ");
        if (comm == null || comm.length < 2) {
            context.put("errorResult", "命令不规范，请检查操作命令");
            return;
        }
        // set mao:mao1 1 1111111 5
        // get mao:mao1
        // XXME 需要解决webconsole中的客户端配置问题
        DataStoreFactory dataStoreFactory = getDataStoreFactory();
        if ("get".equals(comm[0])) {
            if (comm.length != 2 || comm[1].indexOf(":") < 1 || comm[1].indexOf(":") > (comm[1].length() - 1)) {
                context.put("errorResult", "get命令不规范，请检查操作命令");
                return;
            }
            String namespace = comm[1].split(":")[0];
            String key = comm[1].split(":")[1];
            DataStore dataStore = dataStoreFactory.getDataStore(namespace);
            Object object = dataStore.get(key);
            context.put("result", object);
        } else if ("delete".equals(comm[0])) {
            if (comm.length != 2 || comm[1].indexOf(":") < 1 || comm[1].indexOf(":") > (comm[1].length() - 1)) {
                context.put("errorResult", "get命令不规范，请检查操作命令");
                return;
            }
            String namespace = comm[1].split(":")[0];
            String key = comm[1].split(":")[1];
            DataStore dataStore = dataStoreFactory.getDataStore(namespace);
            dataStore.delete(key);
            context.put("errorResult", "delete成功");
        } else if ("set".equals(comm[0])) {
            // set m:m|这已经是最简单的命令了（回车之前的部分）
            int enterIndex = command.indexOf("\r\n");
            String setCommand = command.substring(0, enterIndex);
            String[] setComm = setCommand.split(" ");
            String setValue = command.substring(enterIndex + 2);
            if (setComm.length != 2 || setComm[1].indexOf(":") < 1
                || setComm[1].indexOf(":") > (setComm[1].length() - 1)) {
                context.put("errorResult", "set命令不规范，请检查操作命令");
                return;
            }
            String namespace = setComm[1].split(":")[0];
            String key = setComm[1].split(":")[1];
            DataStore dataStore = dataStoreFactory.getDataStore(namespace);
            dataStore.put(key, setValue);
            context.put("errorResult", "PUT 成功");
        } else {
            context.put("errorResult", "不支持的命令，现在仅支持get,put,delete，请检查操作命令");
            return;
        }
        return;

    }

    private DataStoreFactory getDataStoreFactory() {
        synchronized (WebConsoleAction.class) {
            if (null == dataStoreFactory) {
                String configUrl = "webconsole.properties";
                dataStoreFactory = new DataStoreFactoryImpl(configUrl);
            }

            return dataStoreFactory;
        }
    }

    private static DataStoreFactory dataStoreFactory;
}
