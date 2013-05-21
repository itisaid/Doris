package com.alibaba.doris.dataserver.config;

import java.util.List;
import java.util.Properties;

import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerConfigure {

    public List<ModuleConfigure> getModuleConfigList() {
        return moduleConfigList;
    }

    public void setModuleConfigList(List<ModuleConfigure> moduleConfigList) {
        this.moduleConfigList = moduleConfigList;
        for (ModuleConfigure conf : moduleConfigList) {
            conf.setDataServerConfigure(this);
        }
    }

    public ModuleConfigure getModuleConfigure(String moduleClassName) {
        for (ModuleConfigure conf : moduleConfigList) {
            if (conf.getClassName().equals(moduleClassName)) {
                return conf;
            }
        }
        return null;
    }

    public Properties getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(Properties commandLine) {
        this.commandLine = commandLine;
    }

    private List<ModuleConfigure> moduleConfigList;
    private Properties            commandLine = new Properties();
}
