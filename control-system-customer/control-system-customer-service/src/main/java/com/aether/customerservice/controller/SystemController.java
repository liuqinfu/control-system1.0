package com.aether.customerservice.controller;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.PubFinals;
import com.aether.common.finals.SMSTypeFinals;
import com.aether.common.utils.JWTUtil;
import com.aether.common.utils.RedisUtil;
import com.aether.common.utils.StringUtil;
import com.aether.common.utils.VertifyData;
import com.aether.common.utils.qrcode.TwoDimensionCode;
import com.aether.customerapi.entity.User;
import com.aether.customerservice.service.SMSService;
import com.aether.customerservice.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqinfu
 */
@Slf4j
@RestController
@RequestMapping("/sys")
@Api(value = "sys", tags = "登陆注册接口", description = "用户登陆注册接口")
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
public class SystemController {

    @Autowired
    private UserService userService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private RedisUtil redisUtil;

    //二维码服务器域名
    @Value("${QR_CODE_URL_PREFIX}")
    private String QR_CODE_URL_PREFIX;

    //二维码本地存放路径(绝对路径)
    @Value("${QR_LOCAL_PATH}")
    private String QR_LOCAL_PATH;

    /**
     * 用户登录
     *
     * @param request
     * @param loginName 登陆账户
     * @param pwd       密码
     * @return
     */
    @ApiOperation(value = "登陆", notes = "用户登录")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "loginName", value = "登录账户", required = true),
            @ApiImplicitParam(paramType = "query", name = "pwd", value = "登录密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "device_token", value = "设备token（推送使用）", required = true),
            @ApiImplicitParam(paramType = "query", name = "imei", value = "设备IMEI（验证登陆设备使用）", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码（新设备登陆时使用来更新imei）", required = false),
            @ApiImplicitParam(paramType = "query", name = "from", value = "登陆来源（1：app;2:web）", required = false)
    })
    @PostMapping("/login")
    public RspResult login(HttpServletRequest request,
                           @RequestParam("loginName") String loginName,
                           @RequestParam("pwd") String pwd,
                           @RequestParam("device_token") String device_token,
                           @RequestParam("imei") String imei,
                           @RequestParam(value = "code", required = false) String code,
                           @RequestParam(value = "from",required = false,defaultValue = "1")Integer from) {
        try {
            if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(pwd)
                    || StringUtils.isEmpty(device_token) || StringUtils.isEmpty(imei)
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
            //验证用户合法性
            User user = userService.login(loginName, pwd);
            String temp_imep = null;
            if (user == null) {
                return new RspFailResult(CodeFinals.USER_NAME_PWD_FAIL);
            } else {
                if (!loginName.equals("+8613800138000"))
                {
                    if (from != 2){
                        temp_imep = user.getImei();
                        if (StringUtils.isEmpty(code)) {
                            //校验设备imei
                            if (!imei.equals(user.getImei())) {
                                //发送短信或邮箱
                                String account;
                                int accountType = 0;
                                if (!StringUtils.isEmpty((account = user.getMobile()))) {
                                    accountType = 1;
                                } else if (!StringUtils.isEmpty((account = user.getEmail()))) {
                                    accountType = 2;
                                }
                                String captcha = smsService.sendSMS(account, 0, accountType, user.getCountryCode());
                                if (SMSTypeFinals.CODE_SEND_ERROR.equals(captcha)) {
                                    return new RspFailResult(CodeFinals.SEND_CODE_ERROR);
                                } else if (!redisUtil.set("+4" + account, captcha, 60 * 5)) {
                                    log.error("REDIS cache SMS code ERROR while login a unnormal device");
                                }
                                Map bindInfo = new HashMap(1);
                                bindInfo.put("receiver", account);
                                return new RspFailResult(CodeFinals.CHECK_IMEI_ERROR,bindInfo);
                            }
                        } else {
                            //校验验证码
                            String key = "";
                            if (!StringUtils.isEmpty(key = StringUtils.isEmpty(user.getMobile()) ? null : SMSTypeFinals.REDIS_NEW_DEVICE_CHECK_TYPE + user.getMobile()) || !StringUtils.isEmpty(key = StringUtils.isEmpty(user.getEmail()) ? null : SMSTypeFinals.REDIS_NEW_DEVICE_CHECK_TYPE + user.getEmail())) {
                                if (code.equals((String) redisUtil.get(key))) {
                                    redisUtil.setStringRemove(key);
                                } else {
                                    return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
                                }
                            } else {
                                return new RspFailResult();
                            }
                        }
                    }
                }
                //更新设备登陆信息
                user.setDeviceToken(device_token);
                user.setImei(imei);
            }
            //封装返回的令牌和登录用户信息
            Map<String, Object> map_auth = new HashMap<String, Object>(2);
            Map<String, Object> map_return = new HashMap<String, Object>(2);
            //创建令牌
            map_auth.put(PubFinals.AUTH_KEY_USER, user.getLoginName());
            map_auth.put(PubFinals.AUTH_KEY_USER_IIDD, user.getID());
            String auth_return_str = JWTUtil.sign(map_auth);
            //更新数据库登陆令牌同时缓存到redis
//            userService.updateLogin_token(auth_return_str, user.getID());
            user.setLoginToken(auth_return_str);
            if (imei.equals(temp_imep)){
                userService.uploadLoginInfoWithoutImei(user);
            }else{
                userService.uploadLoginInfo(user);
            }
            if (!redisUtil.set("check_repeat_"+user.getID(), auth_return_str)) {
                log.error("缓存用户登陆token失败：" + user.getID() + "--->" + auth_return_str);
            }
            //封装返回的令牌和登录用户信息
            map_return.put(PubFinals.AUTH_STRING, auth_return_str);
            map_return.put(PubFinals.DATA_USER, user);
            return new RspSuccessResult(map_return);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 手机号+验证码免密登陆
     * @param request
     * @param mobile
     * @param device_token
     * @param imei
     * @param code
     * @return
     */
    @ApiOperation(value = "登陆", notes = "用户短信验证码免密登录")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "mobile", value = "手机号", required = true),
            @ApiImplicitParam(paramType = "query", name = "device_token", value = "设备token（推送使用）", required = true),
            @ApiImplicitParam(paramType = "query", name = "imei", value = "设备IMEI", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", required = true)
    })
    @PostMapping("/login/sms")
    public RspResult loginBySMS(HttpServletRequest request,
                                @RequestParam("mobile")String mobile,
                                @RequestParam("device_token")String device_token,
                                @RequestParam("imei")String imei,
                                @RequestParam("code")String code){
        try {
            String loginCode = (String) redisUtil.get(SMSTypeFinals.REDIS_LOGIN_BY_SMS_TYPE + mobile);
            if (StringUtils.isEmpty(loginCode) || !loginCode.equals(code)){
                //验证码已过期或错误
                return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
            } else {
                //验证码正确
                redisUtil.setStringRemove(SMSTypeFinals.REDIS_LOGIN_BY_SMS_TYPE + mobile);
                User user = userService.queryUserByMobile(mobile);
                //更新设备登陆信息
                user.setLoginToken(device_token);
                user.setImei(imei);
                //封装返回的令牌和登录用户信息
                Map<String, Object> map_auth = new HashMap<String, Object>(2);
                Map<String, Object> map_return = new HashMap<String, Object>(2);
                //创建令牌
                map_auth.put(PubFinals.AUTH_KEY_USER, user.getLoginName());
                map_auth.put(PubFinals.AUTH_KEY_USER_IIDD, user.getID());
                String auth_return_str = JWTUtil.sign(map_auth);
                //更新登陆令牌缓存到redis
//                user.setLogin_token(auth_return_str);
                userService.uploadLoginInfo(user);
                if (!redisUtil.set("check_repeat_"+user.getID(), auth_return_str)) {
                    log.error("缓存用户登陆token失败：" + user.getID() + "--->" + auth_return_str);
                }
                //封装返回的令牌和登录用户信息
                map_return.put(PubFinals.AUTH_STRING, auth_return_str);
                map_return.put(PubFinals.DATA_USER, user);
                return new RspSuccessResult(map_return);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 注册
     *
     * @param request
     * @param loginName 登陆账户
     * @param userName  用户姓名
     * @param type      用户类型
     * @param account   手机号或邮箱
     * @param code      验证码
     * @return
     */
    @ApiOperation(value = "注册", notes = "用户注册")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "loginName", value = "登录账户", required = true),
            @ApiImplicitParam(paramType = "query", name = "pwd", value = "登录密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "userName", value = "用户姓名，当type为2，此字段必填", required = false),
            @ApiImplicitParam(paramType = "query", name = "type", value = "用户类型（1个人、2企业）", required = true),
            @ApiImplicitParam(paramType = "query", name = "account", value = "注册手机号或邮箱", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "短信验证码", required = true),
            @ApiImplicitParam(paramType = "query", name = "accountType", value = "账号类型（1：手机号，2：邮箱）", required = true),
            @ApiImplicitParam(paramType = "query", name = "countryCode", value = "手机号地区码（当accountType=1时，必传）", required = false),
            @ApiImplicitParam(paramType = "query", name = "imei", value = "设备IMEI（验证登陆设备使用）", required = true)
    })
    @PostMapping("/registry")
    public RspResult registry(HttpServletRequest request,
                              @RequestParam("loginName") String loginName,
                              @RequestParam("pwd") String pwd,
                              @RequestParam(value = "userName", required = false) String userName,
                              @RequestParam("type") Integer type,
                              @RequestParam("account") String account,
                              @RequestParam("code") String code,
                              @RequestParam("accountType") Integer accountType,
                              @RequestParam(value = "countryCode", required = false) String countryCode,
                              @RequestParam("imei") String imei) {
        try {
            if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(pwd)
                    || StringUtils.isEmpty(type) || StringUtils.isEmpty(account)
                    || StringUtils.isEmpty(code) || (type == 2 && StringUtils.isEmpty(userName))
                    || (type != 1 && type != 2) || StringUtils.isEmpty(accountType)
                    || (accountType != 1 && accountType != 2) || StringUtils.isEmpty(imei)
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
            //验证验证码
            String redisKey = SMSTypeFinals.REDIS_REGIST_TYPE + account;
            String redisCode = (String) redisUtil.get(redisKey);
            if (!code.equals(redisCode)) {
                return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
            } else {
                //校验是否重复注册
                int vertify = 0;
                if (accountType == 1) {
                    vertify = userService.vertifyMobile(account);
                } else {
                    vertify = userService.vertifyMail(account);
                }
                if (vertify > 0) {
                    return new RspFailResult(CodeFinals.REGIST_REPEAT_ERROR);
                }
            }
            redisUtil.setStringRemove(redisKey);
            String ID = StringUtil.get32GUID();
            //根据id生成个人二维码
            String qr_name = account + ".png";
            String content = accountType == 1 ? countryCode + ","+account : ID;
            TwoDimensionCode.encoderQRCode(content, QR_LOCAL_PATH + qr_name, "png");
            String qr_code_url = QR_CODE_URL_PREFIX + qr_name;
            User user = null;
            if (accountType == 1) {
                user = new User(ID, loginName, DigestUtils.md5DigestAsHex(pwd.getBytes()), userName, type, countryCode, account, qr_code_url, new Date(), imei);
            } else {
                user = new User(ID, loginName, DigestUtils.md5DigestAsHex(pwd.getBytes()), userName, type, account, qr_code_url, new Date(), imei, true);
            }
            userService.registry(user, accountType);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }

    }

    /**
     * 注册
     *
     * @param request
     * @param loginName 登陆账户
     * @param userName  用户姓名
     * @param type      用户类型
     * @param account   手机号或邮箱
     * @return
     */
    @ApiOperation(value = "注册", notes = "用户注册")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "loginName", value = "登录账户", required = true),
            @ApiImplicitParam(paramType = "query", name = "userName", value = "用户姓名，当type为2，此字段必填", required = false),
            @ApiImplicitParam(paramType = "query", name = "type", value = "用户类型（1个人、2企业）", required = true),
            @ApiImplicitParam(paramType = "query", name = "account", value = "注册手机号或邮箱", required = true),
            @ApiImplicitParam(paramType = "query", name = "accountType", value = "账号类型（1：手机号，2：邮箱）", required = true),
            @ApiImplicitParam(paramType = "query", name = "countryCode", value = "手机号地区码（当accountType=1时，必传）", required = false),
            @ApiImplicitParam(paramType = "query", name = "imei", value = "设备IMEI（验证登陆设备使用）", required = true)
    })
    @PostMapping("/registry/quick")
    public RspResult registryQuick(HttpServletRequest request,
                                   @RequestParam("loginName") String loginName,
                                   @RequestParam(value = "userName", required = false) String userName,
                                   @RequestParam("type") Integer type,
                                   @RequestParam("account") String account,
                                   @RequestParam("accountType") Integer accountType,
                                   @RequestParam(value = "countryCode", required = false) String countryCode,
                                   @RequestParam("imei") String imei) {
        try {
            if (StringUtils.isEmpty(loginName)
                    || StringUtils.isEmpty(type) || StringUtils.isEmpty(account)
                    || (type == 2 && StringUtils.isEmpty(userName))
                    || (type != 1 && type != 2) || StringUtils.isEmpty(accountType)
                    || (accountType != 1 && accountType != 2) || StringUtils.isEmpty(imei)
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
            //校验是否重复注册
            int vertify = 0;
            if (accountType == 1) {
                vertify = userService.vertifyMobile(account);
            } else {
                vertify = userService.vertifyMail(account);
            }
            if (vertify > 0) {
                return new RspFailResult(CodeFinals.REGIST_REPEAT_ERROR);
            }
            String ID = StringUtil.get32GUID();
            //根据id生成个人二维码
            String qr_name = account + ".png";
            String content = accountType == 1 ? countryCode + ","+account : ID;
            TwoDimensionCode.encoderQRCode(content, QR_LOCAL_PATH + qr_name, "png");
            String qr_code_url = QR_CODE_URL_PREFIX + qr_name;
            //发送密码
            String pwd = smsService.sendPWD(account, accountType, countryCode);
            User user = null;
            if (accountType == 1) {
                user = new User(ID, loginName, DigestUtils.md5DigestAsHex(pwd.getBytes()), userName, type, countryCode, account, qr_code_url, new Date(), imei);
            } else {
                user = new User(ID, loginName, DigestUtils.md5DigestAsHex(pwd.getBytes()), userName, type, account, qr_code_url, new Date(), imei, true);
            }
            userService.registry(user, accountType);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }

    }


    public static void main(String[] args) {
        /*String ID= "df5241d0f3e54ce99c7855f853eca236";
        String qr_name = ID + ".png";
        TwoDimensionCode.encoderQRCode(ID, "/Users/liuqinfu/" + qr_name, "png");*/
        String pwd ="123456";
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(pwd.getBytes());
        System.out.println(md5DigestAsHex);
    }


}
