package com.aether.customerservice.api;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.customerapi.entity.User;
import com.aether.customerservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuqinfu
 * @date 2019/9/4 21:56
 */
@RestController
@RequestMapping("/api")
public class PublicApiController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/getuserinfo/byid/{id}")
    public RspResult getUserInfoById(@PathVariable("id")String userId){
        try {
            if (StringUtils.isEmpty(userId)){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //根据用户id获取用户所有信息
            User user = userService.getUserById(userId);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }


    /**
     * 根据用户登录名和密码查询用户信息
     * @param loginName
     * @param pwd
     * @return
     */
    @GetMapping("/getuserinfo/bypwd/{loginName}/{pwd}")
    public RspResult getUserInfoByLoginNameAndPwd(@PathVariable("loginName")String loginName,
                                                  @PathVariable("pwd")String pwd){
        try {
            if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(pwd)){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //根据用户id获取用户所有信息
            User user = userService.getUserByLoginNameAndPwd(loginName,pwd);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 获取用户信息
     * @param loginName
     * @return
     */
    @GetMapping("/getuserinfo/byloginName/{loginName}")
    public RspResult getUserInfoByLoginName(@PathVariable("loginName")String loginName){
        try {
            if (StringUtils.isEmpty(loginName)){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //根据用户id获取用户所有信息
            User user = userService.getUserByLoginName(loginName);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }



    /**
     * 查询用户信息
     * @param mobile  手机号
     * @param countryCode  国家码
     * @return
     */
    @GetMapping("/getuserinfo/bymobile/{mobile}/{countrycode}")
    public RspResult getUserInfoByMobileAndCountryCode(@PathVariable("mobile")String mobile,
                                                       @PathVariable("countrycode")String countryCode){
        try {
            User user = userService.selectUserByMobileAndCountryCode(mobile, countryCode);
            return new RspSuccessResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

}
