package com.aether.devicemanageservice.dto;

import lombok.Data;

@Data
public class OpRecordInfoDTO {

    private String mobile;

    private String countryCode;

    private String deviceSn;

    private String deviceName;

    private String opTime;

    private Integer result;

}
