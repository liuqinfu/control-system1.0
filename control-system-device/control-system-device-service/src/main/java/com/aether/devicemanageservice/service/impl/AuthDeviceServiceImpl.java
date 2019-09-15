package com.aether.devicemanageservice.service.impl;

import com.aether.common.utils.StringUtil;
import com.aether.devicemanageservice.dao.mapper.AuthDeviceMapper;
import com.aether.devicemanageservice.dao.model.AuthDevice;
import com.aether.devicemanageservice.dao.model.Business;
import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.service.AuthDeviceService;
import com.aether.devicemanageservice.service.BusinessService;
import com.aether.devicemanageservice.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liuqinfu
 * @date 2019/9/5 15:22
 */
@Service
public class AuthDeviceServiceImpl implements AuthDeviceService {

    @Autowired
    private AuthDeviceMapper authDeviceMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private BusinessService businessService;

    @Override
    public int insert(AuthDevice record) {
        return authDeviceMapper.insert(record);
    }

    @Override
    public int insertSelective(AuthDevice record) {
        return authDeviceMapper.insertSelective(record);
    }


    @Override
    public int unauthDeviceToOther(String businessId) {
        //标记消息已处理
        int i = businessService.dealMsg(businessId);
        Business business = businessService.selectBusinessByBusinessId(businessId);
        //设置授权信息为无效
        Device device = deviceService.selectBindedDeviceBySn(business.getDeviceSn());
        return authDeviceMapper.unauthDeviceToOther(device.getId(), business.getId());
    }

    @Override
    public int unauthDeviceByBindId(String bindId) {
        return authDeviceMapper.unauthDeviceByBindId(bindId);
    }

    @Override
    public int authDeviceToOther(String businessId,String pwd) {
        //标记消息已处理
        int i = businessService.dealMsg(businessId);
        Business business = businessService.selectBusinessByBusinessId(businessId);
        //授权设备给他人
        Device device = deviceService.selectBindedDeviceBySn(business.getDeviceSn());
        AuthDevice authDevice = new AuthDevice();
        authDevice.setId(StringUtil.get32GUID());
        authDevice.setBindId(device.getId());
        authDevice.setOtherUserid(business.getRelationUserid());
        authDevice.setIsValid(1);
        authDevice.setPwd(pwd);
        authDevice.setBusinessRecordId(business.getId());
        authDevice.setCreateTime(new Date());
        return authDeviceMapper.insertSelective(authDevice);
    }


}
