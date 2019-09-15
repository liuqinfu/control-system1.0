package com.aether.deviceapi;

import com.aether.common.code.RspResult;
import com.aether.deviceapi.entity.ComputerStatus;
import com.aether.deviceapi.entity.DevicePower;
import com.aether.deviceapi.entity.DeviceSiren;
import com.aether.deviceapi.entity.OpRecord;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DeviceApi {

    /**
     * 激活设备
     * @param deviceSn
     * @param loginName
     * @return
     */
    @PostMapping("/api/active/device/{deviceSn}/{loginName}")
    public RspResult activeDevice(@PathVariable("deviceSn")String deviceSn,
                                  @PathVariable("loginName")String loginName);

    /**
     * 更新管理员离线钥匙
     * @param deviceSn 设备sn
     * @param offlinePwd  管理员离线解锁钥匙
     * @param loginName  用户登录名
     * @return
     */
    @PostMapping("/api/pwd/update/{deviceSn}/{offlinePwd}/{loginName}")
    public RspResult updateAdminPwd(@PathVariable("deviceSn")String deviceSn,
                                    @PathVariable("offlinePwd")String offlinePwd,
                                    @PathVariable("loginName")String loginName);

    /**
     * 上报用户解锁认证记录
     * @param opRecordList
     * @return
     */
    @PostMapping("/api/report/op")
    public RspResult reportOperations(@RequestParam("records") List<OpRecord> opRecordList);

    /**
     * 上报设备电量
     * @param devicePower
     * @return
     */
    @PostMapping("/api/report/power")
    public RspResult reportDevicePower(@RequestParam("devicePower") DevicePower devicePower);

    /**
     * 上报设备报警状态
     * @param deviceSiren
     * @return
     */
    @PostMapping("/api/report/siren")
    public RspResult reportDeviceSiren(@RequestParam("deviceSiren") DeviceSiren deviceSiren);

    /**
     * 设备处理消息失败
     * @param businessId
     * @return
     */
    @PostMapping("/api/update/msg/fail/{businessId}")
    public RspResult deviceDealMsgFail(@PathVariable("businessId")String businessId);

    /**
     * 设备处理消息成功
     * @param businessId  消息id
     * @param msgType  业务类型   1：解绑  2：亲情授权  3：取消亲情授权
     * @param pwd 亲情授权时，被授权人的离线钥匙
     * @return
     */
    @PostMapping("/api/update/msg/success/{businessId}/{msgType}/{pwd}")
    public RspResult deviceDealMsgSuccess(@PathVariable("businessId")String businessId,
                                          @PathVariable("msgType")Integer msgType,
                                          @PathVariable(value = "pwd",required = false)String pwd);

    /**
     * 上报计算机各端口状态
     * @param computerStatus
     * @return
     */
    @PostMapping("/api/report/computerstatus")
    public RspResult reportComputerStatus(@RequestParam("computerStatus") ComputerStatus computerStatus);

    /**
     * 更新设备心跳
     * @param deviceSn
     * @return
     */
    @PostMapping("/api/breath/{deviceSn}")
    public RspResult updateDeviceBreath(@PathVariable("deviceSn")String deviceSn);

    /**
     * 设备已死亡，更新设备isalive状态
     * @param deviceSn
     * @return
     */
    @PostMapping("/api/dead/{deviceSn}")
    public RspResult updateDeviceToDead(@PathVariable("deviceSn")String deviceSn);
}
