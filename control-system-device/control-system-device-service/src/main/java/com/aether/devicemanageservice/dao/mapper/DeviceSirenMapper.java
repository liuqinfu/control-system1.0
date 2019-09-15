package com.aether.devicemanageservice.dao.mapper;

import com.aether.deviceapi.entity.DeviceSiren;

public interface DeviceSirenMapper {
    int insert(DeviceSiren record);

    int insertSelective(DeviceSiren record);

    /**
     * 更过设备sn删除设备报警状态记录
     * @param deviceSn
     * @return
     */
    int deleteDeviceSirenRecordByDeviceSn(String deviceSn);

}