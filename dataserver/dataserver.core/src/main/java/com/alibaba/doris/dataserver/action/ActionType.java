package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.action.parser.ActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ActionType {

    /**
     * 获取当前Action类型的名称。
     * 
     * @return
     */
    public String getName();

    /**
     * 获取当前Action类型的二进制byte数组。
     * 
     * @return
     */
    public byte[] getNameBytes();

    /**
     * 获取当前请求Action类型相对应的解析器。
     * 
     * @return
     */
    public ActionParser getParser();
}
