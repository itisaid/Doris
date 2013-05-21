package com.alibaba.doris.dataserver;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ModuleContextAware {

    /**
     * 注入当前module的ModuleContext对象
     * 
     * @param moduleContext
     */
    public void setModuleContext(ModuleContext moduleContext);
}
