package com.aether.mqttservice.service.impl;

import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.PubFinals;
import com.aether.common.finals.TradeCodeFinals;
import com.aether.common.utils.BytesUtils;
import com.aether.common.utils.DateUtils;
import com.aether.mqttservice.configration.MqttConfiguration;
import com.aether.mqttservice.mqttbusiness.threads.mqtt.RecBussiness;
import com.aether.mqttservice.service.MqttService;
import com.aether.mqttservice.util.PubStatic;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqinfu
 * @date 2019/9/5 09:13
 */
@Service
public class MqttServiceImpl implements MqttService {


    /**
     * 发送解绑消息，此时并未解绑，须等待设备确认
     * @param deviceSn  设备sn
     * @param loginName  被授权人登录用户名
     * @return
     * @throws Exception
     */
    @Override
    public String sendUnbindDevice(String deviceSn, String loginName) throws Exception {
        String serverRspSerialNum = RecBussiness.getSerialNum();
        Map<String,String> resp = new HashMap<>();
        resp.put("tradeCode", TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_UNBIND_DEVICE);
        resp.put("serverReqSerialNum", serverRspSerialNum);
        resp.put("serverReqSgcmId", deviceSn);
        resp.put("serverReqTime", RecBussiness.getTimeStr());
        String topic = MqttConfiguration.deviceSubTopic+"/"+deviceSn;
        RecBussiness.pushToQueue(resp,topic,(byte) 0x42);
        return serverRspSerialNum;
    }

    /**
     * 发送授权消息，此时并未授权，须等待设备确认
     * @param deviceSn  设备sn
     * @param loginName   被授权人登录用户名
     * @param pwd   被授权人密码
     * @return 消息id
     */
    @Override
    public String sendAuthToOtherIdea(String deviceSn, String loginName,String pwd) throws Exception{
            //发送到设备
            String serverRspSerialNum = RecBussiness.getSerialNum();
            Map<String,String> resp = new HashMap<>();
            resp.put("tradeCode", TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_AUTH_DEVICE_TOOTHER);
            resp.put("serverReqSerialNum", serverRspSerialNum);
            resp.put("serverReqSgcmId", deviceSn);
            resp.put("serverReqUserId", loginName);
            resp.put("serverReqUserPwdAbs", BytesUtils.bytesToHex(pwd.getBytes()));
            resp.put("serverReqTime", RecBussiness.getTimeStr());
            String topic = MqttConfiguration.deviceSubTopic+"/"+deviceSn;
            RecBussiness.pushToQueue(resp,topic,(byte) 0x42);
            return serverRspSerialNum;
    }

    /**
     * 发送解除授权消息，此时并未解除授权，须等待设备确认
     * @param deviceSn  设备sn
     * @param loginName   用户登录名
     * @return 消息id
     */
    @Override
    public String sendUnauthToOtherIdea(String deviceSn,String loginName) throws Exception{
            //发送到设备
            String serverRspSerialNum = RecBussiness.getSerialNum();
            Map<String,String> resp = new HashMap<>();
            resp.put("tradeCode", TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_UNAUTH_DEVICE_TOOTHER);
            resp.put("serverReqSerialNum", serverRspSerialNum);
            resp.put("serverReqSgcmId", deviceSn);
            resp.put("serverReqUserId", loginName);
            resp.put("serverReqTime", RecBussiness.getTimeStr());
            String topic = MqttConfiguration.deviceSubTopic+"/"+deviceSn;
            RecBussiness.pushToQueue(resp,topic,(byte) 0x42);
            return serverRspSerialNum;
    }
}
