package com.aether.mqttservice.api;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.mqttservice.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqinfu
 * @date 2019/9/5 09:01
 */
@RestController
@RequestMapping("/api")
public class MqttController {

    @Autowired
    private MqttService mqttService;

    /**
     * 发送解除绑定消息
     * @param deviceSn
     * @param loginName
     * @return
     */
    @PostMapping("/unbinddevice/{devicesn}/{loginName}")
    public RspResult unBindDevice(@PathVariable("devicesn")String deviceSn,
                                  @PathVariable("loginName")String loginName){
        try {
            String serialNum = mqttService.sendUnbindDevice(deviceSn, loginName);
            return new RspSuccessResult(serialNum);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }


    /**
     * 发送授权设备给他人消息
     * @param deviceSn  设备sn
     * @param loginName  登录用户名
     * @param pwd  用户密码锁
     * @return
     */
    @PostMapping("/authdevicetoother/{devicesn}/{loginName}/{pwd}")
    public RspResult authDeviceToOthers(@PathVariable("devicesn")String deviceSn,
                                        @PathVariable("loginName")String loginName,
                                        @PathVariable("pwd")String pwd){
        try {
            String serialNum = mqttService.sendAuthToOtherIdea(deviceSn, loginName,pwd);
            return new RspSuccessResult(serialNum);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 发送解除设备授权他人消息
     * @param deviceSn  设备sn
     * @param loginName  用户登录名
     * @return
     */
    @PostMapping("/unauthdevicetoother/{devicesn}/{loginName}")
    public RspResult unAuthDeviceToOthers(@PathVariable("devicesn")String deviceSn,
                                        @PathVariable("loginName")String loginName){
        try {
            String serialNum = mqttService.sendUnauthToOtherIdea(deviceSn, loginName);
            return new RspSuccessResult(serialNum);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }



}
