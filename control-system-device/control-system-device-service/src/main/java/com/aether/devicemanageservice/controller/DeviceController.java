package com.aether.devicemanageservice.controller;

import com.aether.common.code.RspFailResult;
import com.aether.common.code.RspResult;
import com.aether.common.code.RspSuccessResult;
import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.PubFinals;
import com.aether.common.utils.JWTUtil;
import com.aether.common.utils.StringUtil;
import com.aether.customerapi.entity.User;
import com.aether.devicemanageservice.dao.model.AuthDevice;
import com.aether.devicemanageservice.dao.model.Business;
import com.aether.devicemanageservice.dao.model.Device;
import com.aether.devicemanageservice.dao.model.DeviceApp;
import com.aether.devicemanageservice.dto.AuthedUserDTO;
import com.aether.devicemanageservice.dto.BeAuthedDeviceInfoDTO;
import com.aether.devicemanageservice.dto.MineDeviceInfoDTO;
import com.aether.devicemanageservice.dto.OpRecordInfoDTO;
import com.aether.devicemanageservice.service.*;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuqinfu
 * @date 2019/9/4 22:29
 */
@RestController
@Api(value = "device", tags = "设备管理")
public class DeviceController {

    @Autowired
    private DeviceAppService deviceAppService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OprecordService oprecordService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private UserService userService;

    /**
     * 绑定设备
     * @param request
     * @param deviceSn  设备sn
     * @param deviceName  设备昵称
     * @return
     */
    @ApiOperation(value = "绑定设备", notes = "绑定设备")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "sn", value = "设备sn", required = true),
            @ApiImplicitParam(paramType = "path", name = "deviceName", value = "设备昵称", required = true)})
    @PostMapping("/bind/{sn}/{deviceName}")
    public RspResult bindDevice(HttpServletRequest request,
                                @PathVariable("sn")String deviceSn,
                                @PathVariable("deviceName")String deviceName){
        try {
            if (StringUtils.isEmpty(deviceSn) || deviceSn.length() < 19 || StringUtils.isEmpty(deviceName)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            //查询设备sn码是否有效
            DeviceApp deviceApp = deviceAppService.selectDeviceAppBySn(deviceSn);
            if (deviceApp == null){
                //sn无效
                return new RspFailResult(CodeFinals.DEVICESN_NOT_EXIST);
            }
            //查询设备是否已被绑定
            Device device = deviceService.selectBindedDeviceBySn(deviceSn);
            if (device != null) {
                //设备已被绑定
                if (!device.getUserId().equals(userId)) {
                    //已被他人绑定
                    return new RspFailResult(CodeFinals.DEVICE_HAD_BINDED_OTHER);
                }else {
                    //自己已经绑定过
                    return new RspFailResult(CodeFinals.DEVICE_HAD_BINDED_MINE);
                }
            }
            //插入到绑定表
            Device newDevice = new Device();
            newDevice.setId(StringUtil.get32GUID());
            newDevice.setUserId(userId);
            newDevice.setDeviceSn(deviceSn);
            newDevice.setDeviceName(deviceName);
            newDevice.setDeviceType(deviceApp.getDeviceType());
            newDevice.setDisable("0");
            newDevice.setIsactive(0);
            newDevice.setCreateTime(new Date());
            deviceService.insertSelective(newDevice);
            //响应
            return new RspSuccessResult(newDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 设备预解绑
     * @param request
     * @param deviceSn
     * @return
     */
    @ApiOperation(value = "设备预解绑", notes = "预解除绑定设备")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "sn", value = "设备sn", required = true)})
    @PostMapping("/preunbind/{sn}")
    public RspResult unBindDevice(HttpServletRequest request,
                                  @PathVariable("sn")String deviceSn){
        try {
            if (StringUtils.isEmpty(deviceSn)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            //查询是否有效绑定
            Device device = deviceService.selectBindedDeviceBySn(deviceSn);
            if (device != null){
                //是否是本人的设备
                if (!device.getUserId().equals(userId)){
                    //不是本人设备
                    return new RspFailResult(CodeFinals.DEVICE_NOT_BELONG);
                }
            }else{
                //没有该设备绑定
                return new RspFailResult(CodeFinals.DEVICE_NOT_BINDED);
            }
            Map res = new HashMap();
            res.put("flag","wait");
            //未激活的设备直接解绑
            if (device.getIsactive() == 0){
                //直接解绑设备
                int i = deviceService.unBindDevice(deviceSn, userId);
                res.put("flag","success");
                return new RspSuccessResult(res);
            }
            RspResult userInfoById = userService.getUserInfoById(userId);
            if (!userInfoById.getCode().equals("200")){
                return userInfoById;
            }
            if (userInfoById.getData() == null){
                return new RspFailResult(CodeFinals.ACCOUNT_NOT_EXIST);
            }
            Map userMap = ((Map) userInfoById.getData());
            User user = JSONObject.parseObject(JSONObject.toJSONString(userMap), User.class);
            //预解绑（此时并没有解绑成功，须等待设备处理该消息）
                //通知设备
            RspResult rspResult = mqttService.unBindDevice(deviceSn, user.getLoginName());
            if (rspResult.getCode().equals("500")){
                return rspResult;
            }
            String serialNum = ((String) rspResult.getData());
            //入库
            Business business = new Business();
            business.setId(StringUtil.get32GUID());
            business.setDeviceSn(deviceSn);
            business.setRelationId(device.getId());
            business.setRelationUserid(userId);
            business.setBusinessid(serialNum);
            business.setType(1);  //1：解绑 2：授权 3：取消授权
            business.setIsdealed(0);
            business.setCreatTime(new Date());
            businessService.insertSelective(business);
            res.put("bussinessid",serialNum);
            return new RspSuccessResult(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 预授权设备到他人
     * @param deviceSn
     * @param mobile
     * @param countryCode
     * @return
     */
    @ApiOperation(value = "预授权设备", notes = "预授权设备到其他人")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "devicesn", value = "设备sn", required = true),
            @ApiImplicitParam(paramType = "path", name = "mobile", value = "被授权人手机号", required = true),
            @ApiImplicitParam(paramType = "path", name = "countrycode", value = "被授权人手机号国家码", required = true)})
    @PostMapping("/preauth/{devicesn}/{mobile}/{countrycode}")
    public RspResult authDeviceToOthers(HttpServletRequest request,
                                        @PathVariable("devicesn")String deviceSn,
                                        @PathVariable("mobile")String mobile,
                                        @PathVariable("countrycode")String countryCode){
        try {
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(countryCode)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //验证设备是否是本人绑定的
            Device device = deviceService.selectBindedDeviceBySn(deviceSn);
            if (device == null){
                return new RspFailResult(CodeFinals.DEVICE_NOT_BINDED);
            }else {
                if (!device.getUserId().equals(userId)){
                    //不是自己的设备
                    return new RspFailResult(CodeFinals.DEVICE_NOT_BELONG);
                }
                //验证设备是否已激活
                if (device.getIsactive() == 0){
                    //未激活
                    return new RspFailResult(CodeFinals.DEVICE_UNACTIVE);
                }
                //预授权
                //查询被授权人信息
                RspResult userInfoByMobileAndCountryCode = userService.getUserInfoByMobileAndCountryCode(mobile, countryCode);
                if (!userInfoByMobileAndCountryCode.getCode().equals("200")){
                    return userInfoByMobileAndCountryCode;
                }
                Map userInfo = (Map)userInfoByMobileAndCountryCode.getData();
                User otherUser = JSONObject.parseObject(JSONObject.toJSONString(userInfo), User.class);
                if (otherUser.getID().equals(userId)){
                    //自己授权自己
                    return new RspFailResult(CodeFinals.DEVICE_CANT_AUTH_TOSELF);
                }
                //通知终端设备
                RspResult rspResult = mqttService.authDeviceToOthers(deviceSn, otherUser.getLoginName(),otherUser.getPassword());
                if (rspResult.getCode().equals("500")){
                    return rspResult;
                }
                String serialNum = ((String) rspResult.getData());
                //插入消息
                Business business = new Business();
                business.setId(StringUtil.get32GUID());
                business.setDeviceSn(deviceSn);
                business.setRelationId(device.getId());
                business.setRelationUserid(otherUser.getID());
                business.setBusinessid(serialNum);
                business.setType(2);
                business.setIsdealed(0);
                business.setCreatTime(new Date());
                businessService.insertSelective(business);
                //响应
                Map res = new HashMap();
                res.put("flag","wait");
                res.put("bussinessid",serialNum);
                return new RspSuccessResult(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }


    /**
     * 取消用户授权
     * @param request
     * @param deviceSn
     * @param mobile
     * @param countryCode
     * @return
     */
    @ApiOperation(value = "预取消授权设备", notes = "预取消授权设备到其他人")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "devicesn", value = "设备sn", required = true),
            @ApiImplicitParam(paramType = "path", name = "mobile", value = "被授权人手机号", required = true),
            @ApiImplicitParam(paramType = "path", name = "countrycode", value = "被授权人手机号国家码", required = true)})
    @PostMapping("/preunauth/{devicesn}/{mobile}/{countrycode}")
    public RspResult unAuthDeviceToOthers(HttpServletRequest request,
                                          @PathVariable("devicesn")String deviceSn,
                                          @PathVariable("mobile")String mobile,
                                          @PathVariable("countrycode")String countryCode){
        try {
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(countryCode)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //验证设备是否是本人绑定的
            Device device = deviceService.selectBindedDeviceBySn(deviceSn);
            if (device == null){
                return new RspFailResult(CodeFinals.DEVICE_NOT_BINDED);
            }else {
                if (!device.getUserId().equals(userId)){
                    //不是自己的设备
                    return new RspFailResult(CodeFinals.DEVICE_NOT_BELONG);
                }
                //预取消授权
                //查询被授权人信息
                RspResult userInfoByMobileAndCountryCode = userService.getUserInfoByMobileAndCountryCode(mobile, countryCode);
                if (!userInfoByMobileAndCountryCode.getCode().equals("200")){
                    return userInfoByMobileAndCountryCode;
                }
                Map userInfo = (Map)userInfoByMobileAndCountryCode.getData();
                if (userInfo == null){
                    //没有改用户
                    return new RspFailResult(CodeFinals.ACCOUNT_NOT_EXIST);
                }
                User otherUser = JSONObject.parseObject(JSONObject.toJSONString(userInfo), User.class);
                //通知终端设备
                RspResult rspResult = mqttService.unAuthDeviceToOthers(deviceSn, otherUser.getLoginName());
                if (rspResult.getCode().equals("500")){
                    return rspResult;
                }
                String serialNum = ((String) rspResult.getData());
                //等待mqtt收到消息取消用户授权关系
                //插入消息
                Business business = new Business();
                business.setId(StringUtil.get32GUID());
                business.setDeviceSn(deviceSn);
                business.setRelationId(device.getId());
                business.setRelationUserid(otherUser.getID());
                business.setBusinessid(serialNum);
                business.setType(3);
                business.setIsdealed(0);
                business.setCreatTime(new Date());
                businessService.insertSelective(business);
                //响应
                Map res = new HashMap();
                res.put("flag","wait");
                res.put("bussinessid",serialNum);
                return new RspSuccessResult(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 查询解绑|授权|取消授权结果
     * @param request
     * @param businessId
     * @return
     */
    @ApiOperation(value = "解绑|授权|取消授权操作结果", notes = "查询解绑|授权|取消授权操作结果")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "bussinessid", value = "消息id", required = true)})
    @GetMapping("/opres/{bussinessid}")
    public RspResult queryAuthResBySerialNum(HttpServletRequest request,
                                             @PathVariable("bussinessid")String businessId){
        try {
            if (StringUtils.isEmpty(businessId)){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //查询消息
            Business business = businessService.selectBusinessByBusinessId(businessId);
            if (business == null) {
                //无效消息id
                return new RspFailResult(CodeFinals.SERIALNUM_IS_VALID);
            }else {
                if (business.getIsdealed() == 1){
                    //设备处理消息完成
                    return new RspSuccessResult();
                }else {
                    //设备处理消息未完成
                    return new RspFailResult(CodeFinals.SERIALNUM_NOTDEALED_DEVICE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 查询自己的所有设备
     * @param request
     * @return
     */
    @ApiOperation(value = "我的设备", notes = "查询我的所有的（或指定类型的）所有设备")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "deviceType", value = "设备类型：1 智能锁  2 配电箱   3 SOS呼叫器   4 路由器  5摄像头  6 安全计算机", required = false)})
    @GetMapping("/mine/{deviceType}/all")
    public RspResult queryMineDeviceList(HttpServletRequest request,
                                         @PathVariable(value = "deviceType",required = false)Integer deviceType){
        try {
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            List<MineDeviceInfoDTO> mineDeviceInfoList = deviceService.selectMineDeviceInfoList(userId,deviceType);
            return new RspSuccessResult(mineDeviceInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 查询被授权的所有设备
     * @param request
     * @return
     */
    @ApiOperation(value = "被授权的设备", notes = "查询授权给我的所有设备")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "deviceType", value = "设备类型：1 智能锁  2 配电箱   3 SOS呼叫器   4 路由器  5摄像头  6 安全计算机", required = false)})
    @GetMapping("/beauth/{devicetype}/all")
    public RspResult queryBeAuthedDeviceList(HttpServletRequest request,
                                             @PathVariable(value = "devicetype",required = false)Integer deviceType){
        try {
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            List<BeAuthedDeviceInfoDTO> beAuthedDeviceInfoList = deviceService.selectBeAuthedDeviceInfoList(userId,deviceType);
            return new RspSuccessResult(beAuthedDeviceInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * 查询设备授权了哪些人
     * @param request
     * @param deviceSn  设备sn
     * @return
     */
    @ApiOperation(value = "授权出去的人员", notes = "查询被我授权的人员信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "devicesn", value = "设备sn", required = true)})
    @GetMapping("/authed/otherinfo/{devicesn}")
    public RspResult queryAuthedOtherUsersInfoList(HttpServletRequest request,
                                                   @PathVariable("devicesn")String deviceSn){
        try {
            if (StringUtils.isEmpty(deviceSn)){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            //查询所有被授权人的所有信息
            List<AuthedUserDTO> authedUserInfoList = deviceService.selectAuthedUserInfoList(deviceSn);
            return new RspSuccessResult(authedUserInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }

    }

    /**
     * 查询我的设备||授权给我的设备的解锁操作情况
     * @param request
     * @return
     */
    @ApiOperation(value = "查询设备解锁操作明细", notes = "查询我的设备||授权给我的设备的解锁操作情况")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌", required = true),
            @ApiImplicitParam(paramType = "path", name = "devicetype", value = "设备类型：1 智能锁  2 配电箱   3 SOS呼叫器   4 路由器  5摄像头  6 安全计算机", required = true)})
    @GetMapping("/history/unlock/{devicetype}")
    public RspResult queryDeviceOpRecordInfoList(HttpServletRequest request,
                                                   @PathVariable("devicetype")Integer deviceType){
        try {
            if (deviceType == null){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String userId = JWTUtil.getUserIIDD(request.getHeader(PubFinals.AUTH_STRING));
            //查询我的设备||授权给我的设备的解锁操作情况
            List<OpRecordInfoDTO> opRecordInfoDTOS = oprecordService.selectOphistoryByUserId(userId, deviceType);
            return new RspSuccessResult(opRecordInfoDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }

    }
}
