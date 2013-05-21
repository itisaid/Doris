package com.alibaba.doris.client.net.command;

import java.util.concurrent.Semaphore;

import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.OperationFutureImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseCommand<V> implements Command<V> {

    public int getNamespace() {
        return namespace;
    }

    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public int getRouteVersion() {
        return routeVersion;
    }

    public void setRouteVersion(int routeVersion) {
        this.routeVersion = routeVersion;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void complete() {
        signal.release();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public OperationFuture<V> getResultFuture() {
        if (null == future) {
            future = new OperationFutureImpl<V>(this, signal);
        }
        return future;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    private OperationFuture<V> future;
    private boolean            isSuccess = false;
    private String             errorMessage;
    private ErrorType          errorType;
    protected Semaphore        signal    = new Semaphore(0);
    private int                namespace;
    private int                routeVersion;
}
