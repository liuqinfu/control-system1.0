package com.aether.zuul.filter;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.PubFinals;
import com.aether.common.utils.JWTUtil;
import com.aether.common.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonEncoding;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class PreCheckFilter extends ZuulFilter {

    /**
     * 既不在ignoreReq中，也不在pointReq中的url，同样需要鉴权
     */
    //需要忽略的请求（全路径！ 如：/lockcustomers/health）
    private static List<String> ignoreReq = new ArrayList<>();

    //需要忽略的请求前缀
    private static List<String> ignorePrefixReq = new ArrayList<>();

    static {
        //siplock-customer-service
        ignoreReq.add("/user/user/forgetpwd");
        ignoreReq.add("/user/common/sms");
        ignoreReq.add("/user/sys/login");
        ignoreReq.add("/user/sys/login/sms");
        ignoreReq.add("/user/sys/registry");
        ignoreReq.add("/user/sys/registry/quick");
        ignoreReq.add("/user/health");

        ignorePrefixReq.add("/api/");
    }

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Object run() {
        // TODO Auto-generated method stub
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        log.warn("开始鉴权");
        String auth_str=request.getHeader(PubFinals.AUTH_STRING);
        //如果鉴权成功
        try {
            RspResult check = authCheck(auth_str);
            String code = check.getCode();
            if (code.equals(CodeFinals.SUCESS_CODE)) {
                //鉴权成功直接执行后续方法
                if (!isValidLogin(auth_str)){
                    //出现多端登陆
                    MappingJackson2HttpMessageConverter jmc=new MappingJackson2HttpMessageConverter();
                    response.setContentType("text/plain;charset=UTF-8");
                    jmc.getObjectMapper().getFactory().createGenerator(response.getOutputStream(), JsonEncoding.UTF8).writeObject(new RspFailResult(CodeFinals.LOGIN_REPEAT_ERROR));
                    ctx.setSendZuulResponse(false);
                }
            }
            else {
                //鉴权失败，返回错误码和错误信息，终止执行后续方法
                MappingJackson2HttpMessageConverter jmc=new MappingJackson2HttpMessageConverter();
                response.setContentType("text/plain;charset=UTF-8");
                jmc.getObjectMapper().getFactory().createGenerator(response.getOutputStream(), JsonEncoding.UTF8).writeObject(check);
                ctx.setSendZuulResponse(false);
            }
        } catch (Exception e) {
            log.error("gatewayException:{}",e.getMessage());
            MappingJackson2HttpMessageConverter jmc=new MappingJackson2HttpMessageConverter();
            response.setContentType("text/plain;charset=UTF-8");
            try {
                jmc.getObjectMapper().getFactory().createGenerator(response.getOutputStream(), JsonEncoding.UTF8).writeObject(new RspFailResult(CodeFinals.EXCEPTION_CODE));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ctx.setSendZuulResponse(false);
        }
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
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //获取请求URL
        String url = request.getRequestURI();
        //服务名
        if (url.contains("/v2/api-docs")){
            return false;
        }
        for (String item:ignoreReq){
            if (url.contains(item)){
                return false;
            }
        }
        //服务内部公共api不鉴权
        for (String urlPrefix : ignorePrefixReq) {
            if (url.contains(urlPrefix)){
                return false;
            }
        }
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
        return 1;
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

    public RspResult authCheck(String auth_str) throws Exception {
        return JWTUtil.check(auth_str);
    }

    /**
     * 鉴权成功之后校验登陆有效性，防止多端登陆
     * @param auth_code  token
     * @return
     */
    private boolean isValidLogin(String auth_code){
        String userIIDD = JWTUtil.getUserIIDD(auth_code);
        //获取redis中对应token
        String login_code = null;
        try {
            login_code = (String) redisUtil.get("check_repeat_"+userIIDD);
        } catch (Exception e) {
            log.error("REDIS ERROR {}",e.getMessage());
        }
        if (!StringUtils.isEmpty(login_code) && login_code.equals(auth_code)){
            log.info("REDIS校验AUTH_CODE通过");
            return true;
        }else {
            //是多端登陆，拒绝请求
            log.info("REDIS校验AUTH_CODE失败");
            return false;
        }

    }
}
