package com.aether.devicemanageservice.service;

import com.aether.devicemanageservice.dao.model.Business;
import org.apache.ibatis.annotations.Param;

public interface BusinessService {

    /**
     * 新增消息
     * @param record
     * @return
     */
    int insertSelective(Business record);

    /**
     * 根据消息业务id查询消息
     * @param businessId
     * @return
     */
    Business selectBusinessByBusinessId(String businessId);

    /**
     * 设备处理消息
     * @param businessId
     * @return
     */
    int dealMsg(String businessId);
}
