package com.alibaba.doris.dataserver.extratools.replica.action;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExportActionParserTest extends TestCase {

    public void testDecode() {
        ExportActionParser parser = new ExportActionParser();
        ExportActionData actionData = (ExportActionData) parser.readHead(NORMAL_INPUT, 0);
        assertNotNull(actionData);
        assertEquals("/home/doris/export", actionData.getTarget());
        assertEquals("1", actionData.getNamespace());
        assertEquals("1,2", actionData.getVnodes());

        actionData = (ExportActionData) parser.readHead(UNNORMAL_INPUT, 0);
        assertNotNull(actionData);

        actionData = (ExportActionData) parser.readHead(UNNORMAL_INPUT1, 0);
        assertNotNull(actionData);
        assertNull(actionData.getNamespace());
        assertEquals("1,2", actionData.getVnodes());
    }

    public void testEncode() {
        ExportActionParser parser = new ExportActionParser();
        ExportActionData actionData = new ExportActionData();
        actionData.setMessage("10%");

        ByteBufferWrapper buffer = new ByteBufferWrapperImpl(100);
        parser.writeHead(buffer, actionData);

        byte[] resultBytes = buffer.array();
        for (int i = 0; i < NORMAL_OUTPUT.length; i++) {
            assertEquals("index=" + i, NORMAL_OUTPUT[i], resultBytes[i]);
        }
    }

    private byte[] UNNORMAL_INPUT  = new byte[] {};
    private byte[] UNNORMAL_INPUT1 = new byte[] { 'v', 'n', 'o', 'd', 'e', 's', '=', '1', ',', '2', '\r', '\n' };
    private byte[] NORMAL_OUTPUT   = new byte[] { '1', '0', '%', '\r', '\n' };
    private byte[] NORMAL_INPUT    = new byte[] { 't', 'a', 'r', 'g', 'e', 't', '=', '/', 'h', 'o', 'm', 'e', '/', 'd',
            'o', 'r', 'i', 's', '/', 'e', 'x', 'p', 'o', 'r', 't', ' ', 'n', 'a', 'm', 'e', 's', 'p', 'a', 'c', 'e',
            '=', '1', ' ', 'v', 'n', 'o', 'd', 'e', 's', '=', '1', ',', '2', '\r', '\n' };

    public class ByteBufferWrapperImpl implements ByteBufferWrapper {

        public ByteBufferWrapperImpl(int capacity) {
            this.buffer = ByteBuffer.allocate(capacity);
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
            return buffer.get();
        }

        public void readBytes(byte[] dst, int dstIndex, int length) {
            buffer.get(dst, dstIndex, length);
        }

        public void readBytes(byte[] dst) {
            buffer.get(dst);
        }

        public void readBytes(ByteBuffer dst) {

        }

        public void readBytes(ChannelBuffer dst, int length) {
        }

        public void readBytes(ChannelBuffer dst) {
        }

        public ChannelBuffer readBytes(int length) {
            return null;
        }

        public char readChar() {
            return buffer.getChar();
        }

        public double readDouble() {
            return buffer.getDouble();
        }

        public float readFloat() {
            return buffer.getFloat();
        }

        public int readInt() {
            return buffer.getInt();
        }

        public long readLong() {
            return buffer.getLong();
        }

        public int readMedium() {
            throw new RuntimeException();
        }

        public short readShort() {
            return buffer.getShort();
        }

        public void writeByte(int value) {
            buffer.putInt(value);
        }

        public void writeBytes(byte[] src, int srcIndex, int length) {
            buffer.put(src, srcIndex, length);
        }

        public void writeBytes(byte[] src) {
            buffer.put(src);
        }

        public void writeBytes(ByteBuffer src) {
            buffer.put(src);
        }

        public void writeChar(int value) {
            buffer.putChar((char) value);
        }

        public void writeDouble(double value) {
            buffer.putDouble(value);
        }

        public void writeFloat(float value) {
            buffer.putFloat(value);
        }

        public void writeInt(int value) {
            buffer.putInt(value);
        }

        public void writeLong(long value) {
            buffer.putLong(value);
        }

        public void writeMedium(int value) {
            throw new RuntimeException();
        }

        public void writeShort(int value) {
            buffer.putShort((short) value);
        }

        private ByteBuffer buffer;
    }
}
