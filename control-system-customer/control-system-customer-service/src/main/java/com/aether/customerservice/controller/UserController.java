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
import com.aether.customerapi.entity.EmergencyContact;
import com.aether.customerapi.entity.User;
import com.aether.customerservice.service.EmergencyContactService;
import com.aether.customerservice.service.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author liuqinfu
 */
@Slf4j
@RestController
@RequestMapping("")
@Api(value = "user", tags = "用户管理", description = "除注册登录外的用户个人信息操作接口")
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
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmergencyContactService emergencyContactService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${HEAD_IMG_DOMAIN}")
    private String HEAD_IMG_DOMAIN;

    @Value("${HEAD_IMG_DEST}")
    private String HEAD_IMG_DEST;

    /**
     * 查询所有用户信息
     *
     * @param request
     * @return
     */
    @ApiIgnore
    @ApiOperation(value = "查询用户", notes = "查询所有用户")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true)})
    @GetMapping("/query/all")
    public RspResult queryAllUsers(HttpServletRequest request) {
        try {
            List<User> users = userService.queryAll();
            return new RspSuccessResult(users);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 分页查询所有用户信息
     *
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiIgnore
    @ApiOperation(value = "查询用户", notes = "查询所有用户")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", required = true),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页大小", required = true)})
    @GetMapping("/query/bypage")
    public RspResult queryAllUsers(HttpServletRequest request,
                                   @RequestParam("pageNum") int pageNum,
                                   @RequestParam("pageSize") int pageSize) {
        try {
            if (StringUtils.isEmpty(pageNum) || StringUtils.isEmpty(pageSize)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            Object users = userService.queryByPage(pageNum, pageSize);
            Map res = new HashMap();
            PageInfo pi = (PageInfo) users;
            res.put("datalist", pi.getList());
            res.put("currPage", pi.getPageNum());
            res.put("pages", pi.getPages());
            res.put("pageSize", pi.getPageSize());
            return new RspSuccessResult(res);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }


    /**
     * 更改手机号或绑定邮箱
     *
     * @param request
     * @param account 新手机号
     * @param code    验证码
     * @return
     */
    @ApiOperation(value = "更换手机号或邮箱", notes = "用户更换新的绑定手机号或新的绑定邮箱")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "account", value = "新的手机号或新的邮箱", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "短信验证码", required = true),
            @ApiImplicitParam(paramType = "query", name = "accountType", value = "账号类型（1：手机号 2：邮箱）", required = true),
            @ApiImplicitParam(paramType = "query", name = "countryCode", value = "手机号所属地区码（accountType=1时必传）", required = false)
    })
    @PostMapping("/update/account")
    public RspResult updateMobile(HttpServletRequest request,
                                  @RequestParam("account") String account,
                                  @RequestParam("code") String code,
                                  @RequestParam("accountType") Integer accountType,
                                  @RequestParam(value = "countryCode", required = false) String countryCode) {
        try {
            if (StringUtils.isEmpty(account) || StringUtils.isEmpty(code) || StringUtils.isEmpty(accountType) || (accountType != 1 && accountType != 2)
                    || (accountType == 1 && StringUtils.isEmpty(countryCode))) {
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
            String redisKey = SMSTypeFinals.REDIS_REPLACE_ACCOUNT_TYPE + account;
            String redisCode = (String) redisUtil.get(redisKey);
            if (!code.equals(redisCode)) {
                return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
            } else {
                redisUtil.setStringRemove(redisKey);
                String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
                if (accountType == 1) {
                    //修改手机号
                    userService.updateMobile(account, ID, countryCode);
                } else {
                    //修改绑定邮箱
                    userService.updateMail(account, ID);
                }
                return new RspSuccessResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiIgnore
    @ApiOperation(value = "校验密码", notes = "校验原密码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "pwd", value = "原密码", required = true)})
    @PostMapping("/vertify/pwd")
    public RspResult vertifyPwd(HttpServletRequest request,
                                @RequestParam("pwd") String pwd) {
        try {
            if (StringUtils.isEmpty(pwd)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
            String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            User user = userService.vertifyPwd(ID, pwd);
            if (user != null) {
                return new RspSuccessResult(user);
            }
            return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 用户忘记密码，通过注册手机号或注册邮箱重置密码
     *
     * @param request
     * @param account 手机号
     * @param code    验证码
     * @param pwd     密码
     * @return
     */
    @ApiOperation(value = "忘记密码", notes = "通过注册手机号或注册邮箱重置密码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "account", value = "手机号或邮箱", required = true),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", required = true),
            @ApiImplicitParam(paramType = "query", name = "pwd", value = "新密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "accountType", value = "账号类型（1：手机号 2：邮箱）", required = true)
    })
    @PostMapping("/forgetpwd")
    public RspResult forgetPassword(HttpServletRequest request,
                                    @RequestParam("account") String account,
                                    @RequestParam("code") String code,
                                    @RequestParam("pwd") String pwd,
                                    @RequestParam("accountType") Integer accountType) {
        try {
            if (StringUtils.isEmpty(account) || StringUtils.isEmpty(code) || StringUtils.isEmpty(pwd) || StringUtils.isEmpty(accountType) || (accountType != 1 && accountType != 2)) {
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
            //校验短信验证码
            String redisKey = SMSTypeFinals.REDIS_FORGET_PWD_TYPE + account;
            String redisCode = (String) redisUtil.get(redisKey);
            if (!code.equals(redisCode)) {
                return new RspFailResult(CodeFinals.CHECK_CODE_ERROR);
            } else {
                redisUtil.setStringRemove(redisKey);
                //修改密码
                pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
                User user = new User();
                if (accountType == 1) {
                    user.setMobile(account);
                } else {
                    user.setEmail(account);
                }
                user.setPassword(pwd);
                try {
                    int res = userService.forgetPwd(user, accountType);
                    if (res <1){
                        return new RspFailResult(CodeFinals.NO_ORIGN_USER);
                    }
                } catch (Exception e) {
                   log.error(e.getMessage());
                    return new RspFailResult(CodeFinals.EXCEPTION_CODE);
                }
                return new RspSuccessResult(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 更新用户信息
     *
     * @param request
     * @param loginName 登陆账号
     * @param userName  用户姓名
     * @param type      用户类型
     * @param tel_phone 住宅电话
     * @return
     */
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息（不能所有数据都为空！）")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "loginName", value = "新登陆账户", required = false),
            @ApiImplicitParam(paramType = "query", name = "sex", value = "性别（0女、1男）", required = false),
            @ApiImplicitParam(paramType = "query", name = "userName", value = "新用户姓名", required = false),
            @ApiImplicitParam(paramType = "query", name = "type", value = "新用户类型（1个人、2企业）", required = false),
            @ApiImplicitParam(paramType = "query", name = "tel_phone", value = "新住宅电话", required = false)})
    @PostMapping("/update/userinfo")
    public RspResult updateUserInfo(HttpServletRequest request,
                                    @RequestParam(value = "loginName", required = false) String loginName,
                                    @ApiParam(name = "head_img", value = "用户头像", required = false) @RequestParam(value = "head_img", required = false) MultipartFile head_img,
                                    @RequestParam(value = "sex", required = false) Integer sex,
                                    @RequestParam(value = "userName", required = false) String userName,
                                    @RequestParam(value = "type", required = false) Integer type,
                                    @RequestParam(value = "tel_phone", required = false) String tel_phone
    ) {
        try {
            if (StringUtils.isEmpty(loginName)
                    && StringUtils.isEmpty(userName) && StringUtils.isEmpty(type)
                    && StringUtils.isEmpty(tel_phone)
                    && StringUtils.isEmpty(sex) && head_img == null
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            if (type != null) {
                if ((type != 1 && type != 2) || (type == 2 && StringUtils.isEmpty(userName))) {
                    return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
                }
            }
            if (sex != null && sex != 1 && sex != 0) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            if (!StringUtils.isEmpty(loginName)) {
                //校验登陆用户名是否重复
                int vertifyLoginName = CommonController.vertifyLoginName(userService, loginName);
                if (vertifyLoginName > 1) {
                    return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
                }
            }
            String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            User user = new User();
            if (head_img != null) {
                //上传文件
                String originalFilename = head_img.getOriginalFilename();
                String extention = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                if (!"jpg".equalsIgnoreCase(extention) && ! "png".equalsIgnoreCase(extention)
                        && ! "jpeg".equalsIgnoreCase(extention)) {
                    return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
                }
                String headImgName = ID + "_head_" + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
                File headImgDest = new File(HEAD_IMG_DEST + headImgName);
                head_img.transferTo(headImgDest);
                user.setHeadImg(HEAD_IMG_DOMAIN + headImgName);

            }
            user.setID(ID);
            user.setLoginName(loginName);
            user.setSex(sex);
            user.setUserName(userName);
            user.setType(type);
            user.setTelPhone(tel_phone);
            user.setUpdateTime(new Date());
            userService.updateUserInfoByID(user);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "更改密码", notes = "根据原密码修改新密码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "oldPwd", value = "原密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "newPwd", value = "新密码", required = true)})
    @PostMapping("/updatepwd")
    public RspResult updatePassword(HttpServletRequest request,
                                    @RequestParam("oldPwd") String oldPwd,
                                    @RequestParam("newPwd") String newPwd
    ) {
        try {
            if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            String orignPwd = newPwd;
            oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
            newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
            try {
                int updatePwd = userService.updatePwd(oldPwd, newPwd, ID);
                if (updatePwd > 0) {
                    return new RspSuccessResult(updatePwd);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return new RspFailResult(CodeFinals.EXCEPTION_CODE);
            }
            return new RspFailResult(CodeFinals.NO_ORIGN_USER);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "自动推送密码",notes = "设置是否自动推送密码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "query", name = "auto_push_pwd", value = "开关（ 1：开启  0：关闭）", required = true)
    })
    @PostMapping("/setting/autopushpwd")
    public RspResult setAutoPushPwd(HttpServletRequest request,
                                    @RequestParam("auto_push_pwd")Integer isOpen){
        try {
            if (StringUtils.isEmpty(isOpen) || (isOpen != 0  && isOpen != 1) ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            int autoPushPwd = userService.setAutoPushPwd(isOpen, ID);
            return new RspSuccessResult(autoPushPwd);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "紧急联系人",notes = "设置紧急联系人")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true)
    })
    @PostMapping("/emergency/add")
    public RspResult addEmergencyContact(HttpServletRequest request,
                                         @RequestParam("list") String emergencyContactStr){
        try {
            JSONArray array = (JSONArray) JSONObject.parse(emergencyContactStr);
            List<EmergencyContact> emergencyContacts = array.toJavaList(EmergencyContact.class);
            if (emergencyContacts == null || emergencyContacts.size() == 0) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String ID = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            //批量添加
            long timeStamp = System.currentTimeMillis();
            for (EmergencyContact emergencyContact:emergencyContacts){
                emergencyContact.setId(StringUtil.get32GUID());
                emergencyContact.setUserId(ID);
                emergencyContact.setCreateDate(timeStamp);
                timeStamp++;
            }
            int i = emergencyContactService.insertCollectList(emergencyContacts);
            return new RspSuccessResult(i);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "紧急联系人",notes = "修改紧急联系人")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true)
    })
    @PostMapping("/emergency/update")
    public RspResult updateEmergencyContact(HttpServletRequest request,
                                            @RequestParam("list") String emergencyContactStr){
        try {
            JSONArray array = (JSONArray) JSONObject.parse(emergencyContactStr);
            List<EmergencyContact> emergencyContacts = array.toJavaList(EmergencyContact.class);
            if (emergencyContacts == null || emergencyContacts.size() ==0 ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //批量修改
            for (EmergencyContact emergencyContact:emergencyContacts){
                emergencyContactService.update(emergencyContact);
            }
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "紧急联系人",notes = "查询紧急联系人")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true)
    })
    @PostMapping("/emergency/query")
    public RspResult queryEmergencyContact(HttpServletRequest request){
        try {
            String id = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            List<EmergencyContact> emergencyContacts = emergencyContactService.queryEmergencyContacts(id);
            return new RspSuccessResult(emergencyContacts);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }


}
