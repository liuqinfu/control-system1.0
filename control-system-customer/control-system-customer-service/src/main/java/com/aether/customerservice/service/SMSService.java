package com.aether.customerservice.service;

/**
 * @author liuqinfu
 */
public interface SMSService {

    /**
     * 发送验证码
     * @param telNoOrMail 账号（手机号||邮箱）
     * @param type 业务类型：1：注册，2:更换手机或邮箱,3：忘记密码,4:新设备验证
     * @param accountType 账号类型
     * @param countryCode  手机号国家码
     * @return
     */
    public String sendSMS(String telNoOrMail, int type, int accountType, String countryCode);
    public String sendPWD(String telNoOrMail, int accountType, String countryCode);
}
