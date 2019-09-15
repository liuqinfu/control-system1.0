package com.aether.customerservice.config;

import com.aether.common.finals.PubFinals;
import com.aether.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuqinfu
 */
@Slf4j
public class AccessControlAllowOriginInterceptor implements HandlerInterceptor {

	public AccessControlAllowOriginInterceptor(){
		log.info("AccessControlAllowOriginInterceptor inited");
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) throws Exception {
	}

	/**
	 * 方法执行前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

		log.warn("跨域设置拦截");
		//增加ajax跨域调用,只支持1个域名，未被拦截器拦截的方法需要单独增加下列代码
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, "+ PubFinals.AUTH_STRING);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		log.info("request	"+request.getMethod()+" "+request.getRequestURI()+"--------IP:" + HttpUtil.getIP(request) + "---------params:" + HttpUtil.dumpHttpRequest(request));
		return true;
	}
}

