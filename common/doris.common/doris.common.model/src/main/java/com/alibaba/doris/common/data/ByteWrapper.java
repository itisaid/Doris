package com.alibaba.doris.common.data;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ByteWrapper {

    public ByteWrapper(byte[] bytes, int startPos, int len) {
        this.bytes = bytes;
        this.startPos = startPos;
        this.len = len;
    }

    public void copy(byte[] targetBytes, int start) {
        if ((targetBytes.length - start) < len) {
            throw new IllegalArgumentException("The remaining space of the targetBytes array is not enough.");
        }

        System.arraycopy(bytes, startPos, targetBytes, start, len);
    }

    public int length() {
        return len;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] copyBytes() {
        byte[] bs = new byte[len];
        System.arraycopy(bytes, startPos, bs, 0, len);
        return bs;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getLen() {
        return len;
    }

    private byte[] bytes;
    private int    startPos;
    private int    len;
}
