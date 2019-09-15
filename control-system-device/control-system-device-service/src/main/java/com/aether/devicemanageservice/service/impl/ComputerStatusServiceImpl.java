package com.aether.devicemanageservice.service.impl;

import com.aether.common.utils.HttpUtil;
import com.aether.deviceapi.entity.ComputerStatus;
import com.aether.devicemanageservice.dao.mapper.ComputerStatusMapper;
import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.service.ComputerStatusService;
import com.aether.devicemanageservice.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComputerStatusServiceImpl implements ComputerStatusService {

    @Autowired
    private ComputerStatusMapper computerStatusMapper;

    @Autowired
    private DeviceService deviceService;

    @Value("{admin.notify.url}")
    private String adminNotifyUrl;

    @Override
    public int reportComputerStatus(ComputerStatus record) {
        //清除旧记录
        computerStatusMapper.deleteRecordByDeviceSn(record.getDevicesn());
        //新增记录
        int insert = computerStatusMapper.insert(record);
        try {
            //通知html
            //获取管理员用户
            Device device = deviceService.selectBindedDeviceBySn(record.getDevicesn());
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(record));
            jsonObject.put("userId",device.getUserId());
            String post = HttpUtil.sendPost(adminNotifyUrl, JSONObject.toJSONString(jsonObject));
            log.info("设备状态更新---通知管理员html成功：url：{}，res:{}",adminNotifyUrl,post);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("设备状态更新---通知管理员html失败：url：{}，res:{}",adminNotifyUrl);
        }
        return insert;
    }
}
