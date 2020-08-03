package com.aether.sos.wifi.dao.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.function.Supplier;

@Accessors(chain = true)
@Data
public class WiFiLink {
    private String id;

    private String wifiInfoId;

    private String wifiLinkUserId;

    private Date linkStartTime;

    private Date linkEndTime;

    private Integer linkStatus;

    private String flow;

    public static WiFiLink create(Supplier<WiFiLink> supplier){
        return supplier.get();
    }

}