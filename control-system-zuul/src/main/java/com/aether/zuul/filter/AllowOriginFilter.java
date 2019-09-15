package com.aether.zuul.filter;

import com.aether.common.finals.PubFinals;
import com.aether.common.utils.HttpUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@Slf4j
public class AllowOriginFilter extends ZuulFilter {


    @Override
    public Object run() throws ZuulException {
        // TODO Auto-generated method stub
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.warn("跨域设置拦截");
        HttpServletResponse response = ctx.getResponse();
        //增加ajax跨域调用,只支持1个域名，未被拦截器拦截的方法需要单独增加下列代码
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, "+ PubFinals.AUTH_STRING);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        log.info("request	"+request.getMethod()+" "+request.getRequestURI()+"--------IP:" + HttpUtil.getIP(request) + "---------params:" + HttpUtil.dumpHttpRequest(request));
        return null;
    }

    /**
     * 判断该过滤器是否需要被执行。这里我们直接返回了true，因此该过滤器对所有请求都会生效。
     * 实际运用中我们可以利用该函数来指定过滤器的有效范围。
     * @return
     */

    @Override
    public boolean shouldFilter() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * filter执行顺序，通过数字指定。
     * 数字越大，优先级越低。
     * @return
     */

    @Override
    public int filterOrder() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 过滤器的类型，它决定过滤器在请求的哪个生命周期中执行。
     * 这里定义为pre，代表会在请求被路由之前执行。
     * @return
     */

    @Override
    public String filterType() {
        // TODO Auto-generated method stub
        return "pre";
    }
}
