package com.aether.devicemanageservice.api;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.customerapi.entity.User;
import com.aether.deviceapi.entity.ComputerStatus;
import com.aether.deviceapi.entity.DevicePower;
import com.aether.deviceapi.entity.DeviceSiren;
import com.aether.deviceapi.entity.OpRecord;
import com.aether.devicemanageservice.service.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceApiController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private OprecordService oprecordService;

    @Autowired
    private DevicePowerService devicePowerService;

    @Autowired
    private DeviceSirenService deviceSirenService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private AuthDeviceService authDeviceService;

    @Autowired
    private ComputerStatusService computerStatusService;

    /**
     * 激活设备
     * @param deviceSn
     * @param loginName
     * @return
     */
    @PostMapping("/active/device/{deviceSn}/{loginName}/{pwd}")
    public RspResult activeDevice(@PathVariable("deviceSn")String deviceSn,
                                  @PathVariable("loginName")String loginName,
                                  @PathVariable("pwd")String pwd){
        try {
            if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(loginName)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //获取用户信息
            RspResult rspResult = userService.getUserInfoByLoginNameAndPwd(loginName, pwd);
            if (rspResult.getCode().equals("200")){
                if (rspResult.getData() != null){
                    Map userMap = (Map) rspResult.getData();
                    User user = JSONObject.parseObject(JSONObject.toJSONString(userMap), User.class);
                    String userId = user.getID();
                    //激活
                    int activeDevice = deviceService.activeDevice(deviceSn, userId);
                    if (activeDevice >0){
                        //激活成功
                        return new RspSuccessResult();
                    }else{
                        //激活失败
                        return new RspFailResult(CodeFinals.DEVICE_ACTIVE_FAIL);
                    }
                }else{
                    //用户名或密码错误
                    return new RspFailResult(CodeFinals.USER_NAME_PWD_FAIL);
                }
            }else{
                //402  参数问题   500 用户服务异常
                //内部服务参数异常
                //内部服务异常
                return rspResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 更新管理员离线钥匙
     * @param deviceSn 设备sn
     * @param offlinePwd  管理员离线解锁钥匙
     * @param loginName  用户登录名
     * @return
     */
    @PostMapping("/pwd/update/{deviceSn}/{offlinePwd}/{loginName}")
    public RspResult updateAdminPwd(@PathVariable("deviceSn")String deviceSn,
                                    @PathVariable("offlinePwd")String offlinePwd,
                                    @PathVariable("loginName")String loginName){
        if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(offlinePwd) || StringUtils.isEmpty(loginName)) {
            return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
        }
        //获取用户信息
        RspResult rspResult = userService.getUserInfoByLoginName(loginName);
        if (rspResult.getCode().equals("200")){
            if (rspResult.getData() != null){
                Map userMap = (Map) rspResult.getData();
                User user = JSONObject.parseObject(JSONObject.toJSONString(userMap), User.class);
                String userId = user.getID();
                //更新钥匙
                int updateAdminKey = deviceService.updateAdminKey(deviceSn, userId);
                if (updateAdminKey > 0) {
                    //更新成功
                    return new RspSuccessResult();
                }else {
                    //更新失败(不是异常)
                    return new RspFailResult(CodeFinals.DEVICE_ADMINPWD_UPDATE_FAIL);
                }
            }else{
                //用户名或密码错误
                return new RspFailResult(CodeFinals.USER_NAME_PWD_FAIL);
            }
        }else{
            //402  参数问题   500 用户服务异常
            //内部服务参数异常
            //内部服务异常
            return rspResult;
        }
    }

    /**
     * 上报用户解锁认证记录
     * @param opRecordList
     * @return
     */
    @PostMapping("/report/op")
    public RspResult reportOperations(@RequestParam("records") List<OpRecord> opRecordList){
        try {
            if (opRecordList == null || opRecordList.size() == 0 ){
                new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //插入数据
            int batchInsertRecordRes = oprecordService.batchInsertRecord(opRecordList);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 上报设备电量
     * @param devicePower
     * @return
     */
    @PostMapping("/report/power")
    public RspResult reportDevicePower(@RequestParam("devicePower")DevicePower devicePower){
        try {
            if (devicePower == null) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int reportDevicePower = devicePowerService.reportDevicePower(devicePower);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 上报设备报警状态
     * @param deviceSiren
     * @return
     */
    @PostMapping("/report/siren")
    public RspResult reportDeviceSiren(@RequestParam("deviceSiren")DeviceSiren deviceSiren){
        try {
            if (deviceSiren == null) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int i = deviceSirenService.updateDeviceSirenStatus(deviceSiren);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 设备处理业务失败
     * @param businessId
     * @return
     */
    @PostMapping("/update/msg/fail/{businessId}")
    public RspResult deviceDealMsgFail(@PathVariable("businessId")String businessId){
        try {
            if ( StringUtils.isEmpty(businessId)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int i = businessService.dealMsg(businessId);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 设备处理消息成功
     * @param businessId  消息id
     * @param msgType  业务类型   1：解绑  2：亲情授权  3：取消亲情授权
     * @param pwd 亲情授权时，被授权人的离线钥匙
     * @return
     */
    @PostMapping("/update/msg/success/{businessId}/{msgType}/{pwd}")
    public RspResult deviceDealMsgSuccess(@PathVariable("businessId")String businessId,
                                          @PathVariable("msgType")Integer msgType,
                                          @PathVariable(value = "pwd",required = false)String pwd){
        try {
            if (StringUtils.isEmpty(businessId) || msgType == null || (msgType !=1 && msgType !=2 && msgType !=3) || (msgType == 2 && StringUtils.isEmpty(pwd))) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            switch (msgType){
                case 1:
                    //解绑设备
                    int i = deviceService.unBindDeviceByBusinessId(businessId);
                    break;
                case 2:
                    //亲情授权
                    int i1 = authDeviceService.authDeviceToOther(businessId, pwd);
                    break;
                case 3:
                    //取消亲情授权
                    int i2 = authDeviceService.unauthDeviceToOther(businessId);
                    break;
                default:
                    break;
            }
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 上报计算机各端口状态
     * @param computerStatus
     * @return
     */
    @PostMapping("/report/computerstatus")
    public RspResult reportComputerStatus(@RequestParam("computerStatus")ComputerStatus computerStatus){
        try {
            int i = computerStatusService.reportComputerStatus(computerStatus);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 更新设备心跳
     * @param deviceSn
     * @return
     */
    @PostMapping("/breath/{deviceSn}")
    public RspResult updateDeviceBreath(@PathVariable("deviceSn")String deviceSn){
        try {
            if (StringUtils.isEmpty(deviceSn)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int breath = deviceService.breath(deviceSn);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 设备已死亡，更新设备isalive状态
     * @param deviceSn
     * @return
     */
    @PostMapping("/dead/{deviceSn}")
    public RspResult updateDeviceToDead(@PathVariable("deviceSn")String deviceSn){
        try {
            if (StringUtils.isEmpty(deviceSn)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int dead = deviceService.setDeviceToDead(deviceSn);
            //推送设备死亡状态到html

            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

}
