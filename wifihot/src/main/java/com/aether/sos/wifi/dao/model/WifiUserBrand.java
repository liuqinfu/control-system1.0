package com.aether.sos.wifi.dao.model;

public class WifiUserBrand {
    private String id;

    private String wifiUserId;

    private Integer brandId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getWifiUserId() {
        return wifiUserId;
    }

    public void setWifiUserId(String wifiUserId) {
        this.wifiUserId = wifiUserId == null ? null : wifiUserId.trim();
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}