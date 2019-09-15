package com.aether.devicemanageservice.service;

import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.dto.AuthedUserDTO;
import com.aether.devicemanageservice.dto.BeAuthedDeviceInfoDTO;
import com.aether.devicemanageservice.dto.MineDeviceInfoDTO;

import java.util.List;

/**
 * @author liuqinfu
 * @date 2019/9/5 08:44
 */
public interface DeviceService {

    /**
     * 绑定设备
     * @param record  设备
     * @return
     */
    int insertSelective(Device record);

    /**
     * 根据sn查询设备是否已被绑定
     * @param sn
     * @return
     */
    Device selectBindedDeviceBySn(String sn);

    /**
     * 解绑
     * @param sn  设备sn
     * @param userId  用户id
     * @return
     */
    int unBindDevice(String sn, String userId);

    /**
     * 查询对应用户的所有已绑定的设备
     * @param userId
     * @return
     */
    List<MineDeviceInfoDTO> selectMineDeviceInfoList(String userId, Integer deviceType);

    /**
     * 查询我的被授权的所有设备
     * @param userId
     * @param deviceType 设备类型
     * @return
     */
    List<BeAuthedDeviceInfoDTO> selectBeAuthedDeviceInfoList(String userId, Integer deviceType);

    /**
     * 查询设备授权了哪些人
     * @param sn
     * @return
     */
    List<AuthedUserDTO> selectAuthedUserInfoList(String sn);

    /**
     * 激活设备
     * @param deviceSn  设备sn
     * @param userId  用户id
     * @return
     */
    int activeDevice(String deviceSn,String userId);

    /**
     * 更新管理员离线钥匙
     * @param deviceSn
     * @param userId
     * @return
     */
    int updateAdminKey(String deviceSn,String userId);

    /**
     * 设备响应后，解除设备绑定
     * @return
     */
    int unBindDeviceByBusinessId(String businessId);

    /**
     * 更新设备心跳
     * @param deviceSn
     * @return
     */
    int breath(String deviceSn);

    /**
     * 设备已死亡，更新设备isalive状态
     * @param deviceSn
     * @return
     */
    int setDeviceToDead(String deviceSn);

}
