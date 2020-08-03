package com.aether.sos.wifi.service;

import com.aether.sos.wifi.dao.model.WiFiInfo;
import com.aether.sos.wifi.dao.model.WiFiLink;

import java.util.List;
import java.util.Map;

public interface AnalyzeService {

    /**
     * 新增wifi信息
     * @param wiFiInfoList
     * @return
     */
    int insertUnexistWiFiInfo(List<WiFiInfo> wiFiInfoList);

    /**
     * 根据wifi名称查询带密码的wifi信息
     * @param wfBssidList
     * @return
     */
    List<WiFiInfo> selectWiFiInfos(List<String> wfBssidList);

    /**
     * 根据wifi名称和用户手机型号查询带密码的wifi信息
     * @param wfBssidList
     * @param brandId
     * @return
     */
    List<WiFiInfo> selectWiFiInfos(List<String> wfBssidList,String brandId);

    /**
     * 上报wifi连接信息
     * @param wiFiLink
     */
    void reportLinkedWiFiInfo(WiFiLink wiFiLink);

    /**
     * 停止使用wifi
     * @param linkRecordId
     * @return
     */
    int updateLinkEndTime(String linkRecordId);

    /**
     * 更新wifi可用状态
     * @param wifiName
     * @param wifiStatus
     * @return
     */
    int updateWiFiStatus(String wifiName,int wifiStatus);

    /**
     * 分享他人wifi
     * @param wiFiInfo
     * @return
     */
    int shareOtherHotPot(WiFiInfo wiFiInfo);

    /**
     * 分页查询wifi下的连接用户列表  按结束时间降序
     */
    List<Map> queryLinkedUsersByWiFiIdToPage(String wfId,int pageNum,int pageSize);

    /**
     * 设置用户黑名单状态
     * @param userId
     * @param isBlack
     * @return
     */
    int updateUserBlackStatus(String userId,int isBlack);
}
