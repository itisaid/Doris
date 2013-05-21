package com.alibaba.doris.dataserver.store.log.db.impl;

import java.util.concurrent.Semaphore;

import com.alibaba.doris.dataserver.store.log.db.LogCommand;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseLogCommand implements LogCommand {

    protected BaseLogCommand(boolean isWaitingForCommandCompleted) {
        if (isWaitingForCommandCompleted) {
            signal = new Semaphore(0);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void waitingResult() {
        if (null != signal) {
            try {
                signal.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void complete() {
        if (null != signal) {
            signal.release();
        }
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    private boolean   isSuccess = false;
    private Semaphore signal;
}
