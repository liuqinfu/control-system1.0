package com.aether.mqttapi;

import com.aether.common.code.RspResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author liuqinfu
 * @date 2019/9/5 09:31
 */
public interface MqttApi {

    /**
     * 发送解除绑定消息
     * @param deviceSn
     * @param userId
     * @return
     */
    @PostMapping("/api/unbinddevice/{devicesn}/{userId}")
    public RspResult unBindDevice(@PathVariable("devicesn")String deviceSn,
                                  @PathVariable("userId")String userId);

    /**
     * 发送授权设备给他人消息
     * @param deviceSn  设备sn
     * @param loginName  登录用户名
     * @param pwd  用户密码锁
     * @return
     */
    @PostMapping("/api/authdevicetoother/{devicesn}/{loginName}/{pwd}")
    public RspResult authDeviceToOthers(@PathVariable("devicesn")String deviceSn,
                                        @PathVariable("loginName")String loginName,
                                        @PathVariable("pwd")String pwd);

    /**
     * 发送解除设备授权他人消息
     * @param deviceSn  设备sn
     * @param userId  用户id
     * @return
     */
    @PostMapping("/api/unauthdevicetoother/{devicesn}/{userId}")
    public RspResult unAuthDeviceToOthers(@PathVariable("devicesn") String deviceSn,
                                          @PathVariable("userId") String userId);
}
