package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.common.utils.StringUtil;
import com.aether.sos.wifi.dao.mapper.WifiFlowLogMapper;
import com.aether.sos.wifi.dao.mapper.WifiMapper;
import com.aether.sos.wifi.dao.model.WiFiLink;
import com.aether.sos.wifi.dao.model.WifiFlowLog;
import com.aether.sos.wifi.dao.model.WifiUser;
import com.aether.sos.wifi.service.WifiFlowLogService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WifiFlowLogServiceImpl implements WifiFlowLogService {

    @Autowired
    WifiMapper wifiMapper;

    @Autowired
    WifiFlowLogMapper wifiFlowLogMapper;

    @Override
    public void countFlowLog() {
        List<Map> wifiByParam = wifiMapper.findWifiByParam(new WifiUser());

        Date time = new Date();
        time = DateUtils.addDays(time, -1);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateTime = df.format(time);
//      DateFormat.getDateInstance( DateFormat.DAY_OF_WEEK_FIELD  "yyyyMMdd").format(time);

        Date lastTimeDate = DateUtils.addDays(time, -1);
        String lastTimeDateStr = df.format(lastTimeDate);

        WifiFlowLog wifiFlowLog = new WifiFlowLog()
                .setFlowTimeStr(dateTime)
                .setFlowTime(time);
        for (Map wifiUser : wifiByParam) {
            wifiFlowLog.setWifiInfoId((String)wifiUser.get("id"));
            List<WiFiLink> wifiLinkByDate = wifiFlowLogMapper.findWifiLinkByDate(wifiFlowLog);
            wifiFlowLog.setFlowTimeStr(lastTimeDateStr);
            List<WiFiLink> wifiLinkLast = wifiFlowLogMapper.findWifiLinkByDate(wifiFlowLog);
//            List<WiFiLink> wifiLinkLast = wifiFlowLogMapper.findWifiLinkLast(wifiFlowLog);

            String lastFlow = "0";

            if (!wifiLinkLast.isEmpty()) {
                WiFiLink wiFiLink = wifiLinkLast.get(0);
                if (wiFiLink.getFlow() != null) {
                    lastFlow = wiFiLink.getFlow();
                }
            }

            wifiFlowLog.setFlow("0");

            if (!wifiLinkByDate.isEmpty()) {
                WiFiLink wiFiLink = wifiLinkByDate.get(0);
                String flow = "0";
                if (wiFiLink.getFlow() != null) {
                    flow = wiFiLink.getFlow();
                }
                wifiFlowLog.setFlow(calculate(flow, lastFlow));
            }
            wifiFlowLog.setId(StringUtil.get32GUID());

            wifiFlowLogMapper.insertSelective(wifiFlowLog);
        }

    }

    @Override
    public List<WifiFlowLog> findWifiFlowLog(WifiFlowLog wifiFlowLog) {
        return wifiFlowLogMapper.findWifiFlowLog(wifiFlowLog);
    }


    private String calculate(String flow, String lastFlow) {
        double v = Double.parseDouble(flow) - Double.parseDouble(lastFlow);
        if (v < 0) {
            v = -v;
        }
        return String.valueOf(v);
    }


}
