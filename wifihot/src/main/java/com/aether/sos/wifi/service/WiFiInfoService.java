package com.aether.sos.wifi.service;

import com.aether.sos.wifi.dao.model.WiFiInfo;

import java.util.List;


public interface WiFiInfoService {


    int  insertWiFiInfo (WiFiInfo wiFiInfo);

    WiFiInfo selectWifiInfoByBssid(String bssid);

    int  updateWiFiInfo (WiFiInfo wiFiInfo);

    int updateAndBindByBssid(WiFiInfo record);

    int updateWiFiGPSByOwnerId(String ownerId,String latitude,String longitude);

    List<WiFiInfo> selectWiFiInfoByIMEI(String imei);
}
