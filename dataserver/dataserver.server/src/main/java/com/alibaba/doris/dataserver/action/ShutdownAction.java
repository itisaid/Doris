package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.event.server.ShutdownEvent;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ShutdownAction extends BaseAction implements Runnable {

    public void execute(Request request, Response response) {
        appContext = request.getApplicationContext();
        Thread exitThread = new Thread(this);
        exitThread.start();
        response.close();
    }

    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
            ;
        }
        // 触发一个关机事件。
        appContext.getEventListenerManager().fireEvent(new ShutdownEvent());
    }

    private ApplicationContext appContext;
}
