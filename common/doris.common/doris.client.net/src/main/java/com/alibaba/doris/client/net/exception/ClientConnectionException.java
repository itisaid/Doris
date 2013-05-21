package com.alibaba.doris.client.net.exception;

import java.net.InetSocketAddress;

import com.alibaba.doris.client.net.NetException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClientConnectionException extends NetException {

    private static final long serialVersionUID = -7850262442332453656L;

    public ClientConnectionException() {

    }

    /**
     * @param message
     */
    public ClientConnectionException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ClientConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ClientConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InetSocketAddress getRemoteServerAddress() {
        return remoteServerAddress;
    }

    public void setRemoteServerAddress(InetSocketAddress remoteServerAddress) {
        this.remoteServerAddress = remoteServerAddress;
    }

    private InetSocketAddress remoteServerAddress;
}
