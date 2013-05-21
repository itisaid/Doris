package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.SetProtocolParser;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetCommand extends BaseCommand<Boolean> {

    public SetCommand(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public void setResult(boolean bResult) {
        this.bResult = bResult;
    }

    public Boolean getResult() {
        return bResult;
    }

    public boolean isCas() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("SET");
        sb.append(" {");
        if (null != getKey()) {
            sb.append("[key=");
            sb.append(getKey());
            sb.append("]");
        }

        long rVersion = getRouteVersion();
        if (rVersion > 0) {
            sb.append("[RouteVersion=");
            sb.append(rVersion);
            sb.append("]");
        }

        Value v = getValue();
        if (null != v) {
            short flag = v.getFlag();
            if (flag > 0) {
                sb.append("[Flag=");
                sb.append(flag);
                sb.append("]");
            }

            if (v.getTimestamp() > 0) {
                sb.append("[Timestamp=");
                sb.append(v.getTimestamp());
                sb.append("]");
            }
        }

        sb.append("[result=").append(bResult).append("]");

        sb.append("}");
        return sb.toString();
    }

    private boolean                     bResult = false;
    private Key                         key;
    private Value                       value;
    private static final ProtocolParser parser  = new SetProtocolParser();
}
