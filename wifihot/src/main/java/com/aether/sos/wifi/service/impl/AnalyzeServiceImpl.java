package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.WiFiInfoMapper;
import com.aether.sos.wifi.dao.mapper.WiFiLinkMapper;
import com.aether.sos.wifi.dao.mapper.WiFiUserMapper;
import com.aether.sos.wifi.dao.model.WiFiInfo;
import com.aether.sos.wifi.dao.model.WiFiLink;
import com.aether.sos.wifi.service.AnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {

    @Autowired
    private WiFiInfoMapper wiFiInfoMapper;

    @Autowired
    private WiFiLinkMapper wiFiLinkMapper;

    @Autowired
    private WiFiUserMapper wiFiUserMapper;

    @Override
    public int insertUnexistWiFiInfo(List<WiFiInfo> wiFiInfoList) {
        return wiFiInfoMapper.insertUnexistWiFiInfo(wiFiInfoList);
    }

    @Override
    public List<WiFiInfo> selectWiFiInfos(List<String> wfBssidList) {
        return wiFiInfoMapper.selectWiFiInfos(wfBssidList);
    }

    @Override
    public List<WiFiInfo> selectWiFiInfos(List<String> wfBssidList, String brandId) {
        return wiFiInfoMapper.selectWiFiInfosWithBrandId(wfBssidList,brandId);
    }

    @Override
    public void reportLinkedWiFiInfo(WiFiLink wiFiLink) {
        wiFiLinkMapper.insertSelective(wiFiLink);
    }

    @Override
    public int updateLinkEndTime(String linkRecordId) {
        return wiFiLinkMapper.updateLinkEndTime(linkRecordId);
    }

    @Override
    public int updateWiFiStatus(String wifiName, int wifiStatus) {
        return wiFiInfoMapper.updateWiFiStatus(wifiName, wifiStatus);
    }

    @Override
    public int shareOtherHotPot(WiFiInfo wiFiInfo) {
        String wfBssId = wiFiInfo.getWifiBssid();
        int res = 0;
        //查询
        WiFiInfo wiFiInfo1 = wiFiInfoMapper.selectWfifiInfoByBssid(wfBssId);
        if (wiFiInfo1 == null){
            //插入
            res = wiFiInfoMapper.insertSelective(wiFiInfo);
        }else {
            //更新
            wiFiInfo.setId(wiFiInfo1.getId()).setWifiUserId(wiFiInfo1.getWifiUserId());
            res = wiFiInfoMapper.updateWiFiInfo(wiFiInfo);
        }
        return res;
    }

    @Override
    public List<Map> queryLinkedUsersByWiFiIdToPage(String wfId,int pageNum,int pageSize) {
        return wiFiLinkMapper.queryLinkedUsersByWifiIdToPage(wfId,pageNum,pageSize);
    }

    @Override
    public int updateUserBlackStatus(String userId, int isBlack) {
        return wiFiUserMapper.updateUserBlackStatus(userId, isBlack);
    }
}
