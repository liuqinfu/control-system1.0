package com.aether.devicemanageservice.dao.mapper;

import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.dto.AuthedUserDTO;
import com.aether.devicemanageservice.dto.BeAuthedDeviceInfoDTO;
import com.aether.devicemanageservice.dto.MineDeviceInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceMapper {
    /**
     * 绑定设备
     * @param record 设备信息
     * @return
     */
    int insert(Device record);

    /**
     * 绑定设备
     * @param record  设备信息
     * @return
     */
    int insertSelective(Device record);

    /**
     * 查询设备绑定关系
     * @param sn  设备sn
     * @return
     */
    Device selectBindDeviceBySn(String sn);

    /**
     * 解绑
     * @param sn  设备sn
     * @param userId  用户id
     * @return
     */
    int unBindDevice(@Param("sn") String sn, @Param("userId") String userId);

    /**
     * 查询对应用户的所有已绑定的设备
     * @param userId
     * @return
     */
    List<MineDeviceInfoDTO> selectMineDeviceInfoList(@Param("userId") String userId, @Param("deviceType") Integer deviceType);

    /**
     * 查询我的被授权的所有设备
     * @param userId
     * @return
     */
    List<BeAuthedDeviceInfoDTO> selectBeAuthedDeviceInfoList(@Param("userId") String userId, @Param("deviceType") Integer deviceType);

    /**
     * 查询设备授权了哪些人
     * @param sn
     * @return
     */
    List<AuthedUserDTO> selectAuthedUserInfoList(String sn);

    /**
     * 激活设备
     * @param deviceSn  设备sn
     * @param loginName  用户登录名
     * @return
     */
    int activeDevice(@Param("deviceSn")String deviceSn,@Param("loginName")String loginName);


    /**
     * 更新管理员离线钥匙
     * @param deviceSn
     * @param userId
     * @return
     */
    int updateAdminKey(@Param("deviceSn")String deviceSn,@Param("userId")String userId);

    /**
     * 设备响应后，解除设备绑定
     * @param recordId
     * @param userId
     * @return
     */
    int unBindDeviceByRecordIdAnduserId(@Param("recordId")String recordId,@Param("userId")String userId);

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