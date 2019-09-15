package com.aether.devicemanageservice.service.impl;

import com.aether.deviceapi.entity.DevicePower;
import com.aether.devicemanageservice.dao.mapper.DevicePowerMapper;
import com.aether.devicemanageservice.service.DevicePowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevicePowerServiceImpl implements DevicePowerService {

    @Autowired
    private DevicePowerMapper devicePowerMapper;


    @Override
    public int reportDevicePower(DevicePower devicePower) {
        devicePowerMapper.deleteDevicePower(devicePower.getDevicesn());
        return devicePowerMapper.insert(devicePower);
    }
}
