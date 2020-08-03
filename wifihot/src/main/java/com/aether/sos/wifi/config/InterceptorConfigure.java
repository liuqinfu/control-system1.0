package com.aether.sos.wifi.config;

import com.aether.sos.wifi.interceptor.AccessControlAllowOriginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuqinfu
 */
@Slf4j
@Configuration
public class InterceptorConfigure implements WebMvcConfigurer {


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration interceptorRegistration2 = registry.addInterceptor(new AccessControlAllowOriginInterceptor());
		interceptorRegistration2.addPathPatterns("/**");
	}

}
