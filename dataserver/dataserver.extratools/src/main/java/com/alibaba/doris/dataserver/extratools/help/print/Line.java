package com.alibaba.doris.dataserver.extratools.help.print;

import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.extratools.help.HelperActionData;

/**
 * 打印一个普通行。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class Line implements Printer {

    public Line(String line) {
        this.line = line;
    }

    public void print(Response response) {
        HelperActionData md = new HelperActionData();
        md.setMessage(line);
        response.write(md);
        response.flush();
    }

    protected String getLine() {
        return line;
    }

    private String line;
}
