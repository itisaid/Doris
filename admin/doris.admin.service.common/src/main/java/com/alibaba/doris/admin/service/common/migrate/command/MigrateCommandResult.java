package com.alibaba.doris.admin.service.common.migrate.command;


public class MigrateCommandResult {

    private Boolean result = null;
    private String commandParam;
    
    public Boolean getResult() {
        return result;
    }
    
    public void setResult(Boolean result) {
        this.result = result;
    }
    
    public String getCommandParam() {
        return commandParam;
    }
    
    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }
    
}
