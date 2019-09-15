package com.aether.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具包
 * @author liuqinfu
 */
@Slf4j
public class VertifyData {

    /**
     * 正则表达式：验证手机号
     */
//    public static final String REGEX_MOBILE = "^((1[345789]))\\d{9}$";
    public static final String REGEX_MOBILE = "\\d+";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    /**
     * 校验手机号格式
     * @param mobile 手机号
     * @return
     */
    public static boolean vertifyMobile(String mobile){
//        if(mobile.length() != 11){
//            log.error("手机号长度不正确:"+mobile);
//            return false;
//        }else{
            Pattern p = Pattern.compile(REGEX_MOBILE);
            Matcher m = p.matcher(mobile);
            boolean isMatch = m.matches();
            if(isMatch){
                return true;
            } else {
                log.error("手机号格式错误："+mobile);
                return false;
            }
//        }
    }

    /**
     * 校验邮箱
     *
     * @param email 邮箱账号
     * @return 校验通过返回true，否则返回false
     */
    public static boolean vertifyMail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

}
