/**
 * Project: skyline.service
 * 
 * File Created at 2011-6-1
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.admin.service.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 根据类型解析数据：字符转成指定的类型， 比如数字。
 * 
 * @author mian.hem
 */
public final class ValueParseUtil {

    public static final String DEF_NULL    = "null";
    public static final String DEF_EMPTY   = "empty";

    private ValueParseUtil() {
    }

    /**
     * 采用缺省的默认值策略,获取内置的默认值,
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getInternalDefaultValue(Class<T> clazz) {
        if (!clazz.isPrimitive()) {
            return null;
        }
        if (Short.TYPE.equals(clazz)) {
            return (T)Short.valueOf((short) 0);
        }
        if (Integer.TYPE.equals(clazz)) {
            return (T)Integer.valueOf(0);
        }
        if (Long.TYPE.equals(clazz)) {
            return (T)Long.valueOf(0);
        }
        if (Boolean.TYPE.equals(clazz)) {
            return (T)Boolean.valueOf(false);
        }
        if (Float.TYPE.equals(clazz)) {
            return (T)Float.valueOf(0);
        }
        if (Double.TYPE.equals(clazz)) {
            return (T)Double.valueOf(0);
        }
        if (Byte.TYPE.equals(clazz)) {
            return (T)Byte.valueOf((byte) 0);
        }
        if (Character.TYPE.equals(clazz)) {
            return (T) Character.valueOf('\0');
        }
        return null;
    }

    /**
     * 转换String类型的值到指定的类型 如果没有定义，则采用缺省的默认值策略:
     * 
     * <pre>
     * short, int, long, float 等数值类型: 0
     * char, byte: 0
     * String: null
     * Map, List: null
     * Integer, Long, Float 等数值对象: null
     * Date: null
     * array: null
     * </pre>
     * 
     * @param strValue
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseStringValue(String strValue, Class<T> clazz,  boolean autoDefault) {

        if (DEF_NULL.equals(strValue)) {
            if (!clazz.isPrimitive()) {
                return null;
            }
            if (autoDefault) {
                return (T) getInternalDefaultValue(clazz);
            } else {
                return null;
            }
        }

        if (DEF_EMPTY.equals(strValue)) {
            if (clazz.isArray()) {
                return (T) Array.newInstance(clazz.getComponentType(), 0);
            }

            if (Map.class.isAssignableFrom(clazz)) {
                return (T) Collections.EMPTY_MAP;
            }

            if (List.class.isAssignableFrom(clazz)) {
                return (T) new ArrayList<Object>();
            }

            if (Set.class.isAssignableFrom(clazz)) {
                return (T) new HashSet<Object>();
            }

            if (String.class.equals(clazz)) {
                return (T) StringUtils.EMPTY;
            }

            if (Character.TYPE.equals(clazz) || Character.class.equals(clazz)) {
                return (T) Character.valueOf(' ');
            }

            if (autoDefault) {
                return (T) getInternalDefaultValue(clazz);
            } else {
                return null;
            }
        }

        if (StringUtils.isBlank(strValue)) {// 采用缺省的默认值策略
            if (autoDefault) {
                return (T) getInternalDefaultValue(clazz);
            } else {
                return null;
            }
        } else {

            if (String.class.equals(clazz)) {
                return (T) strValue;
            }

            if (Short.TYPE.equals(clazz) || Short.class.equals(clazz)) {
                return (T)Short.valueOf(strValue);
            }

            if (Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)) {
                return (T)Integer.valueOf(strValue);
            }
            if (Long.TYPE.equals(clazz) || Long.class.equals(clazz)) {
                return (T)Long.valueOf(strValue);
            }
            if (Boolean.TYPE.equals(clazz) || Boolean.class.equals(clazz)) {
                return (T)Boolean.valueOf(strValue);
            }
            if (Float.TYPE.equals(clazz) || Float.class.equals(clazz)) {
                return (T)Float.valueOf(strValue);
            }
            if (Double.TYPE.equals(clazz) || Double.class.equals(clazz)) {
                return (T)Double.valueOf(strValue);
            }
            if (Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)) {
                return (T)Byte.valueOf(strValue);
            }
            if (Character.TYPE.equals(clazz) || Character.class.equals(clazz)) {
                return (T)Character.valueOf(strValue.charAt(0));
            }

            if (clazz.isArray()) {
                final Class<?> componentType = clazz.getComponentType();
                // String[]
                if (String.class.equals(componentType)) {
                    return (T) StringUtils.split(strValue, ',');
                }
                // 处理char[]
                if (Character.TYPE.equals(componentType)) {
                    return (T) strValue.toCharArray();
                }

                if (Character.class.equals(componentType)) {
                    final char[] tmp = strValue.toCharArray();
                    final Character[] result = new Character[tmp.length];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = tmp[i];
                    }
                    return (T)result;
                }
                
                if (Byte.TYPE.equals(componentType) 
                        || Byte.class.equals(componentType)) {
                    return (T) (strValue == null ? null : strValue.getBytes());
                }
            }
        }
        
        return null;
    
    }
    
    public static <T> T parseStringValue(String strValue, Class<T> clazz) {
        return parseStringValue(strValue, clazz, true);
    }
}
