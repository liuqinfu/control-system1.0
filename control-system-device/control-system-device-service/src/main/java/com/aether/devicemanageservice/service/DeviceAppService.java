package com.aether.devicemanageservice.service;

import com.aether.devicemanageservice.dao.model.DeviceApp;

/**
 * @author liuqinfu
 * @date 2019/9/5 08:33
 */
public interface DeviceAppService {

    /**
     * 根据sn查询该sn对应的app信息
     * @param sn
     * @return
     */
    DeviceApp selectDeviceAppBySn(String sn);
}
