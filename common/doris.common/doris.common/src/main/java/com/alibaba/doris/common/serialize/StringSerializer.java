/*
 * Copyright(C) 2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.serialize;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-7-4
 */
public class StringSerializer implements Serializer {

    private final Map<String, Class<?>> numberClassMap = new HashMap<String, Class<?>>();

    public StringSerializer() {
        numberClassMap.put("int", Integer.class);
        numberClassMap.put(Integer.class.getName(), Integer.class);

        numberClassMap.put("long", Long.class);
        numberClassMap.put(Long.class.getName(), Long.class);

        numberClassMap.put("double", Double.class);
        numberClassMap.put(Double.class.getName(), Double.class);

        numberClassMap.put("short", Short.class);
        numberClassMap.put(Short.class.getName(), Short.class);

        numberClassMap.put("float", Float.class);
        numberClassMap.put(Float.class.getName(), Float.class);

        numberClassMap.put("byte", Byte.class);
        numberClassMap.put(Byte.class.getName(), Byte.class);

        numberClassMap.put(BigInteger.class.getName(), BigInteger.class);

        numberClassMap.put(BigDecimal.class.getName(), BigDecimal.class);

    }

    /**
     * serialize
     * 
     * @see com.alibaba.doris.common.serialize.Serializer#serialize(java.lang.Object, java.lang.Object)
     */
    public byte[] serialize(Object o, Object serializeTarget) {

        String svalue = null;
        byte[] bytes = null;
        if (o == null) {
            bytes = BYTES_NULL;
            return bytes;
        }

        String targetClassName = (String) serializeTarget;
        Class<?> targetClass = numberClassMap.get(targetClassName);

        if (o instanceof String || targetClass == null) {
            svalue = (String) o;
        } else if (targetClass != null) {

            // target class is Number type
            String objClassName = o.getClass().getName();
            Class<?> objStoredClass = numberClassMap.get(objClassName);
            if (objStoredClass != null) {

                if (targetClass == objStoredClass) {
                    svalue = String.valueOf(o);
                } else {
                    throw new IllegalArgumentException("Incompatible number value: '" + o + "',  type  '"
                                                       + objStoredClass.getName() + "', expected "
                                                       + targetClass.getName());
                }

            } else {
                throw new IllegalArgumentException("Illegal number type value '" + o + "', expected "
                                                   + targetClass.getName());
            }
        } else {
            // other object type, use toString()
            svalue = String.valueOf(o);
        }

        bytes = ByteUtils.stringToByte(svalue);
        return bytes;
    }

    /**
     * deserialize
     * 
     * @see com.alibaba.doris.common.serialize.Serializer#deserialize(byte[], java.lang.Object)
     */
    public Object deserialize(byte[] bytes, Object deserializeTarget) {

        String targetClassName = (String) deserializeTarget;
        String svalue = null;
        if (bytes.length == 1 && bytes[0] == BYTE_NULL) {
            svalue = null;
            return svalue;
        }

        svalue = ByteUtils.byteToString(bytes);

        if (deserializeTarget == null || targetClassName.trim().length() == 0
            || String.class.getName().equals(deserializeTarget) || "string".equals(targetClassName.toLowerCase())) {
            return svalue;
        }

        Class<?> targetClass = numberClassMap.get(deserializeTarget);

        if (targetClass == int.class || targetClass == Integer.class) {
            return Integer.valueOf(svalue).intValue();
        } else if (targetClass == long.class || targetClass == Long.class) {
            return Long.valueOf(svalue).longValue();
        } else if (targetClass == double.class || targetClass == Double.class) {
            return Double.valueOf(svalue).doubleValue();
        } else if (targetClass == short.class || targetClass == Short.class) {

            return Short.valueOf(svalue).shortValue();

        } else if (targetClass == float.class || targetClass == Float.class) {

            return Float.valueOf(svalue).floatValue();
        } else if (targetClass == byte.class || targetClass == Byte.class) {
            return Byte.valueOf(svalue).byteValue();

        } else if (targetClass == BigInteger.class) {
            long lv = Long.valueOf(svalue);
            return BigInteger.valueOf(lv);
        } else if (targetClass == BigDecimal.class) {

            double dv = Double.valueOf(svalue);
            return BigDecimal.valueOf(dv);
        } else {
            return svalue;
        }
    }

    public static void main(String[] args) {
        System.out.println("null " + ((char) BYTE_NULL));
    }

}
