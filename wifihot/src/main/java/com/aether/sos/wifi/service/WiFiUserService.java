package com.aether.sos.wifi.service;



import com.aether.sos.wifi.dao.model.WifiUser;

import java.util.List;

public interface WiFiUserService {

    List<WifiUser> selectWiFiUserByIMEI(String imei);

    int  insertWiFiUser (WifiUser wifiUser);

    int  updateWiFiUser (WifiUser wifiUser);
 }
