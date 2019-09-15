package com.aether.devicemanageservice.dao.mapper;

import com.aether.deviceapi.entity.ComputerStatus;

public interface ComputerStatusMapper {
    int insert(ComputerStatus record);

    int insertSelective(ComputerStatus record);

    int deleteRecordByDeviceSn(String deviceSn);
}