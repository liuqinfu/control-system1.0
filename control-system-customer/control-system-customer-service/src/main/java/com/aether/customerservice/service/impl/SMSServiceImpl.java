package com.aether.customerservice.service.impl;

import com.aether.common.finals.SMSTypeFinals;
import com.aether.common.listener.CaptachaQueue;
import com.aether.customerservice.service.SMSService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author liuqinfu
 */
@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

    /**
     *
     * @param telNoOrMail
     * @param type  业务类型；1：注册，2：重置,3:更换手机,4：忘记密码
     * @return
     */
    @Override
    public String sendSMS(String telNoOrMail,int type,int accountType,String countryCode) {
        type = 0;
        String note="】，该验证码5分钟内有效。";
        String note_en="】，The captcha will be valid in 5 minutes。";
        String prefix="";
        if (type == SMSTypeFinals.REGIST_TYPE) {
            prefix="您正在注册永达安全锁,";
        }
        if (type ==SMSTypeFinals.REPLACE_ACCOUNT_TYPE) {
            prefix="您正在更换永达安全锁绑定手机号,";
        }
        if (type ==SMSTypeFinals.FORGET_PWD_TYPE) {
            prefix="您正在变更永达安全锁登陆密码,";
        }
        if (type ==SMSTypeFinals.NEW_DEVICE_CHECK_TYPE) {
            prefix="您正在新设备登陆,";
        }
        //开始生成随机数字 -- 验证码
        StringBuffer buffer = new StringBuffer();
        Random random = new Random(); //随机数字
        for (int i = 0; i < 6; i++) {
            buffer.append(random.nextInt(10));//得到六位随机数字
        }
        String content=prefix+"验证码【"+buffer.toString()+note;
        String content_en=prefix+"The captcha【"+buffer.toString()+note_en;
        //加入验证码发送队列
        Map captacha = new HashMap<String,String>(5);
        captacha.put("target", telNoOrMail);
        captacha.put("targetType", accountType);
        captacha.put("countryCode", countryCode);
        captacha.put("content", content);
        captacha.put("content_en", content_en);
        try {
            log.info("captacha:{}",JSONObject.toJSONString(captacha));
            CaptachaQueue.captachaQueue.push(JSONObject.toJSONString(captacha));
        } catch (Exception e) {
            return "-1";
        }
        return buffer.toString();
    }

    @Override
    public String sendPWD(String telNoOrMail,int accountType, String countryCode) {
        String note="】，请妥善保管。";
        String note_en="】, please keep it safe.";
        String prefix="";
        //开始生成随机数字 -- 验证码
        StringBuffer buffer = new StringBuffer();
        Random random = new Random(); //随机数字
        for (int i = 0; i < 6; i++) {
            buffer.append(random.nextInt(10));//得到六位随机数字
        }
        String content=prefix+"您的登陆密码:【"+buffer.toString()+note;
        String content_en=prefix+"Your login password:【"+buffer.toString()+note_en;
        //加入验证码发送队列
        Map captacha = new HashMap<String,String>(5);
        captacha.put("target", telNoOrMail);
        captacha.put("targetType", accountType);
        captacha.put("countryCode", countryCode);
        captacha.put("content", content);
        captacha.put("content_en", content_en);
        try {
            log.info("captacha:{}",JSONObject.toJSONString(captacha));
            CaptachaQueue.captachaQueue.push(JSONObject.toJSONString(captacha));
        } catch (Exception e) {
            return "-1";
        }
        return buffer.toString();
    }
}
