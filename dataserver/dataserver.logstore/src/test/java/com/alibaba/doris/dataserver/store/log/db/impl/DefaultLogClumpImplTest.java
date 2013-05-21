package com.alibaba.doris.dataserver.store.log.db.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.store.log.BaseTestCase;
import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.ReadWindow;
import com.alibaba.doris.dataserver.store.log.db.WriteWindow;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultLogClumpImplTest extends BaseTestCase {

    public void testWriteWindow() {
        clear(this.clumpName);
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);

        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        WriteWindow writeWindow = logClump.getWriteWindow();
        try {
            assertNotNull(writeWindow);
            LogEntry logEntry = generateLogEntry();
            writeWindow.append(logEntry);
        } catch (Throwable e) {
            fail(e.getMessage());
        } finally {
            writeWindow.close();
        }
    }

    private void checkDatabasePath(ClumpConfigure config) {
        String dbPath = config.getPath();
        if (StringUtils.isBlank(dbPath)) {
            throw new LogStorageException("Invalid database path :" + dbPath);
        }

        File f = new File(dbPath);

        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new LogStorageException("Create database path failed. Path:" + dbPath);
            }
        }
    }

    public void testReadWindow() {
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);
        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        ReadWindow readWindow = null;
        try {
            readWindow = logClump.getReadWindow();
            assertNotNull(readWindow);
            int count = 0;
            while (readWindow.hasNext()) {
                LogEntry logEntry = readWindow.next();
                assertNotNull(logEntry);
                if (count++ > 10) {
                    break;
                }
            }
        } catch (Throwable e) {
            fail(e.getMessage());
        } finally {
            readWindow.close();
        }
    }

    public void testReadAndWrite() {
        clear(this.clumpName);
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);
        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        WriteWindow writeWindow = null;
        ReadWindow readWindow = null;
        try {
            writeWindow = logClump.getWriteWindow();
            assertNotNull(writeWindow);
            LogEntry logEntry = generateLogEntry();
            writeWindow.append(logEntry);

            readWindow = logClump.getReadWindow();
            assertNotNull(readWindow);
            assertTrue(readWindow.hasNext());

            LogEntry logNewEntry = readWindow.next();
            assertEquals(logEntry.getKey().getPhysicalKey(), logNewEntry.getKey().getPhysicalKey());
            assertEquals(logEntry.getVnode(), logNewEntry.getVnode());
            assertTrue(logEntry.getValue().equals(logNewEntry.getValue()));
        } finally {
            if (writeWindow != null) {
                writeWindow.close();
            }
            if (readWindow != null) {
                readWindow.close();
            }
        }
    }

    public void testBatchWrite() {
        clear(this.clumpName);
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);
        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        int len = 1000;
        WriteWindow writeWindow = null;
        try {
            writeWindow = logClump.getWriteWindow();
            assertNotNull(writeWindow);
            long start = System.currentTimeMillis();
            for (int i = 0; i < len; i++) {
                LogEntry logEntry = generateLogEntry();
                writeWindow.append(logEntry);
            }
            long end = System.currentTimeMillis();
            System.out.println("Write entries:" + len + " total time(ms):" + (end - start) + " average(ms):"
                               + ((end - start) / len));
        } catch (Throwable e) {
            fail(e.getMessage());
        } finally {
            writeWindow.close();
        }
    }

    public void testBatchWrite2() {
        clear(this.clumpName);
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);
        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        int len = 1000;
        WriteWindow writeWindow = null;
        try {
            writeWindow = logClump.getWriteWindow();
            assertNotNull(writeWindow);
            LogEntry[] logEntryArray = new LogEntry[len];
            long start = System.currentTimeMillis();
            for (int i = 0; i < len; i++) {
                logEntryArray[i] = generateLogEntry();
            }
            writeWindow.append(logEntryArray);
            long end = System.currentTimeMillis();
            System.out.println("Write2 entries:" + len + " total time(ms):" + (end - start) + " average(ms):"
                               + ((end - start) / len));
        } catch (Throwable e) {
            fail(e.getMessage());
        } finally {
            writeWindow.close();
        }
    }

    public void testBatchRead() {
        ClumpConfigure config = getClumpConfigure();
        checkDatabasePath(config);
        DefaultLogClumpImpl logClump = new DefaultLogClumpImpl(config, clumpName);
        ReadWindow readWindow = null;
        try {
            readWindow = logClump.getReadWindow();
            assertNotNull(readWindow);
            long start = System.currentTimeMillis();
            int len = 0;
            while (readWindow.hasNext()) {
                LogEntry logEntry = readWindow.next();
                assertNotNull(logEntry);
                len++;
            }
            long end = System.currentTimeMillis();
            System.out.println("Read entries:" + len + " total time(ms):" + (end - start) + " average(ms):"
                               + ((end - start) / len));
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != readWindow) {
                readWindow.close();
            }
        }
    }

    private String clumpName = "000001";
}
