package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.WiFiInfoMapper;
import com.aether.sos.wifi.dao.model.WiFiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WiFiInfoServiceImpl implements com.aether.sos.wifi.service.WiFiInfoService {

    @Autowired
    private WiFiInfoMapper wiFiInfoMapper;

    @Override
    public int insertWiFiInfo(WiFiInfo wiFiInfo) {
        return wiFiInfoMapper.insertSelective(wiFiInfo);
    }

    @Override
    public WiFiInfo selectWifiInfoByBssid(String bssid) {
        return wiFiInfoMapper.selectWfifiInfoByBssid(bssid);
    }

    @Override
    public int updateWiFiInfo(WiFiInfo wiFiInfo) {
        return wiFiInfoMapper.update(wiFiInfo);
    }

    @Override
    public int updateAndBindByBssid(WiFiInfo record) {
        return wiFiInfoMapper.updateAndBindByBssid(record);
    }

    @Override
    public int updateWiFiGPSByOwnerId(String ownerId, String latitude, String longitude) {
        return wiFiInfoMapper.updateWiFiGPSByOwnerId(ownerId, latitude, longitude);
    }

    @Override
    public List<WiFiInfo> selectWiFiInfoByIMEI(String imei) {
        return wiFiInfoMapper.selectWiFiInfoByIMEI(imei);
    }
}
