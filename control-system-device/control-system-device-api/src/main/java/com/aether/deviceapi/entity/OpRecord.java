package com.aether.deviceapi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OpRecord {
    private String id;

    private String devicesn;

    private String opUserid;

    private String loginName;

    private Date opTime;

    private Integer result;

    private Date createTime;
}