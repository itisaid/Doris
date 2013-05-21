package com.alibaba.doris.dataserver.extratools;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.parser.ActionParser;
import com.alibaba.doris.dataserver.extratools.help.HelperActionParser;
import com.alibaba.doris.dataserver.extratools.replica.action.ExportActionParser;
import com.alibaba.doris.dataserver.extratools.replica.action.ImportActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum ExtraActionType implements ActionType {
    EXPORT("export", new ExportActionParser()), /**/
    IMPORT("import", new ImportActionParser()), /**/
    HELP("help", new HelperActionParser());

    private ExtraActionType(String name, ActionParser parser) {
        this.name = name;
        this.nameBytes = ByteUtils.stringToByte(name);
        this.parser = parser;
    }

    public byte[] getNameBytes() {
        return nameBytes;
    }

    public String getName() {
        return name;
    }

    public ActionParser getParser() {
        return parser;
    }

    private byte[]       nameBytes;
    private String       name;
    private ActionParser parser;
}
