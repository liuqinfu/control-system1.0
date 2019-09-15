package com.aether.common.finals;

public class SMSTypeFinals {
    /**
     * 发送验证码的业务类型
     */
    //注册
    public static final Integer REGIST_TYPE = 1;
    //更换手机或邮箱
    public static final Integer REPLACE_ACCOUNT_TYPE = 2;
    //忘记密码
    public static final Integer FORGET_PWD_TYPE = 3;
    //新设备验证
    public static final Integer NEW_DEVICE_CHECK_TYPE = 4;
    //短信登陆
    public static final Integer LOGIN_BY_SMS_TYPE = 5;

    /**
     * 短信发送接口返回的状态码
     */
    public static final String CODE_SEND_ERROR = "-1";

    /**
     * 验证码存入redis中是标识用途
     */
    //注册
    public static final String REDIS_REGIST_TYPE = "+1";
    //更换手机或邮箱
    public static final String REDIS_REPLACE_ACCOUNT_TYPE = "+2";
    //忘记密码
    public static final String REDIS_FORGET_PWD_TYPE = "+3";
    //新设备验证
    public static final String REDIS_NEW_DEVICE_CHECK_TYPE = "+4";
    //短信登陆
    public static final String REDIS_LOGIN_BY_SMS_TYPE = "+5";
}
