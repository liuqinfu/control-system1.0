package com.aether.customerapi.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author liuqinfu
 */
@Data
public class User {

    /**
     * ID
     */
    private String ID;


    /**
     * 设备token
     */
    private String deviceToken;


    /**
     * 登陆生成的token
     */
    private String loginToken;

    /**
     * 登录帐号
     */
    private String loginName;

    /**
     *用户密码
     */
    private String password;

    /**
     * 用户头像地址
     */
    private String headImg;

    /**
     * 性别 0：女  1 男
     */
    private Integer sex;

    /**
     *用户姓名（当客户类型为2，此字段必填）
     */
    private String userName;

    /**
     * 客户类型（1普通个人用户  2 酒店用户）
     */
    private Integer type;

    /**
     * 手机号所属地区码：+86
     */
    private String countryCode;

    /**
     *手机号
     */
    private String mobile;

    /**
     *电子邮箱
     */
    private String email;

    /**
     *地址
     */
    private String address;

    /**
     *住址电话
     */
    private String telPhone;

    /**
     *二维码地址
     */
    private String QrCodeUrl;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *修改时间
     */
    private Date updateTime;

    /**
     *登录时间
     */
    private Date loginTime;

    /**
     *上次登录时间
     */
    private Date lastLoginTime;

    /**
     *登录次数
     */
    private Integer count;

    /**
     * 设备imei
     */
    private String imei;

    /**
     * 自动推送密码  1：开启  2 关闭
     */
    private Integer autoPushPwd;

    public User() {
        super();
    }

    public User(String ID,String login_name, String password, String username, Integer type,String countryCode, String mobile,String qr_code_url,Date create_time,String imei) {
        this.ID = ID;
        this.loginName = login_name;
        this.password = password;
        this.userName = username;
        this.type = type;
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.QrCodeUrl = qr_code_url;
        this.createTime = create_time;
        this.imei = imei;
    }
    public User(String ID,String login_name, String password, String username, Integer type, String mail,String qr_code_url,Date create_time,String imei,boolean flag) {
        this.ID = ID;
        this.loginName = login_name;
        this.password = password;
        this.userName = username;
        this.type = type;
        this.email = mail;
        this.QrCodeUrl = qr_code_url;
        this.createTime = create_time;
        this.imei = imei;
    }
}
