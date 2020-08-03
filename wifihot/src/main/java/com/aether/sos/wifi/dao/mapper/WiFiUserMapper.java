package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WifiUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WiFiUserMapper {
    int insert(WifiUser record);

    int insertSelective(WifiUser record);

    int update(WifiUser record);

    List<WifiUser> selectWiFiUserByIMEI(@Param("imei") String imei);

    int updateUserBlackStatus(@Param("userId") String userId, @Param("isBlack") int isBlack);

}
