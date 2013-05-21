package com.alibaba.doris.common.data.impl;

import java.util.Properties;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ByteWrapperValueImpl implements Value {

    public ByteWrapperValueImpl(ByteWrapper valueByteWrapper, short flag, long timestamp) {
        this.valueByteWrapper = valueByteWrapper;
        this.flag = flag;
        this.timestamp = timestamp;
    }

    public ByteWrapper getValueByteWrapper() {
        return valueByteWrapper;
    }

    public short getFlag() {
        return this.flag;
    }

    public Properties getProperties() {
        checkAndCopyValue();
        return innerValue.getProperties();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Object getValue() {
        checkAndCopyValue();
        return innerValue.getValue();
    }

    public byte[] getValueBytes() {
        checkAndCopyValue();
        return innerValue.getValueBytes();
    }

    public boolean isCompressed() {
        checkAndCopyValue();
        return innerValue.isCompressed();
    }

    public void setCompressed(boolean b) {
        throw new UnsupportedOperationException();
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public void setProperties(Properties properties) {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public void setValueBytes(byte[] valueBytes) {
        throw new UnsupportedOperationException();
    }

    public CompareStatus compareVersion(Value o) {
        checkAndCopyValue();
        return innerValue.compareVersion(o);
    }

    public Value checkAndCopyValue() {
        if (innerValue == null) {
            byte[] tempBytes = new byte[valueByteWrapper.length()];
            valueByteWrapper.copy(tempBytes, 0);
            innerValue = ValueFactory.createValue(tempBytes, flag, timestamp);
        }

        return innerValue;
    }

    @Override
    public boolean equals(Object obj) {
        checkAndCopyValue();
        return innerValue.equals(obj);
    }

    @Override
    public int hashCode() {
        checkAndCopyValue();
        return innerValue.hashCode();
    }

    private ByteWrapper valueByteWrapper;
    private short       flag;
    private long        timestamp;
    private Value       innerValue;
}
