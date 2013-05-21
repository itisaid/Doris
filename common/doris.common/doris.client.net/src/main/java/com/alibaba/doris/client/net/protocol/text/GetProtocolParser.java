package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.GetCommand;
import com.alibaba.doris.client.net.command.GetCommand.PhaseStructure;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.NullValueImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class GetProtocolParser extends TextProtocolParser {

    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        GetCommand get = (GetCommand) commandData;
        Key key = get.getKey();
        if (null != key) {
            byte[] keyBytes = key.getPhysicalKeyBytes();
            long routeVersion = key.getRouteVersion();
            byte[] vnodeBytes = ByteUtils.stringToByte(Integer.toString(key.getVNode(), 10));
            byte[] routeVersionBytes = ByteUtils.stringToByte(String.valueOf(routeVersion));
            assemableCommandData(buffer, GET, keyBytes, routeVersionBytes, vnodeBytes);
        }
    }

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        GetCommand get = (GetCommand) commandData;
        PhaseStructure currentPhase = get.getCurrentPhase();
        while (true) {
            boolean needBreak = false;
            switch (currentPhase) {
                case HEAD: {
                    buffer.markReaderIndex();
                    byte[] line = readLine(buffer);
                    if (line == null) {
                        // 流中的数据已经读完，但本命令的数据还没读完，返回。
                        needBreak = true;
                        buffer.resetReaderIndex();
                        break;
                    }

                    parseHead(line, get);

                    if (get.isSuccess()) {
                        needBreak = true;
                    } else {
                        currentPhase = PhaseStructure.DATA;
                        get.setCurrentPhase(currentPhase);
                    }

                    break;
                }
                case DATA: {
                    buffer.markReaderIndex();
                    if (!readData(buffer, get)) {
                        // 还没读完数据。
                        needBreak = true;
                        buffer.resetReaderIndex();
                        break;
                    }

                    currentPhase = PhaseStructure.END;
                    get.setCurrentPhase(currentPhase);
                    break;
                }
                case END: {
                    buffer.markReaderIndex();
                    byte[] line = readLine(buffer);
                    if (line == null) {
                        // 流中的数据已经读完，但本命令的数据还没读完，返回。
                        needBreak = true;
                        buffer.resetReaderIndex();
                        break;
                    }

                    if (checkIsEndFlag(line)) {
                        get.setSuccess(true);
                        needBreak = true;
                        break;
                    }

                    throw new NetException("Invalid byte stream. Couldn't find the end flag. [" + get + "]");
                }

                default:
                    break;
            }

            if (needBreak) {
                break;
            }
        }

        if (get.isSuccess()) {
            return true;
        }

        return false;
    }

    protected boolean readData(ChannelBuffer buffer, GetCommand command) {
        int remain = buffer.readableBytes();

        if (remain == 0) {
            return false;
        }

        int length = command.getValueBytes();
        if (remain < length + 2) {
            // 如果buffer中只有部分数据
            return false;
        }

        // 读取所有data
        byte[] byteArray = new byte[length];
        buffer.readBytes(byteArray);
        buffer.skipBytes(2);// skip \r\n
        command.getValue().setValueBytes(byteArray);
        return true;
    }

    protected void parseHead(byte[] headBytes, GetCommand command) {
        int start = 0;
        if (headBytes[start] == 'V' && headBytes[start + 1] == 'A') {// VALUE
            parseValue(headBytes, command);
            return;
        } else if (headBytes[start] == 'E' && headBytes[start + 1] == 'N' && headBytes[start + 2] == 'D') {// END
            command.setSuccess(true);
            command.setValue(nullValue);
            return;
        } else {
            // success means this command processing success.
            command.setSuccess(true);
            generateErrorMessage(command, headBytes);
            return;
        }
    }

    protected void parseValue(byte[] headBytes, GetCommand command) {
        int startPos = 6;// skip "VALUE "

        int[] pos = { startPos };
        // skip key
        skipNextField(headBytes, pos);

        Value value = new ValueImpl(null, 0);
        command.setValue(value);

        value.setFlag(Short.parseShort(parseNextField(headBytes, pos)));
        // skip vnode
        skipNextField(headBytes, pos);
        // parse time stamp
        String timeStamp = parseNextField(headBytes, pos);
        value.setTimestamp(Long.parseLong(timeStamp));

        // parse value bytes.
        String v = parseNextField(headBytes, pos);
        command.setValueBytes(Integer.parseInt(v));
    }

    private static final Value nullValue = new NullValueImpl();
}
