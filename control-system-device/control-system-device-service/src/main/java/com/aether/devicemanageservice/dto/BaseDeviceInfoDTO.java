package com.aether.devicemanageservice.dto;

import lombok.Data;

/**
 * @author liuqinfu
 * @date 2019/9/5 16:36
 */
@Data
public class BaseDeviceInfoDTO {

    private Integer deviceType;

    private String deviceSn;

    private String deviceName;

    private String belongUser;

    private String pwd;

    private int isalive;

    private int power;

    private int siren;
}
