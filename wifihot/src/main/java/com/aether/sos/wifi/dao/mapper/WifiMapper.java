package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WiFi;
import com.aether.sos.wifi.dao.model.WifiUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WifiMapper {

    public List<WiFi> findWifiByGps(WifiUser wifiUser);

    public List<Map> findWifiByParam(WifiUser wifiUser);

    public List<Map> findWifiByParamToPage(int pageNum, int pageSize);

}