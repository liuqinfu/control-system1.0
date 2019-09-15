package com.aether.customerservice.dao;

import com.aether.customerapi.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author liuqinfu
 */
@Mapper
public interface UserDao {

    @Select("select ID,login_token as loginToken,login_name as loginName,password,head_img as headImg,sex," +
            "username as userName,type,country_code as countryCode,mobile,email,address,tel_phone as telPhone," +
            "Qr_code_url as QrCodeUrl,create_time createTime, update_time as updateTime,login_time as loginTime," +
            "last_login_time as lastLoginTime,count,device_token as deviceToken,imei,auto_push_pwd as autoPushPwd " +
            "   from t_control_user where ID=#{id}")
    User selectUserByID(@Param("id") String id);

    @Select("select ID,login_token as loginToken,login_name as loginName,password,head_img as headImg,sex," +
            "username as userName,type,country_code as countryCode,mobile,email,address,tel_phone as telPhone," +
            "Qr_code_url as QrCodeUrl,create_time createTime, update_time as updateTime,login_time as loginTime," +
            "last_login_time as lastLoginTime,count,device_token as deviceToken,imei,auto_push_pwd as autoPushPwd " +
            "   from t_control_user where login_name=#{loginName} and password=#{pwd}")
    User getUserByLoginNameAndPwd(@Param("loginName") String loginName, @Param("pwd") String pwd);

    @Select("select ID,login_token as loginToken,login_name as loginName,password,head_img as headImg,sex," +
            "username as userName,type,country_code as countryCode,mobile,email,address,tel_phone as telPhone," +
            "Qr_code_url as QrCodeUrl,create_time createTime, update_time as updateTime,login_time as loginTime," +
            "last_login_time as lastLoginTime,count,device_token as deviceToken,imei,auto_push_pwd as autoPushPwd " +
            "   from t_control_user where login_name=#{loginName}")
    User getUserByLoginName(@Param("loginName") String loginName);

    @Select("select ID,login_token as loginToken,login_name as loginName,password,head_img as headImg,sex," +
            "username as userName,type,country_code as countryCode,mobile,email,address,tel_phone as telPhone," +
            "Qr_code_url as QrCodeUrl,create_time createTime, update_time as updateTime,login_time as loginTime," +
            "last_login_time as lastLoginTime,count,device_token as deviceToken,imei,auto_push_pwd as autoPushPwd" +
            "   from t_control_user where mobile = #{mobile} and country_code = #{countryCode}")
    User selectUserByMobileAndCountryCode(@Param("mobile") String mobile, @Param("countryCode") String countryCode);
    /**
     * 登陆
     * @param loginName
     * @param pwd
     * @return
     */
    @Select("select ID,login_token as loginToken,login_name as loginName,password,head_img as headImg,sex," +
            "username as userName,type,country_code as countryCode,mobile,email,address,tel_phone as telPhone," +
            "Qr_code_url as QrCodeUrl,create_time createTime, update_time as updateTime,login_time as loginTime," +
            "last_login_time as lastLoginTime,count,device_token as deviceToken,imei,auto_push_pwd as autoPushPwd" +
            "   from t_control_user sc where sc.login_name=#{loginName} and sc.password=#{pwd}")
    User login(@Param("loginName") String loginName, @Param("pwd") String pwd);

//    @Update("update t_control_user set login_token=#{user.login_token} ,device_token = #{user.device_token} , last_login_time =#{user.login_time}  ,login_time=now(),count=#{user.count}+1,imei= #{user.imei} , update_time=now()  where ID= #{user.ID} ")
    @Update("update t_control_user set device_token = #{user.deviceToken} , last_login_time =#{user.loginTime}  ,login_time=now(),count=#{user.count}+1,imei= #{user.imei} , update_time=now()  where ID= #{user.ID} ")
    int uploadLoginInfo(@Param("user") User user);

    @Update("update t_control_user set device_token = #{user.deviceToken} , last_login_time =#{user.loginTime}  ,login_time=now(),count=#{user.count}+1, update_time=now()  where ID= #{user.ID} ")
    int uploadLoginInfoWithoutImei(@Param("user") User user);
    /**
     * 更新登陆token
     * @param login_token
     * @param ID
     * @return
     */
    @Update("update t_control_user set login_token=#{login_token} ,update_time=now()  where ID= #{ID} ")
    int updateLogin_token(@Param("login_token") String login_token, @Param("ID") String ID);

    /**
     * 查询登陆token
     * @param id
     * @return
     */
    @Select("select login_token from t_control_user where ID= #{id}")
    String getLogin_token(@Param("id") String id);

    /**
     * 更新登陆设备
     * @param user
     * @return
     */
    @Update("update t_control_user set device_token = #{user.deviceToken}  ,update_time=now()  where ID=#{user.ID} ")
    int updateDevice_token(@Param("user") User user);

    /**
     * 更新登陆时间及登陆次数
     * @param ID
     * @param loginTime
     * @return
     */
    @Update("update t_control_user set last_login_time =login_time ,login_time=#{loginTime},count=count+1 ,update_time=now() where ID = #{ID} ")
    int updateLoginTimeAndLastLoginTime(@Param("ID") String ID, @Param("loginTime") Date loginTime);

    /**
     * 验证设备安全性
     * @param id
     * @param imei
     * @return
     */
    @Select("select count(1) from t_control_user where ID = #{ID} and imei =#{imei} ")
    int vertifyImeiByID(@Param("ID") String id, @Param("imei") String imei);

    /**
     * 更新账号所在设备imei
     * @param id
     * @param imei
     * @return
     */
    @Update("update t_control_user set imei= #{imei} ,update_time=now() where ID = #{ID} ")
    int updateImeiByID(@Param("ID") String id, @Param("imei") String imei);

    /**
     * 检验登陆用户名是否存在
     * @param loginName
     * @return
     */
    @Select("select count(1) from t_control_user where login_name=#{loginName} ")
    int vertifyLoginName(String loginName);

    /**
     * 校验手机号是否注册
     * @param mobile
     * @return
     */
    @Select("select count(1) from t_control_user where mobile = #{mobile} ")
    int vertifyMobile(String mobile);

    /**
     * 校验邮箱是否注册
     * @param email
     * @return
     */
    @Select("select count(1) from t_control_user where email = #{email} ")
    int vertifyMail(String email);

    /**
     * 校验密码
     * @param ID
     * @param pwd
     * @return
     */
    @Select("select * from t_control_user where ID = #{ID} and  password = #{pwd} ")
    User vertifyPwd(@Param("ID") String ID, @Param("pwd") String pwd);

    /**
     * 手机号注册
     * @param user
     * @return
     */
    @Insert("insert into t_control_user(ID,login_name,password,username,type,country_code,mobile,Qr_code_url,create_time,count,imei)values (#{user.ID} ,#{user.loginName} ,#{user.password} ,#{user.userName} ,#{user.type} ,#{user.countryCode}  ,#{user.mobile} ,#{user.QrCodeUrl}, #{user.createTime} ,0,#{user.imei} )")
    int registryByMobile(@Param("user") User user);

    /**
     * 邮箱注册
     * @param user
     * @return
     */
    @Insert("insert into t_control_user(ID,login_name,password,username,type,email,Qr_code_url,create_time,count,imei)values (#{user.ID} ,#{user.loginName} ,#{user.password} ,#{user.userName} ,#{user.type} ,#{user.email} ,#{user.QrCodeUrl}, #{user.createTime} ,0,#{user.imei} )")
    int registryByMail(@Param("user") User user);

    /**
     * 更改手机号
     * @param mobile
     * @param ID
     * @param countryCode
     * @return
     */
    @Update("update t_control_user set mobile=#{mobile},country_code = #{country_code} ,update_time=now() where ID = #{ID} ")
    int updateMobile(@Param("mobile") String mobile, @Param("ID") String ID, @Param("country_code") String countryCode);

    /**
     * 更改邮箱
     * @param mail
     * @param ID
     * @return
     */
    @Update("update t_control_user set email=#{mail} ,update_time=now() where ID = #{ID} ")
    int updateMail(@Param("mail") String mail, @Param("ID") String ID);

    /**
     * 查询所有用户信息
     * @return
     */
    @Select("select * from t_control_user")
    List<User> selectAllUsers();

    /**
     * 根据用户id查询用户信息
     * @param user
     * @return
     */
    @UpdateProvider(type=UpdateInfoProvider.class,method="updateUserInfoProvider")
    int updateUserInfoByID(User user);


    /**
     * 更新用户密码
     * @param oldPwd
     * @param newPwd
     * @param ID
     * @return
     */
    @Update("update t_control_user set password=#{newPwd} ,update_time=now() where ID=#{ID}  and password=#{oldPwd} ")
    int updatePwd(@Param("oldPwd") String oldPwd, @Param("newPwd") String newPwd, @Param("ID") String ID);

    /**
     * 通过手机号重置密码
     * @param user
     * @return
     */
    @Update("update t_control_user set password=#{user.password} ,update_time=now() where mobile=#{user.mobile} ")
    int forgetPwdByMobile(@Param("user") User user);

    /**
     * 通过邮箱重置密码
     * @param user
     * @return
     */
    @Update("update t_control_user set password=#{user.password} ,update_time=now() where email=#{user.email} ")
    int forgetPwdByMail(@Param("user") User user);

    @Select("select * from t_control_user where mobile=#{mobile} ")
    User selectUserByMobile(@Param("mobile") String mobile);

    @Select("select * from t_control_user where email=#{email} ")
    User selectUserByEmail(@Param("email") String email);

    /**
     * 是否推送密码
     * @param isOpen  0：关  1：开
     * @param ID
     * @return
     */
    @Update("update t_control_user set auto_push_pwd=#{isOpen}  ,update_time=now() where ID=#{ID} ")
    int setAutoPushPwd(@Param("isOpen") Integer isOpen, @Param("ID") String ID);

    class UpdateInfoProvider{
        public String updateUserInfoProvider(final User user){
            return new SQL(){{
                UPDATE("t_control_user");
                //条件写法.
                if(!StringUtils.isEmpty(user.getLoginName())){
                    SET("login_name=#{loginName}");
                }
                if (!StringUtils.isEmpty(user.getHeadImg())){
                    SET("head_img=#{headImg}");
                }
                if (user.getSex() != null){
                    SET("sex=#{sex}");
                }
                if(!StringUtils.isEmpty(user.getUserName())){
                    SET("username=#{userName}");
                }
                if(!StringUtils.isEmpty(user.getType())){
                    SET("type=#{type}");
                }
                if(!StringUtils.isEmpty(user.getEmail())){
                    SET("email=#{email}");
                }
                if(!StringUtils.isEmpty(user.getTelPhone())){
                    SET("tel_phone=#{telPhone}");
                }
                SET("update_time=#{updateTime}");
                WHERE("ID=#{ID}");
            }}.toString();
        }
    }

}
