package com.aether.mqttservice.configration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liuqinfu
 * @date 2019-06-10 14:12
 */
@Component
public class SM4AndSEAkeys {

    //路由器端公钥
    public static String SMS4kEY="B122AD0A7A362EC7ABA1DEEFB3BF4915";
    //服务端私钥
    public static String AESkEY="B122AD0A7A362EC8ABA1DEEFB3BF4915";

    @Value("${SM4AndSEAkeys.SMS4kEY}")
    public void setSMS4kEY(String SMS4kEY) {
        SM4AndSEAkeys.SMS4kEY = SMS4kEY;
    }

    @Value("${SM4AndSEAkeys.AESkEY}")
    public void setAESkEY(String AESkEY) {
        SM4AndSEAkeys.AESkEY = AESkEY;
    }
}
