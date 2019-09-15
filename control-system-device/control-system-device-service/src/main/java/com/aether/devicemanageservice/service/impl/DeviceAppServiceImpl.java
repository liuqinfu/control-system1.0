package com.aether.devicemanageservice.service.impl;

import com.aether.devicemanageservice.dao.mapper.DeviceAppMapper;
import com.aether.devicemanageservice.dao.model.DeviceApp;
import com.aether.devicemanageservice.service.DeviceAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuqinfu
 * @date 2019/9/5 08:33
 */
@Service
public class DeviceAppServiceImpl implements DeviceAppService {

    @Autowired
    private DeviceAppMapper deviceAppMapper;

    @Override
    public DeviceApp selectDeviceAppBySn(String sn) {
        return deviceAppMapper.selectDeviceAppBySn(sn.substring(0, 19));
    }
}
