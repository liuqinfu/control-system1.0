package com.aether.devicemanageservice.service;

import com.aether.deviceapi.entity.DeviceSiren;

public interface DeviceSirenService {

    /**
     * 更新设备报警状态
     * @param deviceSiren
     * @return
     */
    int updateDeviceSirenStatus(DeviceSiren deviceSiren);
}
