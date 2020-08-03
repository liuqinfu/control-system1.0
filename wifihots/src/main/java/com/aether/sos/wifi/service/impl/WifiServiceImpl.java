package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.WifiMapper;
import com.aether.sos.wifi.dao.model.WiFi;
import com.aether.sos.wifi.dao.model.WifiUser;
import com.aether.sos.wifi.service.WifiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WifiServiceImpl implements WifiService {

    @Autowired
    WifiMapper wifiMapper;

    @Override
    public List<WiFi> findWifiByGps(WifiUser wifiUser) {
        return wifiMapper.findWifiByGps(wifiUser);
    }

    @Override
    public List<Map> findWifiByParamToPage(int pageNum, int pageSize) {
        return wifiMapper.findWifiByParamToPage(pageNum,  pageSize);
    }
}
