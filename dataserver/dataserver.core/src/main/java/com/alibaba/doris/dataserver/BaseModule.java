package com.alibaba.doris.dataserver;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseModule implements Module, ModuleContextAware {

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public ModuleContext getModuleContext() {
        return this.moduleContext;
    }

    public void setModuleContext(ModuleContext moduleContext) {
        this.moduleContext = moduleContext;
    }

    private ModuleContext moduleContext;
    private String        name;
}
