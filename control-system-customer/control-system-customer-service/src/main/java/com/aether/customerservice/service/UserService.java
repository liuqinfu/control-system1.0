package com.aether.customerservice.service;



import com.aether.customerapi.entity.User;

import java.util.List;

/**
 * @author liuqinfu
 */
public interface UserService {

    /**
     * 根据用户id查询用户所有个人信息
     * @param userId  用户id
     * @return
     */
    User getUserById(String userId);

    /**
     * 根据用户登录名和密码查询用户所有个人信息
     * @param loginName  用户登录名
     * @param pwd  用户密码
     * @return
     */
    User getUserByLoginNameAndPwd(String loginName,String pwd);

    /**
     * 根据用户登录名和密码查询用户所有个人信息
     * @param loginName  用户登录名
     * @return
     */
    User getUserByLoginName(String loginName);


    /**
     * 根据手机号和国家码查询用户信息
     * @param mobile
     * @param countryCode
     * @return
     */
    User selectUserByMobileAndCountryCode(String mobile, String countryCode);

    /**
     *登陆
     * @param loginName  登陆名
     * @param pwd  密码
     * @return  用户信息
     */
    User login(String loginName, String pwd);

    /**
     * 校验imei
     * @param ID  用户id
     * @param imei  设备imei
     * @return  0：新设备，需要验证设备安全性 1：安全设备
     */
    int vertifyImei(String ID, String imei);

    /**
     * 更新用户登陆信息
     * @param user
     * @return
     */
    int uploadLoginInfo(User user);

    int uploadLoginInfoWithoutImei(User user);
    /**
     * 更新登陆token
     * @param token  新的token
     * @param ID  用户id
     * @return
     */
    int updateLogin_token(String token, String ID);

    /**
     * 查询登陆token
     * @param ID  用户id
     * @return  toekn
     */
    String getLogin_token(String ID);

    /**
     * 校验登陆用户名
     * @param loginName
     * @return  0：不存在  1：已存在
     */
    int vertifyLoginName(String loginName);

    /**
     * 校验手机号
     * @param mobile 手机号
     * @return 0：不存在  1：已存在
     */
    int vertifyMobile(String mobile);
    /**
     * 校验邮箱
     * @param email 邮箱
     * @return 0：不存在  1：已存在
     */
    int vertifyMail(String email);

    /**
     * 校验用户密码
     * @param ID  用户id
     * @param pwd  用户密码
     * @return  用户
     */
    User vertifyPwd(String ID, String pwd);

    /**
     * 注册
     * @param user  用户信息
     * @param accountType   账号类型 1：手机号  2：邮箱
     * @return
     */
    int registry(User user, int accountType) throws Exception;

    /**
     * 更新手机号
     * @param mobile 手机号
     * @param ID  用户id
     * @param countryCode  手机号国家码
     * @return
     */
    int updateMobile(String mobile, String ID, String countryCode);

    /**
     * 更新邮箱
     * @param mail  新邮箱
     * @param ID  用户id
     * @return
     */
    int updateMail(String mail, String ID);

    /**
     * 根据用户id更新用户信息
     * @param user
     * @return
     */
    int updateUserInfoByID(User user);

    /**
     * 忘记密码后设置密码
     * @param user
     * @param accountType  账号类型  1：手机号  2：邮箱
     * @return
     */
    int forgetPwd(User user, int accountType);

    /**
     * 根据手机号查询用户信息
     * @param mobile
     * @return
     */
    User queryUserByMobile(String mobile);

    /**
     * 查询所有用户信息
     * @return
     */
    List<User> queryAll();

    /**
     * 分页查询所有用户信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    Object queryByPage(int pageNum, int pageSize);


    /**
     * 更改密码
     * @param oldPwd  旧密码
     * @param newPwd  新密码
     * @param ID  用户di
     * @return
     */
    int updatePwd(String oldPwd, String newPwd, String ID);

    /**
     * 是否自动推送密码
     * @param isOpen  0：关闭  1：打开
     * @param ID  用户id
     * @return
     */
    int setAutoPushPwd(Integer isOpen, String ID);


}
