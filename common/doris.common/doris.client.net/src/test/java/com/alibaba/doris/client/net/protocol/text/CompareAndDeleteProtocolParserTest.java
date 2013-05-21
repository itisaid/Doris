package com.alibaba.doris.client.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.client.net.command.CompareAndDeleteCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndDeleteProtocolParserTest extends TestCase {

    public void testEncode() {
        CompareAndDeleteProtocolParser parser = new CompareAndDeleteProtocolParser();
        Key key = KeyFactory.createKey(NAME_SPACE, "key", 10);
        CompareAndDeleteCommand commandData = new CompareAndDeleteCommand(key, 1);
        ChannelBuffer buffer = ChannelBuffers.buffer(200);
        parser.encode(commandData, buffer);

        buffer.array();
        byte[] arrayBytes = buffer.array();

        for (int i = 0; i < DELETE_ENCODE.length; i++) {
            assertEquals("index=" + i, DELETE_ENCODE[i], arrayBytes[i]);
        }
    }

    private static final int    NAME_SPACE    = 1000;
    private static final byte[] DELETE_ENCODE = new byte[] { 'c', 'a', 'd', ' ', '1', '0', '0', '0', ':', 'k', 'e',
            'y', ' ', '1', ' ', '0', ' ', '1', '0', '\r', '\n' };
}
