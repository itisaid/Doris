package com.alibaba.doris.dataserver.extratools.help.print;

import com.alibaba.doris.dataserver.core.Response;

/**
 * 打印输出接口。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Printer {

    public void print(Response response);
}
