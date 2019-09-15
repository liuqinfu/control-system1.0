package com.aether.mqttservice.service;

import com.aether.deviceapi.DeviceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "devicemanage-service",path = "/")
public interface DeviceService extends DeviceApi {
}
