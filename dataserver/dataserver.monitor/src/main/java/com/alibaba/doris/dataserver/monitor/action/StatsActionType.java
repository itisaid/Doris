package com.alibaba.doris.dataserver.monitor.action;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.parser.ActionParser;

/**
 * @author yi.zhou
 */
public enum StatsActionType implements ActionType {

    STATS("stats", new StatsActionParser());

    private StatsActionType(String name, ActionParser actionParser) {
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
