package com.aether.devicemanageservice.dao.model;

import java.util.Date;

public class Business {
    private String id;

    private String deviceSn;

    private String relationId;

    private String relationUserid;

    private String businessid;

    private Integer type;

    private Integer isdealed;

    private Date creatTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn == null ? null : deviceSn.trim();
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId == null ? null : relationId.trim();
    }

    public String getRelationUserid() {
        return relationUserid;
    }

    public void setRelationUserid(String relationUserid) {
        this.relationUserid = relationUserid == null ? null : relationUserid.trim();
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid == null ? null : businessid.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsdealed() {
        return isdealed;
    }

    public void setIsdealed(Integer isdealed) {
        this.isdealed = isdealed;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}