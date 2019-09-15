package com.aether.devicemanageservice.dao.mapper;

import com.aether.devicemanageservice.dao.model.AuthDevice;
import org.apache.ibatis.annotations.Param;

public interface AuthDeviceMapper {

    /**
     * 授权
     * @param record  授权信息
     * @return
     */
    int insert(AuthDevice record);

    /**
     * 授权
     * @param record  授权信息
     * @return
     */
    int insertSelective(AuthDevice record);

    /**
     * 取消对某用户的授权
     * @param bindId
     * @param businessRecordId
     * @return
     */
    int unauthDeviceToOther(@Param("bindId")String bindId,@Param("businessRecordId")String businessRecordId);

    /**
     * 取消设备下的所有授权
     * @param bindId
     * @return
     */
    int unauthDeviceByBindId(@Param("bindId")String bindId);

}