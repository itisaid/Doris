package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.DeleteCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteProtocolParserTest extends TestCase {

    public void testEncode() {
        DeleteProtocolParser parser = new DeleteProtocolParser();
        Key key = KeyFactory.createKey(NAME_SPACE, "key", 10);
        DeleteCommand commandData = new DeleteCommand(key);
        ChannelBuffer buffer = ChannelBuffers.buffer(200);
        parser.encode(commandData, buffer);

        buffer.array();
        byte[] arrayBytes = buffer.array();

        for (int i = 0; i < DELETE_ENCODE.length; i++) {
            assertEquals("index=" + i, DELETE_ENCODE[i], arrayBytes[i]);
        }
    }

    public void testDecode() {
        DeleteProtocolParser parser = new DeleteProtocolParser();
        DeleteCommand commandData = new DeleteCommand(KeyFactory.createKey(NAME_SPACE, "key", 1));
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(DELETE_RESULT);
        assertTrue(parser.decode(commandData, buffer));
        assertTrue(commandData.isSuccess());
    }

    public void testDecodeWiteError() {
        DeleteProtocolParser parser = new DeleteProtocolParser();
        DeleteCommand commandData = new DeleteCommand(KeyFactory.createKey(NAME_SPACE, "key", 1));
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(DELETE_RESULT_NOTFOUND);
        assertTrue(parser.decode(commandData, buffer));
        assertTrue(commandData.isSuccess() == false);
    }

    private static final int    NAME_SPACE             = 1000;
    private static final byte[] DELETE_ENCODE          = new byte[] { 'd', 'e', 'l', 'e', 't', 'e', ' ', '1', '0', '0',
            '0', ':', 'k', 'e', 'y', ' ', '0', ' ', '1', '0', '\r', '\n' };
    private static final byte[] DELETE_RESULT          = new byte[] { 'D', 'E', 'L', 'E', 'T', 'E', 'D', '\r', '\n' };
    private static final byte[] DELETE_RESULT_NOTFOUND = new byte[] { 'N', 'O', 'T', '_', 'F', 'O', 'U', 'N', 'D',
            '\r', '\n'                                };

}
