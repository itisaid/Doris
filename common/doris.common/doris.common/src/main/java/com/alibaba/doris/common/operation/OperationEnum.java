package com.alibaba.doris.common.operation;

public enum OperationEnum {

    READ, WRITE, MIGERATE,
    /**
     * 多读，应用在客户端要求强一致性的场景
     */
    MULTIREAD;
}
