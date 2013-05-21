package com.alibaba.doris.dataserver;

import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Module {

    /**
     * 装载一个Module，装载操作一定发生在服务器发出的onStartup()事件之前调用。
     * 
     * @param conf 传入整个DataServer的全局配置文件信息。
     */
    public void load(ModuleConfigure conf);

    /**
     * 卸载一个Module。卸载操作一定发生在服务器发出onShutdown()时间之后调用。
     */
    public void unload();

    /**
     * 获取一个Module实例的名称。
     * 
     * @return
     */
    public String getName();

    /**
     * 设置一个Module实例的名称；该名称一般从配置文件中读取；
     */
    public void setName(String name);

    /**
     * 获取当前Module的ModuleContext对象
     * 
     * @return
     */
    public ModuleContext getModuleContext();
}
