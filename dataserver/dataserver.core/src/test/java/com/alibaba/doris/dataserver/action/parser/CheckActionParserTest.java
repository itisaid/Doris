package com.alibaba.doris.dataserver.action.parser;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.action.data.CheckActionData;
import com.alibaba.doris.dataserver.action.data.CheckActionData.CheckType;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckActionParserTest extends TestCase {

    /**
     * 测试check指令解析代码的功能；
     */
    public void testDecode() {
        CheckActionParser parser = new CheckActionParser();
        // 测试解析一个不带任何参数的check指令解析；
        CheckActionData actionData = (CheckActionData) parser.readHead(CHECK_REQUEST_BYTES, 6);
        assertNotNull(actionData);
        // 解析不带任何参数的check指令时，checkType应该为null
        assertNull(actionData.getCheckType());

        actionData = (CheckActionData) parser.readHead(CHECK_REQUEST_BYTES_WITH_SPACE, 6);
        assertNotNull(actionData);
        assertNull(actionData.getCheckType());

        actionData = (CheckActionData) parser.readHead(CHECK_TEMP_NODE_REQUEST_BYTES, 6);
        assertNotNull(actionData);
        assertNotNull("解析带参数的check指令时，checkType不应该为null", actionData.getCheckType());
        assertEquals(actionData.getCheckType(), CheckType.CHECK_TEMP_NODE);

        actionData = (CheckActionData) parser.readHead(CHECK_NORMAL_NODE_REQEST_BYTES, 6);
        assertNotNull(actionData);
        assertNotNull("解析带参数的check指令时，checkType不应该为null", actionData.getCheckType());
        assertEquals(actionData.getCheckType(), CheckType.CHECK_NORMAL_NODE);
    }

    private static byte[] CHECK_REQUEST_BYTES            = new byte[] { 'c', 'h', 'e', 'c', 'k', '\r', '\n' };
    private static byte[] CHECK_REQUEST_BYTES_WITH_SPACE = new byte[] { 'c', 'h', 'e', 'c', 'k', ' ', '\r', '\n' };
    private static byte[] CHECK_TEMP_NODE_REQUEST_BYTES  = new byte[] { 'c', 'h', 'e', 'c', 'k', ' ', 'c', 'h', 'e',
            'c', 'k', '_', 't', 'e', 'm', 'p', '_', 'n', 'o', 'd', 'e', '\r', '\n' };
    private static byte[] CHECK_NORMAL_NODE_REQEST_BYTES = new byte[] { 'c', 'h', 'e', 'c', 'k', ' ', 'c', 'h', 'e',
            'c', 'k', '_', 'n', 'o', 'r', 'm', 'a', 'l', '_', 'n', 'o', 'd', 'e', '\r', '\n' };
}
