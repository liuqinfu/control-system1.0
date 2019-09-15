package com.aether.devicemanageservice.dao.mapper;

import com.aether.devicemanageservice.dao.model.DeviceApp;

public interface DeviceAppMapper {
    int insert(DeviceApp record);

    int insertSelective(DeviceApp record);

    /**
     * 根据sn前段查询该sn对应的app信息
     * @param primarySn
     * @return
     */
    DeviceApp selectDeviceAppBySn(String primarySn);
}