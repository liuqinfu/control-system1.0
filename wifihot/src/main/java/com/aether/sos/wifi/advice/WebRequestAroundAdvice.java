package com.aether.sos.wifi.advice;

import com.aether.sos.wifi.common.utils.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author liuqinfu
 */
@Slf4j
@Component
@Aspect
public class WebRequestAroundAdvice {

    public WebRequestAroundAdvice(){
        log.info("WebRequestAroundAdvice inited");
    }
    @Pointcut( value = "execution(* com.aether.sos.wifi.api.controller.*.*(..))" )
    public void pointcut(){}

    @Around("pointcut()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable{
//        preHandle();

        Object retVal = joinPoint.proceed();

        postHandle(retVal);

        return retVal;
    }


    private void preHandle() {
        if (log.isDebugEnabled()) {
            HttpServletRequest request = ( (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() ).getRequest();

            StringBuffer sb = new StringBuffer();
            sb.append("{");

            Enumeration<String> headers = request.getHeaderNames();
            int i = 0;
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();

                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(header + ": " + request.getHeader(header));
                i++;
            }
            sb.append("}");

            log.debug("Pre handling request: {}, headers: {}", getRequestInfo(request, true), sb.toString());
        }
    }

    private void postHandle(Object retVal) {
        HttpServletRequest request = ( (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() ).getRequest();
        log.info("response  {}--------IP: {}---------response: {}", getRequestInfo(request, false), HttpUtil.getIP(request),JSONObject.toJSONString(retVal));
    }

    private String getRequestInfo(HttpServletRequest request, boolean requestDetails) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getMethod()).append(" ");
        sb.append(request.getRequestURI());
        if (requestDetails) {
            Enumeration<String> e = request.getParameterNames();
            sb.append(" ").append("{");
            int i = 0;
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String val = request.getParameter(name);

                if (val != null && !val.isEmpty()) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(name).append(": ").append(val);

                    i++;
                }
            }
            sb.append("}");
        }

        return sb.toString();
    }
}
