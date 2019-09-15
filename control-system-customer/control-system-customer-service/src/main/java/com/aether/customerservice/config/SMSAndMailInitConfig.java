package com.aether.customerservice.config;

import com.aether.common.listener.CaptachaQueue;
import com.aether.common.utils.SendSMS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuqinfu
 */
@Slf4j
@Configuration
public class SMSAndMailInitConfig {

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Value("${sms.dalu.url}")
    private String url_CN;
    @Value("${sms.dalu.username}")
    private String username_CN;
    @Value("${sms.dalu.password}")
    private String password_CN;
    @Value("${sms.dalu.productid}")
    private String productid_CN;

    @Value("${sms.guoji.url}")
    private String url_EN;
    @Value("${sms.guoji.username}")
    private String username_EN;
    @Value("${sms.guoji.password}")
    private String password_EN;
    @Value("${sms.guoji.productid}")
    private String productid_EN;

    
    @Value("${mail.FROM_MAIL}")
    private String fromMail;

    @Value("${mail.MAIL_PWD}")
    private String mailPwd;

    @Value("${mail.MAIL_HOST}")
    private String mailHost;


    @Value("${sms.location}")
    private String location;

    @Bean
    public CaptachaQueue initCaptachaQueue(){
        if ("CN".equals(location)){
            log.info("短信服务----国内短信通道");
        }else {
            log.warn("短信服务----国际短信通道，请注意资费");
        }
        CaptachaQueue captachaQueue = new CaptachaQueue(location);
        CaptachaQueue.runUpdateData(captachaQueue);
        return  captachaQueue;
    }

    @Bean
    public SendSMS initSendSMS(){
        SendSMS.initSendSMS(url_CN, username_CN, password_CN, productid_CN, url_EN, username_EN, password_EN, productid_EN, fromMail, mailPwd, mailHost);
        return null;
    }

}
