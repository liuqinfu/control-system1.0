package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WiFiLink;
import com.aether.sos.wifi.dao.model.WifiFlowLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WifiFlowLogMapper {

    public List<WiFiLink> findWifiLinkByDate(WifiFlowLog wifiUser);

    public List<WiFiLink> findWifiLinkLast(WifiFlowLog wifiUser);

    public List<WifiFlowLog> findWifiFlowLog(WifiFlowLog wifiFlowLog);

    public void insertSelective(WifiFlowLog wifiFlowLog);

}