package com.alibaba.doris.common.config;

/**
 * 此接口用于接收 Doris的配置变更。
 * 
 * @see ConfigManagerImpl
 * @author mianhe
 */
public interface ConfigListener {
	
	public String getConfigListenerName();
	
	public Long getConfigVersion();
	
    /**
     * 配置变更的时候调用此方法
     */
    public void onConfigChange(String configContent);
}
