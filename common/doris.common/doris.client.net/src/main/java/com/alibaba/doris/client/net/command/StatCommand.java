package com.alibaba.doris.client.net.command;

import java.util.concurrent.Semaphore;

import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.OperationFutureImpl;
import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.StatProtocolParser;

public class StatCommand implements Command<String> {

    private String                      viewType;
    private int                         namespace;
    private String                      result;

    private static final ProtocolParser parser = new StatProtocolParser();
    private Semaphore                   signal = new Semaphore(0);
    private OperationFuture<String>     future = new OperationFutureImpl<String>(this, signal);

    public StatCommand(String viewType, int namespace) {
        this.viewType = viewType;
        this.namespace = namespace;
    }

    public String getViewType() {
        return viewType;
    }

    public int getNamespace() {
        return namespace;
    }

    public void complete() {
        signal.release();
    }

    public OperationFuture<String> getResultFuture() {
        return future;
    }

    public ProtocolParser getProtocolParser() {
        return parser;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
