package com.alibaba.doris.dataserver.extratools.help.print;

import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.extratools.help.HelperActionData;

/**
 * 实现逐字打印的输出效果。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LiterallyLine extends Line {

    public LiterallyLine(String line) {
        super(line);
    }

    @Override
    public void print(Response response) {
        String line = getLine();
        HelperActionData md = new HelperActionData();
        for (int i = 0; i < line.length(); i++) {
            md.setMessage(String.valueOf(line.charAt(i)));
            response.write(md);
            response.flush();
            sleep(500);
        }
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignore) {
        }
    }
}
