package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.parser.ActionParser;
import com.alibaba.doris.dataserver.action.parser.CheckActionParser;
import com.alibaba.doris.dataserver.action.parser.CompareAndDeleteActionParser;
import com.alibaba.doris.dataserver.action.parser.CompareAndSetActionParser;
import com.alibaba.doris.dataserver.action.parser.DeleteActionParser;
import com.alibaba.doris.dataserver.action.parser.ErrorActionParser;
import com.alibaba.doris.dataserver.action.parser.ExitActionParser;
import com.alibaba.doris.dataserver.action.parser.GetActionParser;
import com.alibaba.doris.dataserver.action.parser.SetActionParser;
import com.alibaba.doris.dataserver.action.parser.ShutdownActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum BaseActionType implements ActionType {
    SET("set", new SetActionParser()), /* set */
    CAS("cas", new CompareAndSetActionParser()), /* cas */
    GET("get", new GetActionParser()), /* get */
    DELETE("delete", new DeleteActionParser()), /* delete */
    CAD("cad", new CompareAndDeleteActionParser()), /* cad */
    ERROR("error", new ErrorActionParser()), /* error */
    EXIT("exit", new ExitActionParser()), /* exit */
    SHUTDOWN("shutdown", new ShutdownActionParser()), /* shutdown */
    CHECK("check", new CheckActionParser()) /* check */;

    private BaseActionType(String name, ActionParser parser) {
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
