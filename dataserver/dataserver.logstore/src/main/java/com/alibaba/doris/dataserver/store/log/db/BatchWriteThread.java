package com.alibaba.doris.dataserver.store.log.db;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.db.impl.AppendLogCommand;
import com.alibaba.doris.dataserver.store.log.db.impl.DeleteLogByVnodesCommand;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/*
 * 负责顺序批量的往磁盘写入数据；
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BatchWriteThread extends Thread {

    public BatchWriteThread(BlockingQueue<LogCommand> logCommandQueue, LogClumpManager manager) {
        this.setName("Batch Writing Thread");
        this.logCommandQueue = logCommandQueue;
        this.manager = manager;
        this.writeByteBuffer = ByteBuffer.allocate(manager.getClumpConfigure().getWriteBufferSize());
    }

    @Override
    public void run() {
        logger.warn("Start running the batch writing thread...");

        boolean isRuning = true;
        deleteLogCommandQueue = new ArrayBlockingQueue<LogCommand>(1000);
        batchDeleteThread = new BatchDeleteThread(deleteLogCommandQueue, manager);
        batchDeleteThread.start();

        List<LogCommand> commandList = new ArrayList<LogCommand>(FATCH_COMMAND_NUMBER_EACH_TIME);
        LogCommand[][] commandQueue = new LogCommand[LogCommand.Type.values().length][FATCH_COMMAND_NUMBER_EACH_TIME];
        int[] commandQueuePos = new int[LogCommand.Type.values().length];

        try {
            while (isRuning) {
                clearCommandQueuePos(commandQueuePos);

                commandList.add(logCommandQueue.take());

                if (logCommandQueue.size() > 0) {
                    logCommandQueue.drainTo(commandList, FATCH_COMMAND_NUMBER_EACH_TIME - 1);
                }
                isRuning = dispatchCommand(commandList, commandQueue, commandQueuePos);

                try {
                    processingCommandQueue(commandQueue, commandQueuePos);
                } catch (Exception e) {
                    logger.error("Failed to execute log command.", e);
                }

                commandList.clear();
            }

            if (currentLogClump != null) {
                currentLogClump.getWriteWindow().close();
            }
        } catch (Exception e) {
            logger.error("BatchWriteThread", e);
        } finally {
            if (logCommandQueue.size() > 0) {
                if (logCommandQueue.drainTo(commandList) > 0) {
                    for (LogCommand command : commandList) {
                        command.setSuccess(false);
                        command.complete();
                    }
                }
            }
        }

        logger.warn("Exiting the batch writing thread.");
    }

    private boolean dispatchCommand(List<LogCommand> commandList, LogCommand[][] commandQueue, int[] commandQueuePos) {
        boolean isRuning = true;

        for (LogCommand command : commandList) {
            switch (command.getType()) {
                case APPEND: {
                    int index = LogCommand.Type.APPEND.ordinal();
                    commandQueue[index][commandQueuePos[index]++] = command;
                    break;
                }
                case DELETE_BY_TIMESTAMP: {
                    int index = LogCommand.Type.DELETE_BY_TIMESTAMP.ordinal();
                    commandQueue[index][commandQueuePos[index]++] = command;
                    break;
                }
                case DELETE_BY_VNODES: {
                    int index = LogCommand.Type.DELETE_BY_VNODES.ordinal();
                    commandQueue[index][commandQueuePos[index]++] = command;
                    break;
                }
                case EXIT:
                    isRuning = false;
                    if (!deleteLogCommandQueue.add(command)) {
                        logger.error("Couldn't notify DeleteLogThread to exiting.");
                    }
                    break;
                default:
                    logger.error("Unknown Log command type:" + command.getType());
                    // throw new LogStorageException("Unknown Log command type:" + command.getType());
            }

            // Once we have received the exit command to break the loop immediately;
            if (!isRuning) {
                break;
            }
        }

        return isRuning;
    }

    private void processingCommandQueue(LogCommand[][] commandQueue, int[] commandQueuePos) throws Exception {
        appendLog(commandQueue[LogCommand.Type.APPEND.ordinal()], commandQueuePos[LogCommand.Type.APPEND.ordinal()]);
        deleteLogByTimestamp(commandQueue[LogCommand.Type.DELETE_BY_TIMESTAMP.ordinal()],
                             commandQueuePos[LogCommand.Type.DELETE_BY_TIMESTAMP.ordinal()]);
        deleteLogByVnodes(commandQueue[LogCommand.Type.DELETE_BY_VNODES.ordinal()],
                          commandQueuePos[LogCommand.Type.DELETE_BY_VNODES.ordinal()]);
    }

    private void appendLog(LogCommand[] appendLogs, int length) throws Exception {
        if (length <= 0) {
            return;
        }

        LogEntry[] logEntryArray = new LogEntry[length];
        for (int i = 0; i < length; i++) {
            logEntryArray[i] = ((AppendLogCommand) appendLogs[i]).getLogEntry();
        }

        LogClump logClump = null;
        try {
            logClump = manager.getLogClump();
            if (currentLogClump != logClump) {
                if (null != currentLogClump) {
                    currentLogClump.getWriteWindow().close();
                }
                currentLogClump = logClump;
            }

            WriteWindow writeWindow = logClump.getWriteWindow(writeByteBuffer);
            writeWindow.append(logEntryArray);
            setCommandResultAndSendingNotifySignal(appendLogs, length, true);
        } catch (Exception e) {
            setCommandResultAndSendingNotifySignal(appendLogs, length, false);
            throw e;
        }

    }

    private void setCommandResultAndSendingNotifySignal(LogCommand[] logCommandArray, int length, boolean result) {
        for (int i = 0; i < length; i++) {
            logCommandArray[i].setSuccess(result);
            logCommandArray[i].complete();
        }
    }

    private void deleteLogByTimestamp(LogCommand[] deleteLogs, int length) {
        if (length <= 0) {
            return;
        }
    }

    private void deleteLogByVnodes(LogCommand[] deleteLogs, int length) {
        if (length <= 0) {
            return;
        }

        LogClump logClump = manager.getLogClump();
        LogClump[] logClumpArray = manager.listAllNonProcessingLogClumps();
        for (int i = 0; i < length; i++) {
            DeleteLogByVnodesCommand command = (DeleteLogByVnodesCommand) deleteLogs[i];
            try {
                boolean isSuccess = manager.deleteLogClumpDataByVnodes(logClump, command.getVnodeList(), null);
                command.setSuccess(isSuccess);
                command.setLogClumps(logClumpArray);
                deleteLogCommandQueue.put(command);
            } catch (InterruptedException e) {
                command.setSuccess(false);
                command.complete();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                command.setSuccess(false);
                command.complete();
                throw new LogStorageException(e);
            }
        }
    }

    private void clearCommandQueuePos(int[] commandQueuePos) {
        for (int i = 0; i < commandQueuePos.length; i++) {
            commandQueuePos[i] = 0;
        }
    }

    private LogClump                  currentLogClump;
    private ByteBuffer                writeByteBuffer;
    private BlockingQueue<LogCommand> logCommandQueue;
    private BlockingQueue<LogCommand> deleteLogCommandQueue;
    private LogClumpManager           manager;
    private BatchDeleteThread         batchDeleteThread;
    private static final int          FATCH_COMMAND_NUMBER_EACH_TIME = 100;
    private static final Logger       logger                         = LoggerFactory.getLogger(BatchWriteThread.class);
}
