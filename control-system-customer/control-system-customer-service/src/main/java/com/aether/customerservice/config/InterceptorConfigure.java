package com.aether.customerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuqinfu
 */
@Configuration
public class InterceptorConfigure implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration interceptorRegistration2 = registry.addInterceptor(new AccessControlAllowOriginInterceptor());
		interceptorRegistration2.addPathPatterns("/**");


	}

}
