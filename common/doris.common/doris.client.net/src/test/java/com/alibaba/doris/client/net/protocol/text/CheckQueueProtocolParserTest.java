package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.CheckCommand;
import com.alibaba.doris.client.net.command.CheckQueueCommand;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.client.net.protocol.ProtocolParser;

/**
 * @author ajun
 */
public class CheckQueueProtocolParserTest extends TestCase {

    public void testEncode() {

        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        parser.encode(cmd, buffer);

        for (byte b : CHECK_REQUEST_BYTES) {
            assertEquals(b, buffer.readByte());
        }

        for (byte b : CHECK_REQUEST_BYTES) {
            assertEquals(b, buffer.readByte());
        }
    }

    public void testDecode() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_IVALID_BYTES_WITH_INVALID_FULL);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // 断言所有无效的数据全部跳过；队列中的数据清理干净；
        assertTrue(buffer.readableBytes() <= 0);
    }

    public void testDecodeTrue() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    public void testDecodeTrue2() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        // 多插入一条，命令应该正常返回，并且不能多读队列中的数据；
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());

        // 断言队列中应该还剩余一个CHECK_RETURN_TRUE_BYTES 结果；
        CheckProtocolParser cp = new CheckProtocolParser();
        assertTrue(cp.decode(new CheckCommand(null), buffer));
    }

    public void testDecodeFalse() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES);
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    public void testDecodeFalse2() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertFalse(parser.decode(cmd, buffer));
    }

    public void testDecodeInvalidResult() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_FALSE_BYTES);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertFalse(parser.decode(cmd, buffer));
    }

    public void testDecodeFalseTrue() {
        ChannelBuffer buffer = ChannelBuffers.buffer(1024);
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES, 0, CHECK_RETURN_TRUE_BYTES.length - 2);
        CheckQueueCommand cmd = new CheckQueueCommand();
        ProtocolParser parser = cmd.getProtocolParser();
        assertFalse(parser.decode(cmd, buffer));
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES, CHECK_RETURN_TRUE_BYTES.length - 2, 2);
        assertFalse(parser.decode(cmd, buffer));
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES, 0, CHECK_RETURN_TRUE_BYTES.length - 2);
        assertFalse(parser.decode(cmd, buffer));
        buffer.writeBytes(CHECK_RETURN_TRUE_BYTES, CHECK_RETURN_TRUE_BYTES.length - 2, 2);
        assertTrue(parser.decode(cmd, buffer));
        CheckResult result = cmd.getResult();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    private static byte[] CHECK_RETURN_TRUE_BYTES                     = new byte[] { 't', 'r', 'u', 'e', '\r', '\n' };
    private static byte[] CHECK_RETURN_FALSE_BYTES                    = new byte[] { 'f', 'a', 'l', 's', 'e', ' ', 's',
            't', 'o', 'r', 'a', 'g', 'e', '_', 'm', 'o', 'd', 'u', 'l', 'e', '_', 'e', 'r', 'r', 'o', 'r', '\r', '\n' };
    private static byte[] CHECK_RETURN_IVALID_BYTES_WITH_INVALID_FULL = new byte[] { 'V', 'A', 'L', 'U', 'E', ' ', '1',
            '0', '0', '0', ':', 'k', 'e', 'y', ' ', '0', ' ', '1', '0', ' ', '1', '0', '0', '0', ' ', '1', '0', '\r',
            '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n', 'E', 'N', 'D', '\r', '\n' };
    private static byte[] CHECK_REQUEST_BYTES                         = new byte[] { 'c', 'h', 'e', 'c', 'k', '\r',
            '\n'                                                     };
}
