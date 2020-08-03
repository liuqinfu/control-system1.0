package com.aether.sos.wifi.service;


import com.aether.sos.wifi.dao.model.WifiFlowLog;

import java.util.List;

public interface WifiFlowLogService {

    public void countFlowLog();

    public List<WifiFlowLog> findWifiFlowLog(WifiFlowLog wifiFlowLog);

}
