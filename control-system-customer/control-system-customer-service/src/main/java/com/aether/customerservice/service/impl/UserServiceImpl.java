package com.aether.customerservice.service.impl;

import com.aether.customerapi.entity.User;
import com.aether.customerservice.annotation.ExtPageHelper;
import com.aether.customerservice.dao.UserDao;
import com.aether.customerservice.service.MqttService;
import com.aether.customerservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liuqinfu
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MqttService mqttService;

//    @Autowired
//    private PlatformTransactionManager transactionManager;


    @Override
    public User selectUserByMobileAndCountryCode(String mobile, String countryCode) {
        return userDao.selectUserByMobileAndCountryCode(mobile,countryCode);
    }

    @Override
    public User getUserById(String userId) {
        return userDao.selectUserByID(userId);
    }

    @Override
    public User getUserByLoginNameAndPwd(String loginName, String pwd) {
        return userDao.getUserByLoginNameAndPwd(loginName,pwd);
    }

    @Override
    public User getUserByLoginName(String loginName) {
        return userDao.getUserByLoginName(loginName);
    }


    @Override
    public User login(String loginName, String pwd) {
        User login = userDao.login(loginName, pwd);
        return login;
    }

    @Override
    public int vertifyImei(String ID, String imei) {
        return userDao.vertifyImeiByID(ID, imei);
    }

    @Override
    public int uploadLoginInfo(User user) {
        userDao.uploadLoginInfo(user);
        /*//更新设备token
        userDao.updateDevice_token(user);
        //更新登陆时间
        userDao.updateLoginTimeAndLastLoginTime(user.getID(), new Date());
        //更新设备imei
        userDao.updateImeiByID(user.getID(), user.getImei());
        //更新登陆token
        userDao.updateLogin_token(user.getLogin_token(), user.getID());*/
        return 1;
    }

    @Override
    public int uploadLoginInfoWithoutImei(User user){
        return userDao.uploadLoginInfoWithoutImei(user);
    }

    @Override
    public int updateLogin_token(String token, String ID) {
        return userDao.updateLogin_token(token, ID);
    }

    @Override
    public String getLogin_token(String ID) {
        return userDao.getLogin_token(ID);
    }

    @Override
    public int vertifyLoginName(String loginName) {
        return userDao.vertifyLoginName(loginName);
    }

    @Override
    public int vertifyMobile(String mobile) {
        return userDao.vertifyMobile(mobile);
    }

    @Override
    public int vertifyMail(String email) {
        return userDao.vertifyMail(email);
    }

    @Override
    public User vertifyPwd(String ID, String pwd) {
        return userDao.vertifyPwd(ID, pwd);
    }

    @Override
    @Transactional
    public int registry(User user, int accountType) throws Exception {
        if (accountType == 1) {
            userDao.registryByMobile(user);
        } else {
            userDao.registryByMail(user);
        }
        return 1;
    }

    @Override
    public int updateMobile(String mobile, String ID, String countryCode) {
        return userDao.updateMobile(mobile, ID, countryCode);
    }

    @Override
    public int updateMail(String mail, String ID) {
        return userDao.updateMail(mail, ID);
    }

    @Override
    public int updateUserInfoByID(User user) {
        return userDao.updateUserInfoByID(user);
    }

    @Override
    public List<User> queryAll() {
        return userDao.selectAllUsers();
    }

    @Override
    @ExtPageHelper
    public Object queryByPage(int pageNum, int pageSize) {
        return userDao.selectAllUsers();
    }


    @Override
    @Transactional
    public int updatePwd(String oldPwd, String newPwd, String ID) {
        int up = userDao.updatePwd(oldPwd, newPwd, ID);
        return up;
    }

    @Override
    @Transactional
    public int forgetPwd(User user, int accountType) {
        //更新报警平台密码
        int up = accountType == 1 ? userDao.forgetPwdByMobile(user) : userDao.forgetPwdByMail(user);
        return up;
    }

    @Override
    public User queryUserByMobile(String mobile) {
        return userDao.selectUserByMobile(mobile);
    }

    @Override
    public int setAutoPushPwd(Integer isOpen, String ID) {
        return userDao.setAutoPushPwd(isOpen, ID);
    }

    /**
     * 通知该用户下的所有设备更新用户秘钥
     * @param pwd
     * @param targetSnList
     */
    private void updatePwdToDevices(String pwd,List<String> targetSnList){

    }

}
