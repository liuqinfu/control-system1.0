package com.aether.sos.wifi.service;


import com.aether.sos.wifi.dao.model.WiFi;
import com.aether.sos.wifi.dao.model.WifiUser;

import java.util.List;
import java.util.Map;

public interface WifiService {

    public List<WiFi> findWifiByGps(WifiUser wifiUser);

    public List<Map> findWifiByParamToPage(int pageNum,int pageSize);

}
