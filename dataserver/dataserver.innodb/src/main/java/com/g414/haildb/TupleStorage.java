package com.g414.haildb;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import com.g414.haildb.impl.jna.HailDB;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class TupleStorage {
    public static Object coerceType(String value, ColumnType type) {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }

        Object toReturn = null;
        switch (type) {
        case BLOB:
        case BINARY:
        case VARBINARY:
            toReturn = value.getBytes();
            break;
        case CHAR:
        case CHAR_ANYCHARSET:
        case VARCHAR:
        case VARCHAR_ANYCHARSET:
            toReturn = value;
            break;
        case INT:
            toReturn = new BigInteger(value);
            break;
        case DECIMAL:
            toReturn = value;
            break;
        case FLOAT:
        case DOUBLE:
            toReturn = new BigDecimal(value);
            break;
        default:
            throw new IllegalArgumentException("Unsupported Type: " + type);
        }

        return toReturn;
    }

    public static boolean areEqual(Object a, Object b, ColumnType type) {
        if (a == null && b == null) {
            return true;
        }

        if ((a != null && b == null) || (a == null && b != null)) {
            return false;
        }

        if (a instanceof String) {
            a = coerceType((String) a, type);
        }

        if (b instanceof String) {
            b = coerceType((String) b, type);
        }

        switch (type) {
        case BLOB:
        case BINARY:
        case VARBINARY:
            return Arrays.equals((byte[]) a, (byte[]) b);
        case CHAR:
        case CHAR_ANYCHARSET:
        case VARCHAR:
        case VARCHAR_ANYCHARSET:
            return a.equals(b);
        case INT:
            return new BigInteger(a.toString()).equals(new BigInteger(b
                    .toString()));
        case FLOAT:
        case DOUBLE:
            return new BigDecimal(a.toString()).equals(new BigDecimal(b
                    .toString()));
        case DECIMAL:
            return a.toString().equals(b.toString());
        default:
            throw new IllegalArgumentException("Unsupported Type: " + type);
        }
    }

    public static Number loadInteger(Tuple tupl, int i, int length,
            boolean signed) {
        ByteBuffer buf = ByteBuffer.allocateDirect(length);

        if (HailDB.ib_col_get_len(tupl.tupl, i) == HailDB.IB_SQL_NULL) {
            return null;
        }

        switch (length) {
        case 1:
            Util.assertSuccess(HailDB.ib_tuple_read_u8(tupl.tupl, i, buf));
            break;

        case 2:
            ShortBuffer sbuf = buf.asShortBuffer();
            Util.assertSuccess(HailDB.ib_tuple_read_u16(tupl.tupl, i, sbuf));
            break;

        case 4:
            IntBuffer ibuf = buf.asIntBuffer();
            Util.assertSuccess(HailDB.ib_tuple_read_u32(tupl.tupl, i, ibuf));
            break;

        case 8:
            LongBuffer lbuf = buf.asLongBuffer();
            Util.assertSuccess(HailDB.ib_tuple_read_u64(tupl.tupl, i, lbuf));
            break;

        default:
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        byte[] theBytes = new byte[length];
        int k = 0;
        for (int j = length - 1; j >= 0; j--) {
            theBytes[k] = buf.get(j);
            k += 1;
        }
        return signed ? new BigInteger(theBytes) : new BigInteger(1, theBytes);
    }

    public static void storeInteger(Tuple tupl, ColumnDef colDef, int i,
            Number numVal) {
        switch (colDef.getLength()) {
        case 1:
            Util.assertSuccess(HailDB.ib_tuple_write_u8(tupl.tupl, i,
                    numVal.byteValue()));
            break;
        case 2:
            Util.assertSuccess(HailDB.ib_tuple_write_u16(tupl.tupl, i,
                    numVal.shortValue()));
            break;
        case 4:
            Util.assertSuccess(HailDB.ib_tuple_write_u32(tupl.tupl, i,
                    numVal.intValue()));
            break;
        case 8:
            Util.assertSuccess(HailDB.ib_tuple_write_u64(tupl.tupl, i,
                    numVal.longValue()));
            break;
        default:
            throw new IllegalArgumentException(
                    "integer type not supported for length: "
                            + colDef.getLength());
        }
    }

    public static void storeBytes(Tuple tupl, int i, byte[] val) {
        Util.assertSuccess(HailDB.ib_col_set_value(tupl.tupl, i,
                TupleStorage.getDirectMemoryBytes(val), val.length));
    }

    public static byte[] loadBytes(Tuple tupl, int index) {
        int len = HailDB.ib_col_get_len(tupl.tupl, index);
        if (len == HailDB.IB_SQL_NULL) {
            return null;
        }

        return HailDB.ib_col_get_value(tupl.tupl, index).getByteArray(0, len);
    }

    public static void storeString(Tuple tupl, int i, String stringVal) {
        try {
            byte[] stringBytes = stringVal.getBytes("UTF-8");
            Pointer stringPointer = TupleStorage
                    .getDirectMemoryString(stringBytes);
            Util.assertSuccess(HailDB.ib_col_set_value(tupl.tupl, i,
                    stringPointer, stringBytes.length + 1));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Java doesn't recognize UTF-8?!");
        }
    }

    public static String loadString(Tuple tupl, int index) {
        int len = HailDB.ib_col_get_len(tupl.tupl, index);
        if (len == HailDB.IB_SQL_NULL) {
            return null;
        }

        return HailDB.ib_col_get_value(tupl.tupl, index).getString(0);
    }

    private static Pointer getDirectMemoryBytes(byte[] value) {
        if (value == null || value.length == 0) {
            return Pointer.NULL;
        }

        int len = value.length;
        Memory m = new Memory(len);
        m.getByteBuffer(0, len).put(value);

        return Native.getDirectBufferPointer(m.getByteBuffer(0, len));
    }

    public static Pointer getDirectMemoryString(byte[] stringBytes) {
        if (stringBytes == null || stringBytes.length == 0) {
            return Pointer.NULL;
        }

        int length = stringBytes.length + 1;

        Memory m = new Memory(length);
        ByteBuffer buf = m.getByteBuffer(0, length).order(
                ByteOrder.nativeOrder());

        buf.put(stringBytes).put((byte) 0);

        return Native.getDirectBufferPointer(buf);
    }
}
