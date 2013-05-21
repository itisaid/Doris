package com.alibaba.doris.dataserver.migrator.task;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.dataserver.migrator.task.migrate.ExitPair;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.Storage;

/**
 * 读取数据的线程，由于多个线程同时读取数据会造成严重的锁冲突，<br>
 * 因此将读数据转移到专门的数据读取线程来执行。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ReadDataThread implements Callable<Integer> {

    public ReadDataThread(Storage storage, List<Integer> vnodeList, BlockingQueue<Pair> dataQueue, int consumerThreadNum) {
        this.storage = storage;
        this.vnodeList = vnodeList;
        this.dataQueue = dataQueue;
        this.consumerThreadNum = consumerThreadNum;
    }

    public Integer call() throws Exception {
        Iterator<Pair> pairItor = storage.iterator(vnodeList);
        int nRecords = 0;

        ClosableIterator<Pair> closablePairItor = (ClosableIterator<Pair>) pairItor;
        if (null != closablePairItor) {
            try {
                while (isRunning && closablePairItor.hasNext()) {
                    nRecords++;
                    dataQueue.put(closablePairItor.next());
                }
            } catch (Exception e) {
                logger.error("ReadDataThread", e);
            } finally {
                closablePairItor.close();
            }
        }

        notifyExitConsumerThread();
        return nRecords;
    }

    /**
     * 通知所有迁移数据线程，终止迁移；
     */
    private void notifyExitConsumerThread() {
        for (int i = 0; i < consumerThreadNum; i++) {
            try {
                dataQueue.put(new ExitPair());
            } catch (InterruptedException e) {
                logger.error("notifyExitConsumerThread", e);
            }
        }
    }

    /**
     * 通知读数据线程，终止并退出读数据线程；
     */
    public void stopReadThread() {
        int dataQueueSize = dataQueue.size();
        if (dataQueueSize > 0) {
            logger.error("The migration queue is not empty after all mitration task have finished; Cleaning the queue. Queue size:"
                         + dataQueueSize);
            dataQueue.clear();
        }
        isRunning = false;
    }

    private List<Integer>       vnodeList;
    private Storage             storage;
    private BlockingQueue<Pair> dataQueue;
    private volatile boolean    isRunning = true;
    private int                 consumerThreadNum;
    protected Logger            logger    = LoggerFactory.getLogger(ReadDataThread.class);
}
