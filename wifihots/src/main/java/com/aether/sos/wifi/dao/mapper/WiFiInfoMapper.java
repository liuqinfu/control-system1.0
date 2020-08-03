package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WiFiInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WiFiInfoMapper {
    int insert(WiFiInfo record);

    int insertSelective(WiFiInfo record);

    WiFiInfo selectWifiInfoByBssid(@Param("bssid") String bssid);

    int update(WiFiInfo record);

    int updateAndBindByBssid(WiFiInfo record);

    int insertUnexistWiFiInfo(@Param("wifiList")List<WiFiInfo> wiFiInfoList);

    WiFiInfo selectWfifiInfoByBssid(@Param("wifiBssid")String wifiBssid);

    List<WiFiInfo> selectWiFiInfos(@Param("wifiBssids")List<String> wifiBssids);

    //根据当前手机型号对可连接的wifi列表根据wifi通讯距离数据库进行智能筛选推荐
    List<WiFiInfo> selectWiFiInfosWithBrandId(@Param("wifiBssids")List<String> wifiBssids,@Param("brandId")String brandId);

    List<WiFiInfo>  selectWiFiInfoByIMEI(String imei);

    int updateWiFiStatus(@Param("wifiName")String wifiName,@Param("wifiStatus")int wifiStatus);

    int updateWiFiInfo(@Param("wfInfo")WiFiInfo wiFiInfo);

    int updateWiFiGPSByOwnerId(@Param("ownerId") String ownerId,@Param("latitude") String latitude,@Param("longitude") String longitude);
}