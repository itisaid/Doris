package com.alibaba.doris.dataserver.net.netty;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.action.ActionFactory;
import com.alibaba.doris.dataserver.action.CatchCommandErrorAction;
import com.alibaba.doris.dataserver.action.CheckAction;
import com.alibaba.doris.dataserver.action.DeleteAction;
import com.alibaba.doris.dataserver.action.ExitServerAction;
import com.alibaba.doris.dataserver.action.GetAction;
import com.alibaba.doris.dataserver.action.SetAction;
import com.alibaba.doris.dataserver.action.ShutdownAction;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.action.data.SupportBodyActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerDecoderTest extends TestCase {

    public DataServerDecoderTest() {
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

    public void testSetCommandDecode() {
        VirtualRouterImpl.setDebug(true);
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(SET_COMMAND);
        DataServerDecoder decoder = new DataServerDecoder();
        SupportBodyActionData md = null;
        try {
            Object value = decoder.decode(null, null, buffer);
            md = (SupportBodyActionData) value;
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(md);
        assertEquals(10, md.getBodyByteArray().length);
    }

    public void testInvalidSetCommandDecode() {
        VirtualRouterImpl.setDebug(true);
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(INVALID_SET_COMMAND);
        DataServerDecoder decoder = new DataServerDecoder();
        ErrorActionData md = null;
        try {
            Object value = decoder.decode(null, null, buffer);
            md = (ErrorActionData) value;
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(md);
        assertEquals(BaseActionType.ERROR, md.getActionType());
        assertEquals(ErrorActionData.UNKNOWN_COMMAND, md.getCode());
    }

    public void testOneSetInvalidOneSetOKDecode() {
        VirtualRouterImpl.setDebug(true);
        try {
            ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(ONE_SET_INVALID_AND_ONE_SET_OK);
            DataServerDecoder decoder = new DataServerDecoder();

            ErrorActionData md = (ErrorActionData) decoder.decode(null, null, buffer);
            assertNotNull("断言返回一个错误的命令。", md);
            assertEquals(BaseActionType.ERROR, md.getActionType());
            assertEquals(ErrorActionData.CLIENT_ERROR, md.getCode());

            // 测试系统能否正确跳过错误的命令流，并读取到后续正确的命令数据。
            SupportBodyActionData cmd = (SupportBodyActionData) decoder.decode(null, null, buffer);
            assertNotNull("断言系统会自动过滤掉错误的命令数据流，并找到下一个正确的命令序列。", cmd);
            assertEquals(BaseActionType.SET, cmd.getActionType());
            assertEquals(10, cmd.getBodyByteArray().length);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static final byte[] SET_COMMAND                    = { 's', 'e', 't', ' ', 'p', 'r', 'o', 'd', 'u', 'c',
            't', ':', 'p', 'r', 'o', 'd', 'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3', '4', '5', ' ',
            '1', '0', '\r', '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n' };

    private static final byte[] INVALID_SET_COMMAND            = { ' ', 'e', 't', ' ', 'p', 'r', 'o', 'd', 'u', 'c',
            't', ':', 'p', 'r', 'o', 'd', 'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3', '4', '5', ' ',
            '1', '0', '\r', '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n' };

    private static final byte[] ONE_SET_INVALID_AND_ONE_SET_OK = { 's', 'e', 't', ' ', 'p', 'r', 'o', 'd', 'u', 'c',
            't', ':', 'p', 'r', 'o', 'd', 'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3', '4', '5', ' ',
            '1', '0', ' ', 'n', 'r', 'p', 'l', 'y', '\r', '\n', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '\r',
            '\n',/* conmmand two: */'s', 'e', 't', ' ', 'p', 'r', 'o', 'd', 'u', 'c', 't', ':', 'p', 'r', 'o', 'd',
            'u', 'c', 't', '_', 'i', 'd', ' ', '0', ' ', '1', '2', '3', '4', '5', ' ', '1', '0', '\r', '\n', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', '0', '\r', '\n' };
}
