package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.GetProtocolParser;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class GetCommand extends BaseCommand<Value> {

    public GetCommand(Key key) {
        this.key = key;
    }

    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public Key getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getResult() {
        return getValue();
    }

    public int getValueBytes() {
        return valueBytes;
    }

    public void setValueBytes(int valueBytes) {
        this.valueBytes = valueBytes;
    }

    public PhaseStructure getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(PhaseStructure currentPhase) {
        this.currentPhase = currentPhase;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("GET");
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
                sb.append("[Time=");
                sb.append(v.getTimestamp());
                sb.append("]");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private PhaseStructure              currentPhase = PhaseStructure.HEAD;
    private Key                         key;
    private Value                       value;
    private int                         valueBytes;
    private static final ProtocolParser parser       = new GetProtocolParser();

    public static enum PhaseStructure {
        HEAD, DATA, END
    }
}
