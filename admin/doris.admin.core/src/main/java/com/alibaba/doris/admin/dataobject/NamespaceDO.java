package com.alibaba.doris.admin.dataobject;

import java.util.Date;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-14 下午08:58:15
 * @version :0.1
 * @Modification:
 */
public class NamespaceDO {

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

    private Date   gmtCreate;

    private Date   gmtModified;

    /**
     * 是否需要多读, "Y"或"N"
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMultiRead(String multiRead) {
        this.multiRead = multiRead;
    }

    public String getMultiRead() {
        return multiRead;
    }

}
