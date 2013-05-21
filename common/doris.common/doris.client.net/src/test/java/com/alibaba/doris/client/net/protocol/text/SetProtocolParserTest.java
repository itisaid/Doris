package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.SetCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetProtocolParserTest extends TestCase {

    public void testEncode() {
        SetProtocolParser parser = new SetProtocolParser();
        Key key = KeyFactory.createKey(NAME_SPACE, "key", 10);
        Value value = ValueFactory.createValue("test".getBytes(), 10);
        SetCommand command = new SetCommand(key, value);
        ChannelBuffer buffer = ChannelBuffers.buffer(100);
        parser.encode(command, buffer);
        for (int i = 0; i < buffer.readableBytes(); i++) {
            assertEquals("index=" + i, buffer.getByte(i), SET[i]);
        }
    }

    public void testEncode1() {
        SetProtocolParser parser = new SetProtocolParser();
        Key key = KeyFactory.createKey(NAME_SPACE, "key");
        Value value = ValueFactory.createValue("test".getBytes(), 10);
        SetCommand command = new SetCommand(key, value);
        ChannelBuffer buffer = ChannelBuffers.buffer(100);
        parser.encode(command, buffer);
        for (int i = 0; i < buffer.readableBytes(); i++) {
            assertEquals("index=" + i, buffer.getByte(i), SET1[i]);
        }
    }

    private static final int    NAME_SPACE = 10;
    private static final byte[] SET        = new byte[] { 's', 'e', 't', ' ', '1', '0', ':', 'k', 'e', 'y', ' ', '0',
            ' ', '1', '0', ' ', '4', ' ', '0', ' ', '1', '0', '\r', '\n', 't', 'e', 's', 't', '\r', '\n' };

    private static final byte[] SET1       = new byte[] { 's', 'e', 't', ' ', '1', '0', ':', 'k', 'e', 'y', ' ', '0',
            ' ', '1', '0', ' ', '4', ' ', '0', ' ', '-', '1', '\r', '\n', 't', 'e', 's', 't', '\r', '\n' };
}
