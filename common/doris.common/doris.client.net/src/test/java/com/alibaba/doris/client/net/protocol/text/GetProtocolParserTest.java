package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.GetCommand;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.NullValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class GetProtocolParserTest extends TestCase {

    public void testEncode() {
        GetProtocolParser parser = new GetProtocolParser();
        KeyImpl key = new KeyImpl(NAME_SPACE, "key", 10);
        GetCommand commandData = new GetCommand(key);
        ChannelBuffer buffer = ChannelBuffers.buffer(200);
        parser.encode(commandData, buffer);

        buffer.array();
        byte[] arrayBytes = buffer.array();

        for (int i = 0; i < GET_ENCODE.length; i++) {
            assertEquals("arrayPos=" + i, GET_ENCODE[i], arrayBytes[i]);
        }
    }

    public void testDecode() {
        GetProtocolParser parser = new GetProtocolParser();
        GetCommand commandData = new GetCommand(KeyFactory.createKey(NAME_SPACE, "key", 10));
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(GET_RESULT);
        assertTrue(parser.decode(commandData, buffer));
        Value value = commandData.getValue();
        assertNotNull(value);
        assertEquals(0, value.getFlag());
        assertNotNull(value.getValueBytes());
        assertEquals(1000, value.getTimestamp());
        assertEquals("1234567890", ByteUtils.byteToString(value.getValueBytes()));
    }

    public void testDecodeUnnormal() {
        GetProtocolParser parser = new GetProtocolParser();
        GetCommand commandData = new GetCommand(KeyFactory.createKey(NAME_SPACE, "key", 10));

        ChannelBuffer buffer = ChannelBuffers.buffer(100);
        buffer.writeBytes(GET_RESULT1);
        assertFalse(parser.decode(commandData, buffer));

        buffer.writeBytes(GET_RESULT2);
        assertTrue(parser.decode(commandData, buffer));

        Value value = commandData.getValue();

        assertNotNull(value);
        assertEquals(0, value.getFlag());
        assertNotNull(value.getValueBytes());
        assertEquals(1000, value.getTimestamp());
        assertEquals("1234567890", ByteUtils.byteToString(value.getValueBytes()));
    }

    public void testDecodeNotFound() {
        GetProtocolParser parser = new GetProtocolParser();
        GetCommand commandData = new GetCommand(KeyFactory.createKey(NAME_SPACE, "key", 10));
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(GET_RESULT_NOTFOUND);
        assertTrue(parser.decode(commandData, buffer));
        Value value = commandData.getValue();
        assertNotNull(value);
        assertTrue(value instanceof NullValueImpl);
    }

    private static final int    NAME_SPACE          = 1000;
    private static final byte[] GET_ENCODE          = new byte[] { 'g', 'e', 't', ' ', '1', '0', '0', '0', ':', 'k',
            'e', 'y', ' ', '0', ' ', '1', '0', '\r', '\n' };

    // VALUE name:key 0 1000 10\r\n1234567890\r\nEND\r\n
    private static final byte[] GET_RESULT          = new byte[] { 'V', 'A', 'L', 'U', 'E', ' ', '1', '0', '0', '0',
            ':', 'k', 'e', 'y', ' ', '0', ' ', '1', '0', ' ', '1', '0', '0', '0', ' ', '1', '0', '\r', '\n', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n', 'E', 'N', 'D', '\r', '\n' };

    private static final byte[] GET_RESULT1         = new byte[] { 'V', 'A', 'L', 'U', 'E', ' ', '1', '0', '0', '0',
            ':', 'k', 'e', 'y', ' ', '0', ' ', '1', '0', ' ', '1', '0', '0', '0', ' ', '1', '0', '\r' };
    private static final byte[] GET_RESULT2         = new byte[] { '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '\r', '\n', 'E', 'N', 'D', '\r', '\n' };

    private static final byte[] GET_RESULT_NOTFOUND = new byte[] { 'E', 'N', 'D', '\r', '\n' };
}
