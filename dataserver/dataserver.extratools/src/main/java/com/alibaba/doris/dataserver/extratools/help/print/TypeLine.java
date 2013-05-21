package com.alibaba.doris.dataserver.extratools.help.print;

import com.alibaba.doris.dataserver.core.Response;

/**
 * 实现逐行打印的效果。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class TypeLine extends Line {

    public TypeLine(String line) {
        super(line);
    }

    @Override
    public void print(Response response) {
        super.print(response);
        sleep(1000);
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignore) {
        }
    }
}
