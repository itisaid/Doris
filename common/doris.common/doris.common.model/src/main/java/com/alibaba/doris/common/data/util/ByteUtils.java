package com.alibaba.doris.common.data.util;

import java.io.UnsupportedEncodingException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ByteUtils {

    public static String byteToString(byte[] b, int startPos, int len, String charSet) {
        String value = null;
        try {
            value = new String(b, startPos, len, charSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public static String byteToString(byte[] b, int startPos, int len) {
        return byteToString(b, startPos, len, DEFAULT_STRING_CHARSET);
    }

    public static String byteToString(byte[] b) {
        return byteToString(b, 0, b.length, DEFAULT_STRING_CHARSET);
    }

    public static byte[] stringToByte(String value, String charSet) {
        try {
            return value.getBytes(charSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("value=" + value, e);
        }
    }

    public static byte[] stringToByte(String value) {
        return stringToByte(value, DEFAULT_STRING_CHARSET);
    }

    public static final String DEFAULT_STRING_CHARSET = "UTF-8";
}
