package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.WifiUserBrandMapper;
import com.aether.sos.wifi.service.WifiUserBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuqinfu
 * @date 2019-07-26 09:31
 */
@Service
public class WifiUserBrandServiceImpl implements WifiUserBrandService {
    @Autowired
    WifiUserBrandMapper wifiUserBrandMapper;
    @Override
    public int insertRecordByImei(String id, String imei, int bandId) {
        return wifiUserBrandMapper.insertRecordByImei(id, imei, bandId);
    }
}
