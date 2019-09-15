package com.aether.customerservice.controller;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.PubFinals;
import com.aether.common.finals.SMSTypeFinals;
import com.aether.common.utils.JWTUtil;
import com.aether.common.utils.RedisUtil;
import com.aether.common.utils.VertifyData;
import com.aether.customerservice.service.SMSService;
import com.aether.customerservice.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuqinfu
 */
@Slf4j
@RestController
@RequestMapping("/common")
@Api(value = "common", tags = "公共接口", description = "公共接口相关的api")
@ApiResponses({
        @ApiResponse(code = 200, message = "操作成功", response = RspResult.class),
        @ApiResponse(code = 400, message = "令牌已过期", response = RspResult.class),
        @ApiResponse(code = 401, message = "非法请求", response = RspResult.class),
        @ApiResponse(code = 402, message = "输入数据检查不通过", response = RspResult.class),
        @ApiResponse(code = 500, message = "后台程序异常", response = RspResult.class),
        @ApiResponse(code = 10001, message = "用户名或密码不正确", response = RspResult.class),
        @ApiResponse(code = 10002, message = "密码错误", response = RspResult.class),
        @ApiResponse(code = 10003, message = "验证码错误或已过期", response = RspResult.class),
        @ApiResponse(code = 10004, message = "验证码发送失败", response = RspResult.class),
        @ApiResponse(code = 10005, message = "手机号格式错误", response = RspResult.class),
        @ApiResponse(code = 10006, message = "手机号或邮箱已被注册", response = RspResult.class),
        @ApiResponse(code = 10007, message = "账号已在另一台设备登陆，请重新登陆", response = RspResult.class),
        @ApiResponse(code = 10008, message = "邮箱格式错误", response = RspResult.class),
        @ApiResponse(code = 10009, message = "新设备imei登陆需要验证", response = RspResult.class),
        @ApiResponse(code = 100011, message = "账号不存在", response = RspResult.class)
})
public class CommonController {

    @Autowired
    private SMSService smsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 请求获取短信验证码
     *
     * @param request
     * @param account
     * @param accountType
     * @param type
     * @return
     */
    @ApiOperation(value = "发送验证码", notes = "根据手机号或邮箱查询短信验证码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "account", value = "手机号码或者邮箱账号", required = true),
            @ApiImplicitParam(paramType = "query", name = "type", value = "业务类型（1：注册，2:更换手机或邮箱,3：忘记密码,4:新设备验证,5:验证码登陆）", required = true),
            @ApiImplicitParam(paramType = "query", name = "accountType", value = "账号类型（1：手机号，2：邮箱）", required = true),
            @ApiImplicitParam(paramType = "query", name = "countryCode", value = "手机号国家码（当accountType=1时，必填）", required = false)
    })
    @PostMapping("/sms")
    public RspResult sendSMS(HttpServletRequest request,
                             @RequestParam("account") String account,
                             @RequestParam("accountType") Integer accountType,
                             @RequestParam(value = "type") Integer type,
                             @RequestParam(value = "countryCode", required = false, defaultValue = "") String countryCode) {
        try {
            if (StringUtils.isEmpty(account) || StringUtils.isEmpty(type)
                    || (type != SMSTypeFinals.REGIST_TYPE && type != SMSTypeFinals.REPLACE_ACCOUNT_TYPE && type != SMSTypeFinals.FORGET_PWD_TYPE && type != SMSTypeFinals.NEW_DEVICE_CHECK_TYPE && type != SMSTypeFinals.LOGIN_BY_SMS_TYPE)
                    || StringUtils.isEmpty(accountType) || (accountType != 1 && accountType != 2)
                    || (accountType == 1 && StringUtils.isEmpty(countryCode))
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            if (accountType == 1) {
                if (!VertifyData.vertifyMobile(account)) {
                    return new RspFailResult(CodeFinals.CHECK_MOBILE_ERROR);
                }
            } else {
                if (!VertifyData.vertifyMail(account)) {
                    return new RspFailResult(CodeFinals.CHECK_MAIL_ERROR);
                }
            }
            if (type == SMSTypeFinals.REGIST_TYPE || type == SMSTypeFinals.REPLACE_ACCOUNT_TYPE || type == SMSTypeFinals.FORGET_PWD_TYPE || type == SMSTypeFinals.LOGIN_BY_SMS_TYPE) {
                //校验是否重复注册  或账号不存在
                int vertify = 0;
                if (accountType == 1) {
                    vertify = userService.vertifyMobile(account);
                } else {
                    vertify = userService.vertifyMail(account);
                }
                if (type == SMSTypeFinals.FORGET_PWD_TYPE || type == SMSTypeFinals.NEW_DEVICE_CHECK_TYPE || type == SMSTypeFinals.LOGIN_BY_SMS_TYPE) {
                    if (vertify == 0) {
                        return new RspFailResult(CodeFinals.ACCOUNT_NOT_EXIST);
                    }
                } else {
                    if (vertify > 0) {
                        return new RspFailResult(CodeFinals.REGIST_REPEAT_ERROR);
                    }
                }
            }
            String code = smsService.sendSMS(account, type, accountType, countryCode);
            if (SMSTypeFinals.CODE_SEND_ERROR.equals(code)) {
                return new RspFailResult(CodeFinals.SEND_CODE_ERROR);
            }
            redisUtil.set("+" + type + account, code, 60 * 5);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiIgnore
    @ApiOperation(value = "校验登陆用户名", notes = "校验登陆用户名是否重复")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "loginName", value = "登陆用户名", required = true)})
    @PostMapping("/vertify/loginname")
    public RspResult vertifyLoginName(HttpServletRequest request,
                                      @RequestParam("loginName") String loginName) {
        try {
            if (StringUtils.isEmpty(loginName)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int vertifyLoginName = vertifyLoginName(userService, loginName);
            return new RspSuccessResult(vertifyLoginName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 验证注册输入信息
     *
     * @param request
     * @param telNo   手机号
     * @param code    验证码
     * @return
     */
    @ApiIgnore
    @ApiOperation(value = "验证手机号验证码", notes = "验证手机号与验证码是否匹配")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "telNo", value = "手机号码", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "短信验证码", required = true)})
    @PostMapping("/vertify")
    public RspResult vertifyCode(HttpServletRequest request, @RequestParam("telNo") String telNo, @RequestParam("code") String code) {
        try {
            if (StringUtils.isEmpty(telNo) || StringUtils.isEmpty(code)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            if (!VertifyData.vertifyMobile(telNo)) {
                return new RspFailResult(CodeFinals.CHECK_MOBILE_ERROR);
            }
            String redisCode = (String) redisUtil.get(telNo);
            if (code.equals(redisCode)) {
                redisUtil.setStringRemove(telNo);
                return new RspSuccessResult(code);
            }
            return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }


    /**
     * 检测授权码是否失效
     * @param request
     * @return
     */
    @ApiOperation(value = "检测授权码是否失效", notes = "校验客户端的jwt授权码是否有效")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true)})
    @PostMapping("/checkSign")
    public RspResult checkSign(HttpServletRequest request){
        String auth_str=request.getHeader(PubFinals.AUTH_STRING);
        //鉴权
        return JWTUtil.check(auth_str);
    }

    public static int vertifyLoginName(UserService userService, String loginName) {
        return userService.vertifyLoginName(loginName);
    }
}
