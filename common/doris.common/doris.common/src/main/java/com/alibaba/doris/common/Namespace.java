/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common;


/**
 * Namespace
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-4
 */
public class Namespace {
	
	private int    id;
    /**
     * Namespace名称
     */
    private String name;

    /**
     * className名称
     */
    private String className;

    /**
     * 需要保存的份数
     */
    private int    copyCount;

    /**
     * 第一负责人
     */
    private String firstOwner;

    /**
     *第二负责人
     */
    private String secondOwner;

    /**
     *压缩模式
     */
    private String compressMode;

    /**
     * 压缩阀值大小
     */
    private String compressThreshold;

    /**
     * 序列化方式
     */
    private String serializeMode;

    /**
     * Namespace的状态
     */
    private int    status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否需要多读
     */
    private String multiRead;

    public int getId() {
		return id;
	}
    
    public void setId(int id) {
		this.id = id;
	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
    	 return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public String getFirstOwner() {
        return firstOwner;
    }

    public void setFirstOwner(String firstOwner) {
        this.firstOwner = firstOwner;
    }

    public String getSecondOwner() {
        return secondOwner;
    }

    public void setSecondOwner(String secondOwner) {
        this.secondOwner = secondOwner;
    }

    public String getCompressMode() {
        return compressMode;
    }

    public void setCompressMode(String compressMode) {
        this.compressMode = compressMode;
    }

    public String getCompressThreshold() {
        return compressThreshold;
    }

    public void setCompressThreshold(String compressThreshold) {
        this.compressThreshold = compressThreshold;
    }

    public String getSerializeMode() {
        return serializeMode;
    }

    public void setSerializeMode(String serializeMode) {
        this.serializeMode = serializeMode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String toString() {
        return "[Namespace:" + id +"|" + name + "|" + copyCount + "|" + firstOwner + "|" + status +"]" ;
    }

    public void setMultiRead(String multiRead) {
        this.multiRead = multiRead;
    }

    public String getMultiRead() {
        return multiRead;
    }

    public boolean isMultiRead() {
        return "Y".equalsIgnoreCase(multiRead);
    }
}
