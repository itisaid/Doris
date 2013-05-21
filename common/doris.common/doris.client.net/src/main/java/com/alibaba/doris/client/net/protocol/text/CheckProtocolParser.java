package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.CheckCommand;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.Command;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckProtocolParser extends TextProtocolParser {

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        byte[] line = readLine(buffer);
        if (line == null) {
            return false;
        }

        CheckCommand check = (CheckCommand) commandData;
        int[] pos = new int[] { 0 };
        String status = parseNextField(line, pos);
        if (TRUE.equals(status)) {
            check.setSuccess(true);
            return true;
        } else if (FALSE.equals(status)) {
            check.setSuccess(false);
            check.setErrorMessage(parseNextField(line, pos));
        } else {
            check.setSuccess(false);
            generateErrorMessage(check, line);
        }

        return true;
    }

    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        CheckCommand check = (CheckCommand) commandData;
        Type checkType = check.getType();
        if (null != checkType) {
            assemableCommandData(buffer, CHECK, checkType.getType());
        } else {
            assemableCommandData(buffer, CHECK);
        }

    }

    protected static final String TRUE  = "true";
    protected static final String FALSE = "false";
}
