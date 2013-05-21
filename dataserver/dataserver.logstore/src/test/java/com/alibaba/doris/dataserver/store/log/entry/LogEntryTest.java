package com.alibaba.doris.dataserver.store.log.entry;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.store.log.entry.LogEntry.Type;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogEntryTest extends TestCase {

    public void testLogEntyType() {
        Type[] typeArray = Type.values();
        for (Type t : typeArray) {
            byte code = t.getCode();
            Type decodeType = Type.valueOf(code);
            assertEquals(t, decodeType);
        }
    }
}
