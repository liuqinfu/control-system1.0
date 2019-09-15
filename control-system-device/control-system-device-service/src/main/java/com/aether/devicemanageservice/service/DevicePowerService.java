package com.aether.devicemanageservice.service;

import com.aether.deviceapi.entity.DevicePower;

public interface DevicePowerService {

    /**
     * 更新设备电量信息
     * @param devicePower
     * @return
     */
    int reportDevicePower(DevicePower devicePower);

}
