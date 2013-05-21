package com.alibaba.doris.dataserver.store.log.db;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.store.log.db.LogFile.AccessMode;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogFileTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        File f = new File(getFileName());
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        File f = new File(getFileName());
        if (f.exists()) {
            f.delete();
        }
    }

    public void testOpen() {
        LogFile file = new LogFile(getFileName());
        try {
            file.open(AccessMode.R);
            fail(getFileName() + " should be not exists, and here we should catch an exception.");
        } catch (IOException ignore) {
        } finally {
            file.close();
        }

        try {
            file.open(AccessMode.RW);
            File f = new File(getFileName());
            assertTrue(f.exists());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            file.close();
        }
    }

    public void testWriteAndRead() {
        LogFile file = new LogFile(getFileName());
        LogFile fileRead = new LogFile(getFileName());
        try {
            file.open(AccessMode.RW);
            String testString = "test write;";
            ByteBuffer buf = ByteBuffer.allocate(testString.length());
            buf.put(testString.getBytes());
            buf.flip();
            file.write(buf);

            fileRead.open(AccessMode.R);
            ByteBuffer bufRead = ByteBuffer.allocate(testString.length());
            fileRead.read(bufRead);
            String msg = new String(bufRead.array());
            assertEquals(testString, msg);
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            file.close();
            fileRead.close();
        }
    }

    private String getFileName() {
        return getFileName("testLogFile.txt");
    }

    private String getFileName(String name) {
        String path = LogFileTest.class.getClassLoader().getResource("").getPath();
        return path + name;
    }

}
