package com.alibaba.doris.client.net.command.result;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface CheckResult {

    public boolean isSuccess();

    public String getMessage();
}
