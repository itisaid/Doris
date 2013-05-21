package com.alibaba.doris.dataserver.net;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * ByteBuffer的一个包装类，隔离Netty ChannelBuffer。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ByteBufferWrapper {

    public byte[] array();

    public int capacity();

    public void clear();

    public boolean hasArray();

    public boolean isDirect();

    public byte readByte();

    public ChannelBuffer readBytes(int length);

    public void readBytes(ChannelBuffer dst);

    public void readBytes(byte[] dst);

    public void readBytes(ByteBuffer dst);

    public void readBytes(ChannelBuffer dst, int length);

    public void readBytes(byte[] dst, int dstIndex, int length);

    public char readChar();

    public double readDouble();

    public float readFloat();

    public int readInt();

    public long readLong();

    public int readMedium();

    public short readShort();

    void writeByte(int value);

    void writeShort(int value);

    void writeMedium(int value);

    void writeInt(int value);

    void writeLong(long value);

    void writeChar(int value);

    void writeFloat(float value);

    void writeDouble(double value);

    void writeBytes(byte[] src);

    void writeBytes(byte[] src, int srcIndex, int length);

    void writeBytes(ByteBuffer src);
}
