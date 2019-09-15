package com.aether.devicemanageservice.service;

import com.aether.mqttapi.MqttApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liuqinfu
 * @date 2019/9/5 09:33
 */
@FeignClient(value = "mqttservice-service",path = "/")
public interface MqttService extends MqttApi {
}
