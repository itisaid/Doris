package com.alibaba.doris.client.net;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.doris.client.net.command.BaseCommand;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.ErrorType;
import com.alibaba.doris.client.net.exception.ClientConnectionException;
import com.alibaba.doris.client.net.exception.DataServerErrorException;
import com.alibaba.doris.client.net.exception.InvalidCommandException;
import com.alibaba.doris.client.net.exception.RouteVersionOutOfDateException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class OperationFutureImpl<V> implements OperationFuture<V> {

    public OperationFutureImpl(Command<V> command, Semaphore signel) {
        this.signel = signel;
        this.command = command;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public V get() throws InterruptedException, ExecutionException {
        signel.acquire();
        return getResult();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (signel.tryAcquire(timeout, unit)) {
            return getResult();
        }
        
        if(null != command){
        	throw new TimeoutException(command.toString());
        } else {
        	throw new TimeoutException();
        }
        // return null;
    }

    private V getResult() {
        if (command instanceof BaseCommand<?>) {
            BaseCommand<?> baseCommand = (BaseCommand<?>) command;
            ErrorType errorType = baseCommand.getErrorType();
            if (errorType != null) {
                switch (errorType) {
                    case CLIENT_ERROR: {
                        throw new InvalidCommandException(baseCommand.getErrorMessage());
                    }
                    case SERVER_ERROR: {
                        throw new DataServerErrorException(baseCommand.getErrorMessage());
                    }
                    case VERSION_OUT_OF_DATE: {
                        throw new RouteVersionOutOfDateException("VERSION_OUT_OF_DATE", baseCommand.getErrorMessage());
                    }
                    case CONNECTION: {
                        throw new ClientConnectionException(baseCommand.getErrorMessage());
                    }
                    case UNKNOWN: {
                        throw new NetException(baseCommand.getErrorMessage());
                    }
                    default: {
                        // Nothing to do.
                    }
                }
            }
        }
        return command.getResult();
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        if (signel.availablePermits() > 0) {
            return true;
        }

        return false;
    }

    private Semaphore  signel;
    private Command<V> command;
}
