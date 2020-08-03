package com.aether.sos.wifi.service;


import com.aether.sos.wifi.dao.model.WifiUser;

import java.util.List;

public interface FlowCountService {
    /**
     * 根据gps查询wifi用户提供者信息
     * @param wifiUser
     * @return
     */
    public List<WifiUser> findWifiByGps(WifiUser wifiUser);

}
