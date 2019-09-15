package com.aether.mqttservice.service;

/**
 * @author liuqinfu
 * @date 2019/9/5 09:11
 */
public interface MqttService {

    /**
     * 发送解除绑定消息，此时并未解除绑定，须等待设备确认
     * @param deviceSn
     * @param loginName
     * @return
     * @throws Exception
     */
    String sendUnbindDevice(String deviceSn,String loginName) throws Exception;

    /**
     * 发送授权消息，此时并未授权，须等待设备确认
     * @param deviceSn  设备sn
     * @param loginName   登录用户名
     * @param pwd   密码
     * @return 消息id
     */
    String sendAuthToOtherIdea(String deviceSn, String loginName,String pwd) throws Exception;

    /**
     * 发送解除授权消息，此时并未解除授权，须等待设备确认
     * @param deviceSn  设备sn
     * @param loginName   用户登录名
     * @return 消息id
     */
    String sendUnauthToOtherIdea(String deviceSn, String loginName) throws Exception;

}
