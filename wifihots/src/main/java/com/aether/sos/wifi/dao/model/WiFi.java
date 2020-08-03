package com.aether.sos.wifi.dao.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class WiFi {
    private String id;//
    private String wifiName;//
    private String wifiBssid;
    private String wifiPassword;//
    private String singalStrength;//
    private String wifiUserId;//
    private String wifiStatus;//
    private String longitude;
    private String latitude;
    private String juli;

}