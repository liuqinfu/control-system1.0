package com.aether.customerapi;

import com.aether.common.code.RspResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liuqinfu
 * @date 2019/9/4 16:30
 */
public interface CustomerApi {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/api/getuserinfo/byid/{id}")
    public RspResult getUserInfoById(@PathVariable("id") String userId);

    /**
     * 根据用户登录名和密码查询用户信息
     * @param loginName
     * @param pwd
     * @return
     */
    @GetMapping("/api/getuserinfo/bypwd/{loginName}/{pwd}")
    public RspResult getUserInfoByLoginNameAndPwd(@PathVariable("loginName")String loginName,
                                                  @PathVariable("pwd")String pwd);


    /**
     * 获取用户信息
     * @param loginName
     * @return
     */
    @GetMapping("/api/getuserinfo/byloginName/{loginName}")
    public RspResult getUserInfoByLoginName(@PathVariable("loginName")String loginName);

    /**
     * 查询用户信息
     * @param mobile  手机号
     * @param countryCode  国家码
     * @return
     */
    @GetMapping("/api/getuserinfo/bymobile/{mobile}/{countrycode}")
    public RspResult getUserInfoByMobileAndCountryCode(@PathVariable("mobile") String mobile,
                                                       @PathVariable("countrycode") String countryCode);
}
