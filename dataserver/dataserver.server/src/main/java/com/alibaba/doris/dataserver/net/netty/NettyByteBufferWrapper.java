package com.alibaba.doris.dataserver.net.netty;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NettyByteBufferWrapper implements ByteBufferWrapper {

    public NettyByteBufferWrapper(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    public byte[] array() {
        return buffer.array();
    }

    public int capacity() {
        return buffer.capacity();
    }

    public void clear() {
        buffer.clear();
    }

    public boolean hasArray() {
        return buffer.hasArray();
    }

    public boolean isDirect() {
        return buffer.isDirect();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public void readBytes(byte[] dst, int dstIndex, int length) {
        buffer.readBytes(dst, dstIndex, length);
    }

    public void readBytes(byte[] dst) {
        buffer.readBytes(dst);
    }

    public void readBytes(ByteBuffer dst) {
        buffer.readBytes(dst);
    }

    public void readBytes(ChannelBuffer dst, int length) {
        buffer.readBytes(dst, length);
    }

    public void readBytes(ChannelBuffer dst) {
        buffer.readBytes(dst);
    }

    public ChannelBuffer readBytes(int length) {
        return buffer.readBytes(length);
    }

    public char readChar() {
        return buffer.readChar();
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public int readInt() {
        return buffer.readInt();
    }

    public long readLong() {
        return buffer.readLong();
    }

    public int readMedium() {
        return buffer.readMedium();
    }

    public short readShort() {
        return buffer.readShort();
    }

    public void writeByte(int value) {
        buffer.writeByte(value);
    }

    public void writeBytes(byte[] src, int srcIndex, int length) {
        buffer.writeBytes(src, srcIndex, length);
    }

    public void writeBytes(byte[] src) {
        buffer.writeBytes(src);
    }

    public void writeBytes(ByteBuffer src) {
        buffer.writeBytes(src);
    }

    public void writeChar(int value) {
        buffer.writeChar(value);
    }

    public void writeDouble(double value) {
        buffer.writeDouble(value);
    }

    public void writeFloat(float value) {
        buffer.writeFloat(value);
    }

    public void writeInt(int value) {
        buffer.writeInt(value);
    }

    public void writeLong(long value) {
        buffer.writeLong(value);
    }

    public void writeMedium(int value) {
        buffer.writeMedium(value);
    }

    public void writeShort(int value) {
        buffer.writeShort(value);
    }

    private ChannelBuffer buffer;
}
