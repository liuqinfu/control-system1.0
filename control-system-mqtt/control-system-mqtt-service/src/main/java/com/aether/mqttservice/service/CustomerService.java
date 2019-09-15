package com.aether.mqttservice.service;

import com.aether.customerapi.CustomerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "customer-service",path = "/")
public interface CustomerService extends CustomerApi {
}
