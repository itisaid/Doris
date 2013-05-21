package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.BaseCommand;
import com.alibaba.doris.client.net.command.ErrorType;
import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class TextProtocolParser implements ProtocolParser {

    public static final byte[] CRLF                = { '\r', '\n' };
    public static final byte[] SPLID               = { ' ' };

    public static final byte[] SET                 = { 's', 'e', 't' };
    public static final byte[] CAS                 = { 'c', 'a', 's' };
    public static final byte[] CAD                 = { 'c', 'a', 'd' };
    public static final byte[] GET                 = { 'g', 'e', 't' };
    public static final byte[] GETS                = { 'g', 'e', 't', 's' };
    public static final byte[] CHECK               = { 'c', 'h', 'e', 'c', 'k' };
    public static final byte   SPACE               = ' ';
    public static final byte[] DELETE              = { 'd', 'e', 'l', 'e', 't', 'e' };
    public static final byte[] NOREPLY             = { 'n', 'o', 'r', 'e', 'p', 'l', 'y' };

    public static final byte[] STATS               = { 's', 't', 'a', 't', 's' };
    public static final byte[] VERSION             = { 'v', 'e', 'r', 's', 'i', 'o', 'n' };

    public static final byte[] STORED              = { 'S', 'T', 'O', 'R', 'E', 'D' };
    public static final byte[] DELETED             = { 'D', 'E', 'L', 'E', 'T', 'E', 'D' };
    public static final byte[] NOT_STORED          = { 'N', 'O', 'T', '_', 'S', 'T', 'O', 'R', 'E', 'D' };
    public static final byte[] NOT_FOUND           = { 'N', 'O', 'T', '_', 'F', 'O', 'U', 'N', 'D' };
    public static final byte[] DELETE_FAILED       = { 'D', 'E', 'L', 'E', 'T', 'E', '_', 'F', 'A', 'I', 'L', 'E', 'D' };
    public static final byte[] ERROR               = { 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] CLIENT_ERROR        = { 'C', 'L', 'I', 'E', 'N', 'T', '_', 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] SERVER_ERROR        = { 'S', 'E', 'R', 'V', 'E', 'R', '_', 'E', 'R', 'R', 'O', 'R' };
    public static final byte[] VERSION_OUT_OF_DATE = { 'V', 'E', 'R', 'S', 'I', 'O', 'N', '_', 'O', 'U', 'T', '_', 'O',
            'F', '_', 'D', 'A', 'T', 'E'          };
    public static final byte[] VALUE               = { 'V', 'A', 'L', 'U', 'E' };

    protected void assemableCommandData(ChannelBuffer buffer, byte[]... args) {
        boolean isFirst = true;

        for (byte[] o : args) {
            if (isFirst) {
                isFirst = false;
            } else {
                buffer.writeBytes(SPLID);
            }
            buffer.writeBytes(o);
        }

        buffer.writeBytes(CRLF);
    }

    public byte[] readLine(ChannelBuffer buf) {
        byte b = 0;
        int pos;
        int startPos = pos = 0;
        int limit = buf.readableBytes();
        boolean eol = false;

        buf.markReaderIndex();

        while (pos < limit) {
            b = buf.readByte();
            pos++;
            if (b == CRLF[0]) {
                eol = true;
            } else {
                if (eol) {
                    if (b == CRLF[1]) {
                        eol = false;
                        byte[] byteArray = new byte[pos - startPos];
                        buf.resetReaderIndex();
                        buf.readBytes(byteArray);
                        return byteArray;
                    }
                    eol = false;
                }
            }
        }

        buf.resetReaderIndex();
        return null;
    }

    protected String parseNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        while (line[pos] != SPACE && line[pos] != CRLF[0] && pos < line.length) {
            pos++;
        }
        // skip ' '
        pos++;

        String value = ByteUtils.byteToString(line, startPos[0], (pos - startPos[0] - 1));
        startPos[0] = pos;
        return value;
    }

    protected void skipNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        while (line[pos] != SPACE && line[pos] != CRLF[0]) {
            pos++;
        }
        // skip ' '
        pos++;

        startPos[0] = pos;
        return;
    }

    protected void generateErrorMessage(BaseCommand<?> command, byte[] messageBytes) {
        if ((messageBytes[0] == ERROR[0]) && (messageBytes[4] == ERROR[4])) {
            int start = ERROR.length + 1;

            command.setErrorMessage(getErrorMessage(messageBytes, start));
            command.setErrorType(ErrorType.UNKNOWN);
            return;
        }

        if (messageBytes[0] == VERSION_OUT_OF_DATE[0] && messageBytes[4] == VERSION_OUT_OF_DATE[4]) {
            int start = VERSION_OUT_OF_DATE.length + 1;

            command.setErrorMessage(getErrorMessage(messageBytes, start));
            command.setErrorType(ErrorType.VERSION_OUT_OF_DATE);
            return;
        }

        if (messageBytes[0] == CLIENT_ERROR[0] && messageBytes[4] == CLIENT_ERROR[4]) {
            // read error message
            int start = CLIENT_ERROR.length + 1;

            command.setErrorMessage(getErrorMessage(messageBytes, start));
            command.setErrorType(ErrorType.CLIENT_ERROR);
            return;
        }

        if (messageBytes[0] == SERVER_ERROR[0] && messageBytes[4] == SERVER_ERROR[4]) {
            // read error message
            int start = SERVER_ERROR.length + 1;

            command.setErrorMessage(getErrorMessage(messageBytes, start));
            command.setErrorType(ErrorType.SERVER_ERROR);
            return;
        }

        command.setErrorMessage("Unknown exception! " + ByteUtils.byteToString(messageBytes));
        command.setErrorType(ErrorType.UNKNOWN);
    }

    protected String getErrorMessage(byte[] messageBytes, int start) {
        // skip \r\n
        int len = messageBytes.length - start - 2;
        if (len > 0) {
            // read error message
            return ByteUtils.byteToString(messageBytes, start, len);
        }

        return "";
    }

    /**
     * 判断当前流是否是命令结束标记。
     * 
     * @param byteArray
     * @return
     */
    protected boolean checkIsEndFlag(byte[] lineBytes) {
        int start = 0;
        if (lineBytes[start] == 'E' && lineBytes[start + 1] == 'N' /* && lineBytes[start + 2] == 'D' */) {
            return true;
        }

        return false;
    }
}
