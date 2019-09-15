package com.aether.devicemanageservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuqinfu
 * @date 2019/9/4 21:47
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(basePackages = "com.aether.devicemanageservice.dao.mapper")
public class DeviceManageApp {
    public static void main(String[] args) {
        SpringApplication.run(DeviceManageApp.class,args);
    }
}
