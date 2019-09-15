package com.aether.devicemanageservice.dao.model;

import lombok.Data;

import java.util.Date;

@Data
public class Device {
    private String id;

    private String deviceSn;

    private String pwd;

    private String deviceName;

    private Integer deviceType;

    private String userId;

    private Integer encryType;

    private String disable;

    private int isactive;

    private Date activeTime;

    private int isalive;

    private Date breathTime;

    private Date createTime;

    private Date updateTime;
}