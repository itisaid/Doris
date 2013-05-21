package com.alibaba.doris.dataserver.net.protocol.text;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.action.CatchCommandErrorAction;
import com.alibaba.doris.dataserver.action.CheckAction;
import com.alibaba.doris.dataserver.action.DeleteAction;
import com.alibaba.doris.dataserver.action.ExitServerAction;
import com.alibaba.doris.dataserver.action.GetAction;
import com.alibaba.doris.dataserver.action.SetAction;
import com.alibaba.doris.dataserver.action.ShutdownAction;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.CommonActionData;

public class TextProtocolDecoderTest extends TestCase {

    public TextProtocolDecoderTest() {
        ActionFactory.registAction(BaseActionType.SET, new SetAction());
        ActionFactory.registAction(BaseActionType.CAS, new SetAction());
        ActionFactory.registAction(BaseActionType.GET, new GetAction());
        ActionFactory.registAction(BaseActionType.DELETE, new DeleteAction());
        ActionFactory.registAction(BaseActionType.CAD, new DeleteAction());
        ActionFactory.registAction(BaseActionType.ERROR, new CatchCommandErrorAction());
        ActionFactory.registAction(BaseActionType.EXIT, new ExitServerAction());
        ActionFactory.registAction(BaseActionType.SHUTDOWN, new ShutdownAction());
        ActionFactory.registAction(BaseActionType.CHECK, new CheckAction());
    }

    public static void main(String[] args) {
        VirtualRouterImpl.setDebug(true);
        ActionFactory.registAction(BaseActionType.GET, new GetAction());

        TextProtocolDecoder decoder = new TextProtocolDecoder();
        ChannelBuffer buffer = ChannelBuffers.copiedBuffer(commandHead);

        int error = 0;
        int len = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            buffer.markReaderIndex();
            if (decoder.readHeader(buffer) == null) {
                error++;
            }
            buffer.resetReaderIndex();
        }
        long end = System.currentTimeMillis();
        System.out.println("Len:" + len + " error:" + error + " time(ms):" + (end - start));
    }

    public void testSetDecodeNormal() {
        VirtualRouterImpl.setDebug(true);
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(SET_COMMAND);
        TextProtocolDecoder decoder = new TextProtocolDecoder();
        ActionData md = decoder.readHeader(buffer);
        assertNotNull(md);

        assertEquals(BaseActionType.SET.getName(), md.getActionType().getName());
        CommonActionData sMd = (CommonActionData) md;
        sMd.setBodyByteArray(decoder.readBody(buffer, sMd));
        assertNotNull(sMd.getBodyByteArray());
        assertEquals(0, sMd.getFlag());
        assertEquals("product:product_id", ByteUtils.byteToString(sMd.getKeyBytes()));
        assertEquals(12345, sMd.getTimestamp());
        assertEquals(10, sMd.getBodyByteArray().length);
        assertFalse(sMd.isNoreply());
    }

    public void testSetDecodeWithNoreplyAndExtraSpace() {
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(SET_COMMAND_NOREPLY_AND_SPACE);
        TextProtocolDecoder decoder = new TextProtocolDecoder();
        ActionData md = decoder.readHeader(buffer);
        assertNotNull(md);

        assertEquals(BaseActionType.SET.getName(), md.getActionType().getName());
        CommonActionData sMd = (CommonActionData) md;
        sMd.setBodyByteArray(decoder.readBody(buffer, sMd));
        assertNotNull(sMd.getBodyByteArray());
        assertEquals(0, sMd.getFlag());
        assertEquals("product:product_id", ByteUtils.byteToString(sMd.getKeyBytes()));
        assertEquals(12345, sMd.getTimestamp());
        assertEquals(10, sMd.getBodyByteArray().length);

        assertFalse("noreply should be false.", sMd.isNoreply());
    }

    // set product:product_id 0 1234 10\r\n
    // 1234567890\r\n
    private static final byte[] SET_COMMAND                   = { 's', 'e', 't', ' ', 'p', 'r', 'o', 'd', 'u', 'c',
            't', ':', 'p', 'r', 'o', 'd', 'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3', '4', '5', ' ',
            '1', '0', '\r', '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n' };

    // set product:product_id 0 1234 10 noreply \r\n
    // 1234567890\r\n
    private static final byte[] SET_COMMAND_NOREPLY_AND_SPACE = { ' ', ' ', ' ', 's', 'e', 't', ' ', 'p', 'r', 'o',
            'd', 'u', 'c', 't', ':', 'p', 'r', 'o', 'd', 'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3',
            '4', '5', ' ', '1', '0', ' ', '1', ' ', '2', ' ', ' ', '\r', '\n',/* split */'1', '2', '3', '4', '5', '6',
            '7', '8', '9', '0', '\r', '\n'                   };

    private static final byte[] commandHead                   = new byte[] { 'g', 'e', 't', ' ', '1', ':', 'k', 'e',
            'y', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            ' ', '0', ' ', '0', ' ', '0', ' ', '1', ' ', '1', '\r', '\n' };
}
