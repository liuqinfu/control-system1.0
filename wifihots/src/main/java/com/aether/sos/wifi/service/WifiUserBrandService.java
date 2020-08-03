package com.aether.sos.wifi.service;

/**
 * @author liuqinfu
 * @date 2019-07-26 09:30
 */
public interface WifiUserBrandService {

    int insertRecordByImei(String id,String imei, int bandId);
}
