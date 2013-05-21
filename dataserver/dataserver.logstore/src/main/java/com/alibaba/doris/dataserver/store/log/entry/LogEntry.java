package com.alibaba.doris.dataserver.store.log.entry;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface LogEntry {

    /**
     * 获取当前Entry对应的虚拟节点编号；
     * 
     * @return
     */
    public int getVnode();

    public void setVnode(int vnode);

    /**
     * 获取当前Entry对应值的key
     * 
     * @return
     */
    public Key getKey();

    /**
     * 获取当前Entry对应的Value
     * 
     * @return
     */
    public Value getValue();

    /**
     * 获取当前Entry对应的数据类型。
     * 
     * @return
     */
    public Type getType();

    /**
     * 将当前LogEntry对象编码成二进制流。
     * 
     * @param buffer
     */
    public boolean encode(ByteBuffer buffer);

    /**
     * 从二进制流中读取当前LogEntry的数据。
     * 
     * @param buffer
     */
    public boolean decode(ByteBuffer buffer);

    public enum Type {
        SET(SET_CODE), DELETE(DELETE_CODE);

        private Type(byte code) {
            this.code = code;
        }

        public static Type valueOf(byte typeCode) {
            switch (typeCode) {
                case SET_CODE:
                    return SET;
                case DELETE_CODE:
                    return DELETE;
                default:
                    break;
            }
            throw new RuntimeException("Unknown code:" + typeCode);
        }

        public byte getCode() {
            return this.code;
        }

        private byte code;
    }

    /**
     * 注意：Code不能够被修改，否则文件中已有的数据无法正确识别。
     */
    public static final byte SET_CODE    = 1;
    public static final byte DELETE_CODE = 2;
}
