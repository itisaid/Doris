package com.alibaba.doris.dataserver.store.log.db;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.store.log.db.impl.DeleteLogByVnodesCommand;

/*
 * 负责批量删除数据。
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BatchDeleteThread extends Thread {

    public BatchDeleteThread(BlockingQueue<LogCommand> logCommandQueue, LogClumpManager manager) {
        this.setName("Batch Deleting Thread");
        this.logCommandQueue = logCommandQueue;
        this.manager = manager;
        this.writeByteBuffer = ByteBuffer.allocate(manager.getClumpConfigure().getWriteBufferSize());
    }

    @Override
    public void run() {
        logger.warn("Start running the batch deleting thread...");

        boolean isRuning = true;
        try {
            while (isRuning) {
                LogCommand command = logCommandQueue.take();
                switch (command.getType()) {
                    case DELETE_BY_VNODES: {
                        deleteLogClumpsByVnodes((DeleteLogByVnodesCommand) command);
                        break;
                    }
                    case EXIT:
                        isRuning = false;
                        break;
                    case DELETE_BY_TIMESTAMP:
                    case APPEND:
                    default: {
                        logger.error("Unknown Log command type:" + command.getType());
                        // throw new LogStorageException("Unknown Log command type:" + command.getType());
                        command.setSuccess(false);
                    }
                }
                command.complete();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (logCommandQueue.size() > 0) {
                List<LogCommand> commandList = new ArrayList<LogCommand>();
                if (logCommandQueue.drainTo(commandList) > 0) {
                    for (LogCommand command : commandList) {
                        command.setSuccess(false);
                        command.complete();
                    }
                }
            }
        }

        logger.warn("Exiting batch delete thread.");
    }

    private void deleteLogClumpsByVnodes(DeleteLogByVnodesCommand command) {
        LogClump[] logClumps = command.getLogClumps();
        if (null == logClumps || logClumps.length <= 0) {
            return;
        }

        try {
            boolean isSucess = false;
            for (LogClump logClump : logClumps) {
                if (manager.deleteLogClumpDataByVnodes(logClump, command.getVnodeList(), writeByteBuffer)) {
                    isSucess = true;
                }
            }
            command.setSuccess(isSucess);
        } catch (Exception e) {
            logger.error("Delete log clump failed!", e);
            command.setSuccess(false);
        }
    }

    private ByteBuffer                writeByteBuffer;
    private BlockingQueue<LogCommand> logCommandQueue;
    private LogClumpManager           manager;
    private static final Logger       logger = LoggerFactory.getLogger(BatchDeleteThread.class);
}
