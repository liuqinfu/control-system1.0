package com.aether.customerservice.service;

import com.aether.mqttapi.MqttApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "mqttservice-service",path = "/")
public interface MqttService extends MqttApi {
}
