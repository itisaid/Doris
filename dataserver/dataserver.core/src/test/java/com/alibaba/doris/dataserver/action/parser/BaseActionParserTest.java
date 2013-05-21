package com.alibaba.doris.dataserver.action.parser;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BaseActionParserTest extends TestCase {

    public void testParseNextField() {
        BaseActionParser parser = getBaseActionParser();
        byte[] header = new byte[] { 'f', 'i', 'e', 'l', 'd', '1', ' ', 'f', 'i', 'e', 'l', 'd', '2', '\r', '\n' };
        int[] startPos = new int[] { 0 };
        String f1 = parser.parseNextField(header, startPos);
        String f2 = parser.parseNextField(header, startPos);
        assertEquals("field1", f1);
        assertEquals("field2", f2);

        header = new byte[] { '\r', '\n' };
        startPos = new int[] { 0 };
        String f3 = parser.parseNextField(header, startPos);
        assertNull(f3);

        header = new byte[] { ' ' };
        startPos = new int[] { 0 };
        f3 = parser.parseNextField(header, startPos);
        assertNull(f3);

        header = new byte[] {};
        startPos = new int[] { 0 };
        String f4 = parser.parseNextField(header, startPos);
        assertNull(f4);
    }

    public void testReadRouteVersion() {
        BaseActionParser parser = getBaseActionParser();
        BaseActionData command = new CommonActionData(BaseActionType.GET);
        byte[] header = new byte[] { 'f', 'i', 'e', 'l', 'd', '1', ' ', ' ', ' ', '\r', '\n' };
        int[] startPos = new int[] { 0 };
        String f1 = parser.parseNextField(header, startPos);
        assertEquals("field1", f1);
        parser.readRouteVersion(command, header, startPos);
        assertEquals(BaseActionData.INVALID_ROUTE_VERSION, command.getRouteVersion());

        header = new byte[] { 'f', 'i', 'e', 'l', 'd', '1', '\r', '\n' };
        startPos = new int[] { 0 };
        f1 = parser.parseNextField(header, startPos);
        assertEquals("field1", f1);
        parser.readRouteVersion(command, header, startPos);
        assertEquals(BaseActionData.INVALID_ROUTE_VERSION, command.getRouteVersion());

        header = new byte[] { 'f', 'i', 'e', 'l', 'd', '1', ' ', '1', '\r', '\n' };
        startPos = new int[] { 0 };
        f1 = parser.parseNextField(header, startPos);
        assertEquals("field1", f1);
        parser.readRouteVersion(command, header, startPos);
        assertEquals(1, command.getRouteVersion());

        header = new byte[] { 'f', 'i', 'e', 'l', 'd', '1', ' ', '1', ' ', '\r', '\n' };
        startPos = new int[] { 0 };
        f1 = parser.parseNextField(header, startPos);
        assertEquals("field1", f1);
        parser.readRouteVersion(command, header, startPos);
        assertEquals(1, command.getRouteVersion());
    }

    public void testReadBaseActionData() {
        BaseActionParser parser = getBaseActionParser();
        byte[] header = new byte[] { '1', '2', '3', '4', ':', 'k', 'e', 'y', '1', ' ', '1', '0', '0', '0', ' ', '1',
                ' ' };
        int[] pos = new int[] { 0 };
        CommonActionData actionData = new CommonActionData(BaseActionType.GET);
        parser.readKeyBytes(actionData, header, pos);
        parser.readRouteVersion(actionData, header, pos);
        parser.readVnode(actionData, header, pos);
        assertNotNull(actionData.getKeyBytes());
        assertEquals(9, actionData.getKeyBytes().length);
        assertEquals(1000, actionData.getRouteVersion());
        assertEquals(1, actionData.getVnode());
    }

    private BaseActionParser getBaseActionParser() {
        return new BaseActionParser() {

            public ActionData readHead(byte[] header, int startPos) {
                return null;
            }

            public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {

            }

        };
    }
}
