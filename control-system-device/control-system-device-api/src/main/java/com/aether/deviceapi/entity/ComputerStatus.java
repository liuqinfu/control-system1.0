package com.aether.deviceapi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ComputerStatus {
    private String id;

    private String devicesn;

    private Integer stateusb;

    private Integer stateminiusb;

    private Integer statehdmi;

    private Integer stateio;

    private Integer statewindowspower;

    private Date reportTime;
}