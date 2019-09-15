package com.aether.devicemanageservice.service;

import com.aether.devicemanageservice.dao.model.AuthDevice;
import org.apache.ibatis.annotations.Param;

/**
 * @author liuqinfu
 * @date 2019/9/5 15:22
 */
public interface AuthDeviceService {

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
     * @param businessId
     * @return
     */
    int unauthDeviceToOther(String businessId);

    /**
     * 取消设备下的所有授权
     * @param bindId
     * @return
     */
    int unauthDeviceByBindId(String bindId);


    /**
     * 设备响应后，授权设备给他人
     * @param businessId
     * @param pwd  被授权人钥匙
     * @return
     */
    int authDeviceToOther(String businessId,String pwd);


}
