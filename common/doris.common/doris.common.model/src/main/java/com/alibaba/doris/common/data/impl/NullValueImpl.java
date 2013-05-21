package com.alibaba.doris.common.data.impl;

import java.util.Properties;

import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NullValueImpl implements Value {

    private boolean compressed;

    public CompareStatus compareVersion(Value o) {
        if (null == o) {
            return CompareStatus.EQUALS;
        }

        if (o == this) {
            return CompareStatus.EQUALS;
        }

        if (o instanceof NullValueImpl) {
            return CompareStatus.EQUALS;
        }

        return CompareStatus.BEFORE;
    }

    public short getFlag() {
        return 0;
    }

    public Properties getProperties() {
        return null;
    }

    public long getTimestamp() {
        return 0;
    }

    public Object getValue() {
        return null;
    }

    public byte[] getValueBytes() {
        return null;
    }

    public void setFlag(short flag) {

    }

    public void setProperties(Properties properties) {

    }

    public void setValue(Object value) {

    }

    public void setValueBytes(byte[] valueBytes) {

    }

    public void setTimestamp(long timestamp) {
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean b) {
        this.compressed = b;
    }

    public static final NullValueImpl NULL_VALUE = new NullValueImpl();
}
