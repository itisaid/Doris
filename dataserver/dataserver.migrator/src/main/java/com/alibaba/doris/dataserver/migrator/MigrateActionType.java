package com.alibaba.doris.dataserver.migrator;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.parser.ActionParser;
import com.alibaba.doris.dataserver.migrator.action.MigrateActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum MigrateActionType implements ActionType {

    MIGRATE("migrate", new MigrateActionParser());

    private MigrateActionType(String name, ActionParser actionParser) {
        this.command = name;
        this.commandBytes = ByteUtils.stringToByte(command);
        this.parser = actionParser;
    }

    public String getName() {
        return command;
    }

    public byte[] getNameBytes() {
        return commandBytes;
    }

    public ActionParser getParser() {
        return parser;
    }

    private ActionParser parser;
    private String       command;
    private byte[]       commandBytes;
}
