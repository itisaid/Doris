/**
 * Project: doris.admin.core-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-6-27
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
package com.alibaba.doris.admin.core;

import junit.framework.TestCase;

import org.junit.Test;

import com.alibaba.doris.admin.service.impl.ValueParseUtil;

/**
 * TODO Comment of ValueParseUtilTest
 * 
 * @author mian.hem
 */
public class ValueParseUtilTest extends TestCase {

    @Test
    public void testParse() {
        String value = ValueParseUtil.parseStringValue("String", String.class);
        assertEquals("String", value);

        Integer valueI = ValueParseUtil.parseStringValue("111", Integer.class);
        assertEquals(111, valueI.intValue());

        Double valueD = ValueParseUtil.parseStringValue("111.00", Double.class);
        assertEquals(111.00D, valueD.doubleValue());
    }
}
