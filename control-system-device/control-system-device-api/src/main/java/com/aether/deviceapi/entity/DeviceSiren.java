package com.aether.deviceapi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceSiren {
    private String id;

    private String devicesn;

    private Integer sirenStatus;

    private Date reportTime;

}