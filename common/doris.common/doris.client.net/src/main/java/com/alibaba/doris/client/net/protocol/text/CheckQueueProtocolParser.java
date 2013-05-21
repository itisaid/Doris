package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.CheckQueueCommand;
import com.alibaba.doris.client.net.command.Command;

/**
 * @author ajun
 */
public class CheckQueueProtocolParser extends CheckProtocolParser {

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        CheckQueueCommand cleaner = (CheckQueueCommand) commandData;
        byte[] line = readLine(buffer);
        if (line == null) {
            return false;
        }

        // 循环递归解析结果中的内容，直到命令结果完全正常为止；
        while (decodeLine(cleaner, line)) {
            line = readLine(buffer);
            if (line == null) {
                return false;
            }
        }

        if (count <= 0) {
            if (count == 0) {
                cleaner.setSuccess(true);
            } else {
                cleaner.setSuccess(false);
            }
        } else {
            // 循环并丢弃连接通道中的所有数据；
            return false;
        }

        return true;
    }

    private boolean decodeLine(CheckQueueCommand cleaner, byte[] line) {
        if (equalsTrue(line)) {
            count--;
        } else if (equalsFalse(line)) {
            count--;
            int[] pos = new int[] { 0 };
            skipNextField(line, pos);
            cleaner.setErrorMessage(parseNextField(line, pos));
        } else {
            // 统计丢弃数据的大小；
            discardBytes += line.length;
            return true;
        }

        if (count <= 0) {
            return false;
        }

        return true;
    }

    private boolean equalsTrue(byte[] line) {
        int i = 0;
        if (line.length < (TRUE_LENGTH)) {
            return false;
        }

        for (byte b : TRUE_BYTES) {
            if (line[i++] != b) {
                return false;
            }
        }

        if (line[i++] == CRLF[0] && line[i++] == CRLF[1]) {
            return true;
        }

        return false;
    }

    private boolean equalsFalse(byte[] line) {
        int i = 0;

        if (line.length < (FALSE_LENGTH)) {
            return false;
        }

        for (byte b : FALSE_BYTES) {
            if (line[i++] != b) {
                return false;
            }
        }

        if (line[i++] != ' ') {
            return false;
        }

        return true;
    }

    @Override
    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        // 连续写入两个check命令；
        super.encode(commandData, buffer);
        super.encode(commandData, buffer);
    }

    public long getDiscardBytes() {
        return discardBytes;
    }

    private int                   count        = 2;
    private long                  discardBytes;
    protected static final byte[] TRUE_BYTES   = TRUE.getBytes();
    protected static final byte[] FALSE_BYTES  = FALSE.getBytes();
    protected static final int    TRUE_LENGTH  = TRUE_BYTES.length + CRLF.length;
    protected static final int    FALSE_LENGTH = FALSE_BYTES.length + 1;
}
