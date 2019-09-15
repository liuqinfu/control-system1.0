package com.aether.customerapi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EmergencyContact {
    private String id;

    private String userId;

    private Integer relation;

    private String name;

    private String mobile;

    private String countryCode;

    private long createDate;

    private Date updateDate;
}