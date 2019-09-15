package com.aether.mqttservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuqinfu
 * @date 2019/9/4 16:53
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MqttApp {
    public static void main(String[] args) {
        SpringApplication.run(MqttApp.class,args);
    }
}
