package com.alibaba.doris.dataserver.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.SupportBodyActionData;
import com.alibaba.doris.dataserver.action.parser.ActionParser;
import com.alibaba.doris.dataserver.net.InvalidCommandException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class TextProtocolDecoder {

    public ActionData readHeader(ChannelBuffer buf) {
        byte[] byteArray = readLine(buf);
        if (byteArray != null) {
            return parseHeader(byteArray);
        }
        return null;
    }

    /**
     * 从数据流中读取一行数据，行分割符：\r\n
     * 
     * @param buf
     * @return
     */
    public byte[] readLine(ChannelBuffer buf) {
        byte b = 0;
        int pos;
        int startPos = pos = buf.readerIndex();
        int limit = buf.writerIndex();
        boolean eol = false;
        byte[] array = buf.array();

        //buf.markReaderIndex();

        while (pos < limit) {
            b = array[pos++];
            if (b == ProtocolConstant.CRLF[0]) {
                eol = true;
            } else {
                if (eol) {
                    if (b == ProtocolConstant.CRLF[1]) {
                        eol = false;
                        byte[] byteArray = new byte[pos - startPos];
                        // buf.resetReaderIndex();
                        buf.readBytes(byteArray);
                        return byteArray;
                    }
                    eol = false;
                }
            }
        }

       // buf.resetReaderIndex();
        return null;
    }

    /**
     * 跳过流中的一段数据，以\r\n作为段分割符，返回true:表示成功跳过流中的一段数据，跳过的数据段<br>
     * 将无法被读取。如果返回false:标识流中的数据不完整，数据的指针会回到<br>
     * 读取前的位置。
     * 
     * @param buf
     * @return
     */
    public boolean skipLine(ChannelBuffer buf) {
        byte b = 0;
        int pos = buf.readerIndex();
        int limit = buf.readableBytes();
        boolean eol = false;

        buf.markReaderIndex();
        while (pos < limit) {
            b = buf.readByte();
            pos++;
            if (b == ProtocolConstant.CRLF[0]) {
                eol = true;
            } else {
                if (eol) {
                    if (b == ProtocolConstant.CRLF[1]) {
                        eol = false;
                        return true;
                    }
                    eol = false;
                }
            }
        }

        buf.resetReaderIndex();
        return false;
    }

    public byte[] readBody(ChannelBuffer buf, SupportBodyActionData md) {
        int bodyBytes = md.getBodyBytes();
        if (bodyBytes > MAX_BODY_SIZE) {
            throw new InvalidCommandException("Invalid command body, The body is too large.");
        }

        if (buf.readableBytes() >= (bodyBytes + ProtocolConstant.CRLF.length)) {
            byte[] bodyArray = new byte[bodyBytes];
            buf.readBytes(bodyArray);
            // check body length
            byte cr = buf.readByte();
            byte lf = buf.readByte();
            if (cr != ProtocolConstant.CRLF[0] || lf != ProtocolConstant.CRLF[1]) {
                throw new InvalidCommandException("Invalid body data, The command body length is " + md.getBodyBytes()
                                                  + ".");
            }
            return bodyArray;
        } else {
            return null;
        }

    }

    public ActionData parseHeader(byte[] header) {
        int pos = 0;
        // skip all space of the command head.
        while (header[pos] == ProtocolConstant.SPACE) {
            pos++;
        }

        int startPos = pos;
        while (header[pos] != ProtocolConstant.SPACE && header[pos] != ProtocolConstant.CRLF[0]) {
            pos++;
        }

        String commandName = ByteUtils.byteToString(header, startPos, pos - startPos);
        ActionParser parser = ActionFactory.getActionParser(commandName);
        if (null != parser) {
            // skip ' '
            if (header[pos] == ProtocolConstant.SPACE) {
                pos++;
            }
            return parser.readHead(header, pos);
        }

        throw new InvalidCommandException("Unknown command : '" + commandName + "'");
    }

    protected String parseNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        while (line[pos] != ProtocolConstant.SPACE && line[pos] != ProtocolConstant.CRLF[0]) {
            pos++;
        }
        // skip ' '
        pos++;

        String value = ByteUtils.byteToString(line, startPos[0], (pos - startPos[0] - 1));
        startPos[0] = pos;
        return value;
    }

    /**
     * 为了提升性能，new String会消耗大量CPU资源。
     * 
     * @param line
     * @param startPos
     */
    protected void skipNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        while (line[pos] != ProtocolConstant.SPACE && line[pos] != ProtocolConstant.CRLF[0]) {
            pos++;
        }
        // skip ' '
        pos++;

        startPos[0] = pos;
    }

    private static final int MAX_BODY_SIZE = 1024 * 1024; // The max lenght of body.
}
