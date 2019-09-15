package com.aether.devicemanageservice.dto;

import lombok.Data;

/** 已授权用户的授权信息
 * @author liuqinfu
 * @date 2019/9/5 17:30
 */
@Data
public class AuthedUserDTO {

    private String mobile;

    private String countryCode;

    private String headImg;

    private String authTime;

}
