package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.WifiUserBrand;
import org.apache.ibatis.annotations.Param;

public interface WifiUserBrandMapper {
    int insert(WifiUserBrand record);

    int insertSelective(WifiUserBrand record);

    int insertRecordByImei(@Param("id")String id,@Param("imei")String imei,@Param("brandId")int bandId);
}