package com.aether.devicemanageservice.service.impl;

import com.aether.deviceapi.entity.DeviceSiren;
import com.aether.devicemanageservice.dao.mapper.DeviceSirenMapper;
import com.aether.devicemanageservice.service.DeviceSirenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceSirenServiceImpl implements DeviceSirenService {

    @Autowired
    private DeviceSirenMapper deviceSirenMapper;

    @Override
    public int updateDeviceSirenStatus(DeviceSiren deviceSiren) {
        deviceSirenMapper.deleteDeviceSirenRecordByDeviceSn(deviceSiren.getDevicesn());
        return deviceSirenMapper.insert(deviceSiren);
    }
}
