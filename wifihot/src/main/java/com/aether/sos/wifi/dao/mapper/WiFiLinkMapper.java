package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WiFiLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WiFiLinkMapper {
    int insert(WiFiLink record);

    int insertSelective(WiFiLink record);

    int updateLinkEndTime(@Param("linkRecordId")String linkRecordId);

    List<Map> queryLinkedUsersByWifiIdToPage(@Param("wifiId") String wifiId,int pageNum,int pageSize);
}