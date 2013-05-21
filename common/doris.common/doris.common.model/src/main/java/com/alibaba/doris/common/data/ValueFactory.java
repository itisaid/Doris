package com.alibaba.doris.common.data;

import com.alibaba.doris.common.data.impl.ByteWrapperValueImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ValueFactory {

    public static Value createValue(ByteWrapper valueBytesWrapper, short flag, long timestamp) {
        return new ByteWrapperValueImpl(valueBytesWrapper, flag, timestamp);
    }

    public static Value createValue(byte[] valueBytes, short flag, long timestamp) {
        Value value = new ValueImpl(valueBytes, timestamp);
        value.setFlag(flag);
        return value;
    }

    public static Value createValue(byte[] valueBytes, long timestamp) {
        return new ValueImpl(valueBytes, timestamp);
    }
}
