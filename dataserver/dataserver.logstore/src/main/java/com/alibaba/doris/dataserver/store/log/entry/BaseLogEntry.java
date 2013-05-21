package com.alibaba.doris.dataserver.store.log.entry;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.serialize.KeyValueSerializerFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseLogEntry implements LogEntry {

    public BaseLogEntry(Key key, Value value) {
        setKey(key);
        setValue(value);
    }

    public void setKey(Key key) {
        if (null != key) {
            this.vnode = key.getVNode();
        }
        this.key = key;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public int getVnode() {
        return vnode;
    }

    public void setVnode(int vnode) {
        this.vnode = vnode;
    }

    public Value getValue() {
        return value;
    }

    private Key                         key;
    private Value                       value;
    private int                         vnode;
    protected KeyValueSerializerFactory serializer = KeyValueSerializerFactory.getInstance();
}
