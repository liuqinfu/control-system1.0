package com.aether.sos.wifi.dao.model;

import com.aether.sos.wifi.common.utils.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class WiFiInfo {
    private String id ;

    private String wifiName;

    private String wifiBssid;

    private String wifiPassword;

    private String singalStrength;

    private String wifiUserId;

    private int wifiStatus = 1;

    private String latitude;

    private String longitude;

    public WiFiInfo(){
        this.id = StringUtil.get32GUID();
    }

    public WiFiInfo(String id, String wifiName,String wifiBssid, String wifiPassword, String singalStrength, String wifiUserId, int wifiStatus, String latitude, String longitude) {
        this.id = id;
        this.wifiName = wifiName;
        this.wifiBssid = wifiBssid;
        this.wifiPassword = wifiPassword;
        this.singalStrength = singalStrength;
        this.wifiUserId = wifiUserId;
        this.wifiStatus = wifiStatus;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}