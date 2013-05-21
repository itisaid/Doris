package com.alibaba.doris.dataserver.extratools;

import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.extratools.help.HelperAction;
import com.alibaba.doris.dataserver.extratools.replica.action.ExportAction;
import com.alibaba.doris.dataserver.extratools.replica.action.ImportAction;

/**
 * DataServer扩展工具模块<br>
 * 存储层工具：输入导入导出工具。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExtraToolsModule extends BaseModule {

    public String getName() {
        return "ExtraToolsModule";
    }

    public void load(ModuleConfigure conf) {
        ActionFactory.registAction(ExtraActionType.EXPORT, new ExportAction());
        ActionFactory.registAction(ExtraActionType.IMPORT, new ImportAction());
        ActionFactory.registAction(ExtraActionType.HELP, new HelperAction());
    }

    public void unload() {

    }

}
