package com.aether.devicemanageservice.service.impl;

import com.aether.common.utils.HttpUtil;
import com.aether.devicemanageservice.dao.mapper.DeviceMapper;
import com.aether.devicemanageservice.dao.model.Business;
import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.dto.AuthedUserDTO;
import com.aether.devicemanageservice.dto.BeAuthedDeviceInfoDTO;
import com.aether.devicemanageservice.dto.MineDeviceInfoDTO;
import com.aether.devicemanageservice.service.AuthDeviceService;
import com.aether.devicemanageservice.service.BusinessService;
import com.aether.devicemanageservice.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuqinfu
 * @date 2019/9/5 08:44
 */
@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private AuthDeviceService authDeviceService;

    @Value("{admin.notify.url}")
    private String adminNotifyUrl;

    @Override
    public int insertSelective(Device record) {
        return deviceMapper.insertSelective(record);
    }

    @Override
    public Device selectBindedDeviceBySn(String sn) {
        return deviceMapper.selectBindDeviceBySn(sn);
    }

    @Override
    public int unBindDevice(String sn, String userId) {
        return deviceMapper.unBindDevice(sn,userId);
    }

    @Override
    public List<MineDeviceInfoDTO> selectMineDeviceInfoList(String userId,Integer deviceType) {
        return deviceMapper.selectMineDeviceInfoList(userId,deviceType);
    }

    @Override
    public List<BeAuthedDeviceInfoDTO> selectBeAuthedDeviceInfoList(String userId, Integer deviceType) {
        return deviceMapper.selectBeAuthedDeviceInfoList(userId,deviceType);
    }

    @Override
    public List<AuthedUserDTO> selectAuthedUserInfoList(String sn) {
        return deviceMapper.selectAuthedUserInfoList(sn);
    }

    @Override
    public int activeDevice(String deviceSn, String userId) {
        return deviceMapper.activeDevice(deviceSn,userId);
    }

    @Override
    public int updateAdminKey(String deviceSn, String userId) {
        return deviceMapper.updateAdminKey(deviceSn,userId);
    }

    @Override
    public int unBindDeviceByBusinessId(String businessId) {
        //标记消息为已处理
        businessService.dealMsg(businessId);
        Business business = businessService.selectBusinessByBusinessId(businessId);
        //清除亲情授权
        Device device = deviceMapper.selectBindDeviceBySn(business.getDeviceSn());
        int i = authDeviceService.unauthDeviceByBindId(device.getId());
        //标记设备为已解绑
        return deviceMapper.unBindDeviceByRecordIdAnduserId(business.getRelationId(),business.getRelationUserid());
    }

    @Override
    public int breath(String deviceSn) {
        int breath = deviceMapper.breath(deviceSn);
        //通知管理端html设备已上线
        try {
            //通知html
            //获取管理员用户
            Device device = deviceMapper.selectBindDeviceBySn(deviceSn);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",device.getUserId());
            jsonObject.put("devicesn",deviceSn);
            jsonObject.put("isalive",true);
            String post = HttpUtil.sendPost(adminNotifyUrl, JSONObject.toJSONString(jsonObject));
            log.info("设备下线---通知管理员html成功：url：{}，res:{}",adminNotifyUrl,post);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("设备下线---通知管理员html失败：url：{}，res:{}",adminNotifyUrl);
        }
        return breath;
    }

    @Override
    public int setDeviceToDead(String deviceSn) {
        int toDead = deviceMapper.setDeviceToDead(deviceSn);
        //通知管理端html设备已离线
        try {
            //通知html
            //获取管理员用户
            Device device = deviceMapper.selectBindDeviceBySn(deviceSn);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",device.getUserId());
            jsonObject.put("devicesn",deviceSn);
            jsonObject.put("isalive",false);
            String post = HttpUtil.sendPost(adminNotifyUrl, JSONObject.toJSONString(jsonObject));
            log.info("设备下线---通知管理员html成功：url：{}，res:{}",adminNotifyUrl,post);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("设备下线---通知管理员html失败：url：{}，res:{}",adminNotifyUrl);
        }
        return toDead;
    }


}
