package com.aether.devicemanageservice.dao.model;

import lombok.Data;

import java.util.Date;

@Data
public class AuthDevice {
    private String id;

    private String bindId;

    private String otherUserid;

    private String pwd;

    private String businessRecordId;

    private Integer isValid;

    private Date createTime;

    private Date updateTime;

}