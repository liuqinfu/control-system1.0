package com.aether.deviceapi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DevicePower {
    private String id;

    private String devicesn;

    private Integer power;

    private Date reportTime;

}