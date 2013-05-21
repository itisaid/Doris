package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.CheckCommand;
import com.alibaba.doris.client.net.command.CheckCommand.CheckType;
import com.alibaba.doris.client.net.command.result.CheckResult;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckProtocolParserTest extends TestCase {

    public void testEncode() {
        CheckProtocolParser parser = new CheckProtocolParser();
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        CheckCommand cmd = new CheckCommand(CheckType.CHECK_NORMAL_NODE);
        parser.encode(cmd, buffer);

        for (byte b : CHECK_REQUEST_BYTES) {
            assertEquals(b, buffer.readByte());
        }
    }

    public void testCheckTempNodeEncode() {
        CheckProtocolParser parser = new CheckProtocolParser();
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        CheckCommand cmd = new CheckCommand(CheckType.CHECK_TEMP_NODE);
        parser.encode(cmd, buffer);

        for (byte b : CHECK_TEMP_NODE_REQUEST_BYTES) {
            assertEquals(b, buffer.readByte());
        }
    }

    public void testDecodeTrue() {
        CheckProtocolParser parser = new CheckProtocolParser();
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        CheckCommand cmd = new CheckCommand(CheckType.CHECK_NORMAL_NODE);
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    public void testDecodeFalse() {
        CheckProtocolParser parser = new CheckProtocolParser();
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES);
        CheckCommand cmd = new CheckCommand(CheckType.CHECK_NORMAL_NODE);
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    public void testDecode() {
        CheckProtocolParser parser = new CheckProtocolParser();
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        CheckCommand cmd = new CheckCommand(CheckType.CHECK_NORMAL_NODE);

        int len = 10;
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES, 0, len);
        assertFalse(parser.decode(cmd, buffer));
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES, len, CHECK_RETURN_FALSE_BYTES.length - len);
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    public static byte[] CHECK_REQUEST_BYTES           = new byte[] { 'c', 'h', 'e', 'c', 'k', ' ', 'c', 'h', 'e', 'c',
            'k', '_', 'n', 'o', 'r', 'm', 'a', 'l', '_', 'n', 'o', 'd', 'e', '\r', '\n' };
    public static byte[] CHECK_TEMP_NODE_REQUEST_BYTES = new byte[] { 'c', 'h', 'e', 'c', 'k', ' ', 'c', 'h', 'e', 'c',
            'k', '_', 't', 'e', 'm', 'p', '_', 'n', 'o', 'd', 'e', '\r', '\n' };
    public static byte[] CHECK_RETURN_TRUE_BYTES       = new byte[] { 't', 'r', 'u', 'e', '\r', '\n' };
    public static byte[] CHECK_RETURN_FALSE_BYTES      = new byte[] { 'f', 'a', 'l', 's', 'e', ' ', 's', 't', 'o', 'r',
            'a', 'g', 'e', '_', 'm', 'o', 'd', 'u', 'l', 'e', '_', 'e', 'r', 'r', 'o', 'r', '\r', '\n' };
}
