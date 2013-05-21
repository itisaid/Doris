/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.data.impl;

import java.util.Properties;

import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.util.FlagUtils;

/**
 * KeyImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-4
 */
public class ValueImpl implements Value {

    private Object     value;
    private long       timestamp;
    private short      flag;
    private Properties properties;
    private byte[]     valueBytes;
    private boolean    compressed;

    @Deprecated
    public ValueImpl(Object value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public ValueImpl(byte[] value) {
        this.valueBytes = value;
        this.timestamp = System.currentTimeMillis();
    }

    public ValueImpl(byte[] value, long timestamp) {
        this.valueBytes = value;
        this.timestamp = timestamp;
    }

    @Deprecated
    public Object getValue() {
        return value;
    }

    @Deprecated
    public void setValue(Object value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        if (valueBytes != null) {
            sb.append("valueLen=" + valueBytes.length);
        } else {
            sb.append("valueLen=0");
        }

        sb.append(" timestamp=" + timestamp);
        return sb.toString();
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }

    public void setValueBytes(byte[] valueBytes) {
        this.valueBytes = valueBytes;
    }

    public CompareStatus compareVersion(Value o) {
        if (null == o) {
            return CompareStatus.AFTER;
        }

        if (o == this) {
            return CompareStatus.EQUALS;
        }

        if (this.timestamp > o.getTimestamp()) {
            return CompareStatus.AFTER;
        } else if (this.timestamp < o.getTimestamp()) {
            return CompareStatus.BEFORE;
        }

        return CompareStatus.EQUALS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Value) {
            Value v = (Value) obj;
            byte[] vBytes = v.getValueBytes();
            byte[] thisVBytes = this.getValueBytes();
            if (vBytes.length != thisVBytes.length) {
                return false;
            }

            for (int i = 0; i < thisVBytes.length; i++) {
                if (thisVBytes[i] != vBytes[i]) {
                    return false;
                }
            }

            if (this.getTimestamp() != v.getTimestamp()) {
                return false;
            }

            if (this.flag != v.getFlag()) {
                return false;
            }
            return true;
        }

        return false;
    }

    public boolean isCompressed() {
        return FlagUtils.isCompressed(flag);
    }

    public void setCompressed(boolean b) {
        if (b) {
            flag = FlagUtils.setCompressed(flag);
        }
    }

}
