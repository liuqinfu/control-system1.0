package com.aether.devicemanageservice.dao.mapper;

import com.aether.deviceapi.entity.DevicePower;

public interface DevicePowerMapper {
    int insert(DevicePower record);

    int insertSelective(DevicePower record);

    /**
     * 删除设备电量记录
     * @param deviceSn
     * @return
     */
    int deleteDevicePower(String deviceSn);
}