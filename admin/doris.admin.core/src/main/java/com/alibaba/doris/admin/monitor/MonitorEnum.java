package com.alibaba.doris.admin.monitor;

public enum MonitorEnum {
    ROUTER("Router"), ADMIN("Admin"), MIGRATION("Migration"), NODE_HEALTH("NodeHealth");

    private String name;

    private MonitorEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
