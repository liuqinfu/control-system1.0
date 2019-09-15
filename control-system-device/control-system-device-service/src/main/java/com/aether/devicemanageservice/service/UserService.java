package com.aether.devicemanageservice.service;

import com.aether.customerapi.CustomerApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liuqinfu
 * @date 2019/9/4 22:20
 */
@FeignClient(value = "customer-service",path = "/")
public interface UserService extends CustomerApi {
}
