package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.WiFiUserMapper;
import com.aether.sos.wifi.dao.model.WifiUser;
import com.aether.sos.wifi.service.WiFiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WiFiUserServiceImpl implements WiFiUserService {

    @Autowired
    private WiFiUserMapper wiFiUserMapper;



    @Override
    public List<WifiUser> selectWiFiUserByIMEI(String imei) {
        return this.wiFiUserMapper.selectWiFiUserByIMEI(imei);
    }

    @Override
    public int insertWiFiUser(WifiUser wifiUser) {
        return this.wiFiUserMapper.insertSelective(wifiUser);
    }

    @Override
    public int updateWiFiUser(WifiUser wifiUser) {
        return  this.wiFiUserMapper.update(wifiUser);
    }
}
