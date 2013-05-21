package com.alibaba.doris.dataserver.store.serialize;

import java.util.Date;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ValueSerializerTest extends TestCase {

    public void testEncodeAndDecode() {
        KeyValueSerializerFactory factory = KeyValueSerializerFactory.getInstance();
        String value = "这是一个测试字符串，中文，繁體，１２３４２４３其它符號等等";
        byte[] valueBytes = ByteUtils.stringToByte(value);
        Value v = new ValueImpl(valueBytes, (new Date()).getTime());
        v.setFlag((short) 1);

        byte[] tempBytes = factory.encode(v).copyBytes();
        assertNotNull(tempBytes);

        Value newValue = factory.decodeValue(tempBytes);
        assertNotNull(newValue);
        assertEquals(v.getFlag(), newValue.getFlag());
        assertEquals(v.getTimestamp(), newValue.getTimestamp());

        String valueNew = ByteUtils.byteToString(newValue.getValueBytes());
        assertEquals(value, valueNew);
        
        assertNull(factory.decodeValue(new byte[0]));
    }
}
