package com.aether.mqttservice.mqttbusiness.threads.mqtt;

import com.aether.common.code.RspResult;
import com.aether.common.finals.TradeCodeFinals;
import com.aether.common.utils.AESUtils;
import com.aether.common.utils.BytesUtils;
import com.aether.common.utils.DateUtils;
import com.aether.common.utils.SM2SM4.SM2Utils;
import com.aether.common.utils.SM2SM4.SM4Utils;
import com.aether.common.utils.SM2SM4.Util;
import com.aether.common.utils.StringUtil;
import com.aether.common.utils.blowfish.BlowfishUtils;
import com.aether.common.utils.blowfish.RSAEncrypt;
import com.aether.deviceapi.entity.ComputerStatus;
import com.aether.deviceapi.entity.DevicePower;
import com.aether.deviceapi.entity.DeviceSiren;
import com.aether.deviceapi.entity.OpRecord;
import com.aether.mqttservice.configration.SM4AndSEAkeys;
import com.aether.mqttservice.mqttbusiness.threads.queue.MqttSenderQueue;
import com.aether.mqttservice.service.CustomerService;
import com.aether.mqttservice.service.DeviceService;
import com.aether.mqttservice.util.PubStatic;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author liuqinfu
 */
@Slf4j
public class RecBussiness implements Runnable {

    private String serverSubTopic;
    private String deviceSubTopic;

    private CustomerService customerService;

    private DeviceService deviceService;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 消息来源topic
     */
    private String fromTopic;

    private MqttMessage mqttMessage;

    public RecBussiness(String serverSubTopic,
                        String deviceSubTopic,
                        String fromTopic,
                        MqttMessage mqttMessage,
                        CustomerService customerService,
                        DeviceService deviceService) {
        this.serverSubTopic = serverSubTopic;
        this.deviceSubTopic = deviceSubTopic;
        this.fromTopic = fromTopic;
        this.mqttMessage = mqttMessage;
        this.customerService =customerService;
        this.deviceService = deviceService;
    }

    @Override
    public void run() {
        try {
            String[] topicPartterns = fromTopic.split("/");
            log.info(JSONObject.toJSONString(topicPartterns));
            byte[] payload = mqttMessage.getPayload();
            //解析数据
            Map<String, String> data = this.decode(payload);
            //业务分类
            String tradeCode = data.get("tradeCode");
            if (StringUtils.isNotEmpty(tradeCode)) {
                //获取公共请求参数
                //路由器请求流水号
                String sgcmReqSerialNum = data.get("sgcmReqSerialNum");
                //服务器请求流水号
                String serverReqSerialNum = data.get("serverReqSerialNum");
                if (StringUtils.isNoneEmpty(sgcmReqSerialNum)) {
                    //路由器请求路由ID
                    String sgcmReqSgcmId = data.get("sgcmReqSgcmId");
                    //响应参数包装
                    Map<String, String> resp = new HashMap<>();
                    String dateStr = simpleDateFormat.format(new Date());
                    // 0 代表前面补充0
                    // 4 代表长度为4
                    // d 代表参数为正数型
                    String serialNumStr = String.format("%010d", PubStatic.SERVERSERIALNUM.incrementAndGet());
                    String serverRspSerialNum = dateStr + serialNumStr;
                    resp.put("tradeCode", tradeCode);
                    //路由器请求流水号
                    resp.put("sgcmReqSerialNum", sgcmReqSerialNum);
                    //服务器响应流水号
                    resp.put("serverRspSerialNum", serverRspSerialNum);
                    //服务器所有响应返回码
                    resp.put("allRspReturnCode", "0000");
                    resp.put("serverRspTime",getTimeStr());

                    try {
                        switch (tradeCode) {
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_CHALLANGE_CODE:
                                //挑战应答
                                log.info("挑战应答");
                                String sgcmReqUserId = data.get("sgcmReqUserId");//用户id
                                String sgcmReqUserPwdAbs = data.get("sgcmReqUserPwdAbs");//密码摘要
                                String sgcmReqUserEqpMac = data.get("sgcmReqUserEqpMac");//用户mac
                                //校验用户信息
                                RspResult userInfoByLoginNameAndPwd = customerService.getUserInfoByLoginNameAndPwd(sgcmReqUserId, sgcmReqUserPwdAbs);
                                log.info("挑战应答---获取用户信息：sgcmReqSgcmId:{}, sgcmReqUserId:{},sgcmReqUserPwdAbs:{},result:{}",sgcmReqSgcmId, sgcmReqUserId, sgcmReqUserPwdAbs,JSONObject.toJSONString(userInfoByLoginNameAndPwd));
                                if (userInfoByLoginNameAndPwd.getCode().equals("200")){
                                    Map userInfo = (Map)userInfoByLoginNameAndPwd.getData();
                                    if (userInfo == null){
                                        //没有该用户
                                        resp.put("allRspReturnCode", "1002");;
                                    }else{
                                        resp.put("serverChallengeRspCode",RecBussiness.getFixLengthString(6));
                                    }
                                }else{
                                    //平台异常
                                    resp.put("allRspReturnCode", "0500");
                                }
                                //获取挑战应答
                                resp.put("sgcmReqUserId",sgcmReqUserId);
                                //路由器请求id
                                resp.put("sgcmReqSgcmId", sgcmReqSgcmId);
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_ACTIVE_DEVICE:
                                log.info("设备激活");
                                String sgcmReqUserId2 = data.get("sgcmReqUserId");//用户id(登录用户名)
                                String sgcmReqUserPwdAbs2 = data.get("sgcmReqUserPwdAbs");//密码摘要
                                String sgcmReqWorkKey = data.get("sgcmReqWorkKey"); //客户端工作秘钥
                                String sgcmReqUserEqpMac2 = data.get("sgcmReqUserEqpMac");//用户mac
                                RspResult rspResult = deviceService.activeDevice(sgcmReqSgcmId, sgcmReqUserId2);
                                log.info("设备激活---激活设备：sgcmReqSgcmId:{}, sgcmReqUserId2:{},result:{}",sgcmReqSgcmId, sgcmReqUserId2,JSONObject.toJSONString(rspResult));
                                if (!rspResult.getCode().equals("200")){
                                    //激活失败
                                    resp.put("allRspReturnCode", "0500");
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_OFFLINE_KEY:
                                log.info("管理员离线钥匙上报");
                                String sgcmReqUserId1 = data.get("sgcmReqUserId");//客户端登录用户名
                                String adminPwd = data.get("sgcmReqChallengeRspKey");//管理员离线钥匙
                                //入库
                                RspResult rspResult1 = deviceService.updateAdminPwd(sgcmReqSgcmId, adminPwd, sgcmReqUserId1);
                                log.info("管理员离线钥匙上报---更新管理员钥匙：sgcmReqSgcmId:{}, sgcmReqUserId1:{},adminPwd:{},result:{}",sgcmReqSgcmId, sgcmReqUserId1,adminPwd,JSONObject.toJSONString(rspResult1));
                                if (!rspResult1.getCode().equals("200")){
                                    //更新管理员密码失败
                                    resp.put("allRspReturnCode", "0500");
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_REPORT_OPERATION:
                                log.info("上报用户认证记录");
                                String opRecordListStr = data.get("sgcmReqChallengeRspUsed");
                                //将字符转转集合
                                List<OpRecord> opRecordList = opRecordListStrToList(opRecordListStr,sgcmReqSgcmId);
                                RspResult rspResult2 = deviceService.reportOperations(opRecordList);
                                log.info("上报用户认证记录---上报用户操作记录：sgcmReqSgcmId:{}, sgcmReqChallengeRspUsed:{},opRecordList:{},result:{}",sgcmReqSgcmId, opRecordListStr,opRecordList,JSONObject.toJSONString(rspResult2));
                                if (!rspResult2.getCode().equals("200")){
                                    resp.put("allRspReturnCode", "0500");
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_REPORT_POWER:
                                log.info("设备电量上报");
                                String sgcmReqBatteryPower = data.get("sgcmReqBatteryPower");
                                if (StringUtils.isEmpty(sgcmReqBatteryPower)){
                                    resp.put("allRspReturnCode", "0500");
                                    log.error("消息数据不完整");
                                }else{
                                    DevicePower devicePower = new DevicePower();
                                    devicePower.setId(StringUtil.get32GUID());
                                    devicePower.setDevicesn(sgcmReqSgcmId);
                                    devicePower.setPower(Integer.valueOf(sgcmReqBatteryPower));
                                    devicePower.setReportTime(new Date());
                                    RspResult rspResult3 = deviceService.reportDevicePower(devicePower);
                                    log.info("设备电量上报---更新设备电量记录：sgcmReqSgcmId:{}, sgcmReqBatteryPower:{},devicePower:{},result:{}",sgcmReqSgcmId, sgcmReqBatteryPower,devicePower,JSONObject.toJSONString(rspResult3));
                                    if (!rspResult3.getCode().equals("200")){
                                        resp.put("allRspReturnCode", "0500");
                                    }
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_REPORT_POLICE:
                                log.info("上报报警状态");
                                /**
                                 * 01-防撬报警。
                                 * 02-密码试开门错误次数达到限制次数，锁被冻结报警。
                                 * 03-电池电压低报警。
                                 * 05-钥匙开门报警。
                                 */
                                String sgcmReqAlarmStatus = data.get("sgcmReqAlarmStatus");
                                if (StringUtils.isEmpty(sgcmReqAlarmStatus)) {
                                    resp.put("allRspReturnCode", "0500");
                                    log.error("消息数据不完整");
                                }else{
                                    DeviceSiren deviceSiren = new DeviceSiren();
                                    deviceSiren.setId(StringUtil.get32GUID());
                                    deviceSiren.setDevicesn(sgcmReqSgcmId);
                                    deviceSiren.setSirenStatus(Integer.valueOf(sgcmReqAlarmStatus));
                                    deviceSiren.setReportTime(new Date());
                                    RspResult rspResult3 = deviceService.reportDeviceSiren(deviceSiren);
                                    log.info("设备电量上报---更新设备电量记录：sgcmReqSgcmId:{}, sgcmReqAlarmStatus:{},deviceSiren:{},result:{}",sgcmReqSgcmId, sgcmReqAlarmStatus,deviceSiren,JSONObject.toJSONString(rspResult3));
                                    if (!rspResult3.getCode().equals("200")) {
                                        resp.put("allRspReturnCode", "0500");
                                    }
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_REPORT_STATUS:
                                log.info("上报计算机状态");
                                String stateUsb = data.get("stateUsb"); //usb状态 正常1，异常0
                                String stateMiniUsb = data.get("stateMiniUsb"); //miniUsb状态 正常1，异常0
                                String stateHDMI = data.get("stateHDMI"); //HDMI状态 正常1，异常0
                                String stateIo = data.get("stateIo"); //io口状态 正常1，异常0
                                String stateWindowsPower = data.get("stateWindowsPower"); //外网windows开关机状态 正常1，异常
                                String stateDeviceConnect = data.get("stateDeviceConnect");//计算机心跳 0:异常 1：正常
                                if (StringUtils.isEmpty(stateUsb) || StringUtils.isEmpty(stateMiniUsb)
                                        || StringUtils.isEmpty(stateHDMI) || StringUtils.isEmpty(stateIo)
                                        || StringUtils.isEmpty(stateWindowsPower) || StringUtils.isEmpty(stateDeviceConnect)) {
                                    //计算机上报数据异常
                                    resp.put("allRspReturnCode", "0500");
                                    log.error("消息数据不完整");
                                }else{
                                    //更新心跳
                                    RspResult rspResult4 = deviceService.updateDeviceBreath(sgcmReqSgcmId);
                                    log.info("上报计算机状态---更新设备心跳：sgcmReqSgcmId:{},result:{}",sgcmReqSgcmId,rspResult4);
                                    if (!rspResult4.getCode().equals("200")){
                                        //更新心跳异常
                                        resp.put("allRspReturnCode", "0500");
                                    }
                                    ComputerStatus computerStatus = new ComputerStatus();
                                    computerStatus.setId(StringUtil.get32GUID());
                                    computerStatus.setDevicesn(sgcmReqSgcmId);
                                    computerStatus.setStateusb(Integer.valueOf(stateUsb));
                                    computerStatus.setStateminiusb(Integer.valueOf(stateMiniUsb));
                                    computerStatus.setStatehdmi(Integer.valueOf(stateHDMI));
                                    computerStatus.setStateio(Integer.valueOf(stateIo));
                                    computerStatus.setStatewindowspower(Integer.valueOf(stateWindowsPower));
                                    computerStatus.setReportTime(new Date());
                                    RspResult rspResult3 = deviceService.reportComputerStatus(computerStatus);
                                    log.info("上报计算机状态---更新设备端口状态：sgcmReqSgcmId:{},computerStatus:{},result:{}",sgcmReqSgcmId,computerStatus,JSONObject.toJSONString(rspResult3));
                                    if (!rspResult3.getCode().equals("200")){
                                        resp.put("allRspReturnCode", "0500");
                                    }
                                }
                                break;
                            case TradeCodeFinals.SERVERSUBTOPIC_TRADECODE_BREATH_WILL:
                                log.info("设备遗言");
                                //设备遗言  标识网络连接已断开
                                RspResult rspResult3 = deviceService.updateDeviceToDead(sgcmReqSgcmId);
                                log.info("设备遗言---更新设备存活状态：sgcmReqSgcmId:{},,result:{}",sgcmReqSgcmId,rspResult3);
                                if (!rspResult3.getCode().equals("200")){
                                    log.error("设备遗言---更新设备存活状态   失败");
                                }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        resp.put("allRspReturnCode", "1002");
                    }
                    String topic = fromTopic.replace(serverSubTopic, deviceSubTopic);
                    String encryType = data.get("encryType");
                    byte[] encryTypeByte = BytesUtils.hexToBytes(encryType);
                    pushToQueue(resp, topic,encryTypeByte[0]);
                }else {
                    //处理设备响应
                    String allRspReturnCode = data.get("allRspReturnCode");
                    if (!allRspReturnCode.equals("0000")){
                        //设备处理消息失败     将消息标记为已处理，其他逻辑不处理
                        RspResult rspResult = deviceService.deviceDealMsgFail(serverReqSerialNum);
                        log.info("设备响应：设备处理消息失败：serverReqSerialNum:{},tradeCode:{},rspResult:{}",serverReqSerialNum,tradeCode,JSONObject.toJSONString(rspResult));
                    }
                    //设备处理消息成功
                    switch (tradeCode){
                        case TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_UNBIND_DEVICE:
                            log.info("设备响应--设备解绑");
                            RspResult rspResult = deviceService.deviceDealMsgSuccess(serverReqSerialNum, 1, null);
                            log.info("设备响应：设备解绑：serverReqSerialNum:{},rspResult:{}",serverReqSerialNum,JSONObject.toJSONString(rspResult));
                            break;
                        case TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_AUTH_DEVICE_TOOTHER:
                            log.info("设备响应--亲情授权");
                            String sgcmRspChallengeRspKey = data.get("sgcmRspChallengeRspKey");
                            RspResult rspResult1 = deviceService.deviceDealMsgSuccess(serverReqSerialNum, 2, sgcmRspChallengeRspKey);
                            log.info("设备响应：亲情授权：serverReqSerialNum:{},sgcmRspChallengeRspKey:{},rspResult:{}",serverReqSerialNum,sgcmRspChallengeRspKey,JSONObject.toJSONString(rspResult1));
                            break;
                        case TradeCodeFinals.DEVICESUBTOPIC_TRADECODE_UNAUTH_DEVICE_TOOTHER:
                            log.info("设备响应--删除亲情授权");
                            RspResult rspResult2 = deviceService.deviceDealMsgSuccess(serverReqSerialNum, 3, null);
                            log.info("设备响应：亲情授权：serverReqSerialNum:{},rspResult:{}",serverReqSerialNum,JSONObject.toJSONString(rspResult2));
                            break;
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("后台异常{}", e.getMessage());
        }

    }

    /**
     * 将长时间格式字符串转换为时间 yyyyMMddHHmmss
     *
     * @param strDate
     * @return
     */
    private static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * 生成消息id
     * @return
     */
    public  static  String getSerialNum(){
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String serialNumStr = String.format("%010d", PubStatic.SERVERSERIALNUM.incrementAndGet()*System.currentTimeMillis());
        String serverRspSerialNum = dateStr + serialNumStr;
        return serverRspSerialNum;
    }

    /**
     * 日期转字符串
     * @return
     */
    public static String getTimeStr(){
        return DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");
    }

    /**
     * 将用户操作记录字符串转集合
     * @param opRecordListStr
     * @return
     */
    private static List<OpRecord> opRecordListStrToList(String opRecordListStr,String deviceSn){
        List<OpRecord> opRecordList = new ArrayList<>();
        String[] itemArr = opRecordListStr.split("\\|");
        for (String item : itemArr) {
            String[] columnArr = item.split(",");
            OpRecord opRecord = new OpRecord();
            opRecord.setId(StringUtil.get32GUID());
            opRecord.setDevicesn(deviceSn);
            opRecord.setLoginName(columnArr[0]);
            opRecord.setOpTime(DateUtils.convertDateFormat(columnArr[1],"yyyy-MM-dd HH:mm:ss"));
            opRecord.setResult(Integer.valueOf(columnArr[2]));
            opRecord.setCreateTime(new Date());
            opRecordList.add(opRecord);
        }
        return opRecordList;
    }



    public static void pushToQueue(Map<String, String> resp, String topic,byte encryType) throws Exception {
        log.info("to--topic:{}  msg:{}", topic, JSONObject.toJSONString(resp));
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setPayload(encode(resp, encryType));
        Map<String, Object> msg = new HashMap<String, Object>(2);
        msg.put("topic", topic);
        msg.put("mqttMsg", mqttMessage);
        boolean offer = MqttSenderQueue.msgQueue.offer(msg);
        log.info("消息已进入发送队列：{}", offer);
    }

    private static Map<String, String> decode(byte[] msg) throws Exception {
        log.info("-----------------------解密数据-----start---------------------");
        byte[] totalLenthBytes = Arrays.copyOfRange(msg, 0, 4);
        int totalLenth = BytesUtils.getInt(totalLenthBytes);
        log.info("消息总长度：{}", totalLenth);
        byte[] dataEncryType = Arrays.copyOfRange(msg, 4, 5);//数据包体加密类型 0x41:国密  0x42非国密
        byte[] secureKeyType = Arrays.copyOfRange(msg, 5, 6); //数据包加密秘钥加密类型
        byte[] encryKeyLengthBytes = Arrays.copyOfRange(msg, 6, 8);
        int encryKeyLength = BytesUtils.getShort(encryKeyLengthBytes);
        byte[] encryKeyBytes = Arrays.copyOfRange(msg, 8, 8 + encryKeyLength);
        //解密key
        byte[] datadecryKey = {};
        if (dataEncryType[0] == ((byte) 0x42)) {
            //采用AES解密
            java.security.Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );
            log.info("key密文:"+BytesUtils.bcdToString(encryKeyBytes));
            log.info("AES私钥：:"+ SM4AndSEAkeys.AESkEY);
            datadecryKey = AESUtils.aesDecrypt(encryKeyBytes,BytesUtils.hexToBytes(SM4AndSEAkeys.AESkEY));
            /*byte[] keyIvBytes = RSAEncrypt.decryptToByteArr(BytesUtils.bcdToString(encryKeyBytes), RSAkeys.rsaPrivateKey);
            log.info("keyIvy原文："+BytesUtils.bytesToHex(keyIvBytes));
            //获取blowfish秘钥和iv
            datadecryKey = Arrays.copyOfRange(keyIvBytes, 0, 16);
            log.info("key:"+BytesUtils.bcdToString(datadecryKey));
            decryIv = Arrays.copyOfRange(keyIvBytes, 16, keyIvBytes.length);
            log.info("iv:"+BytesUtils.bcdToString(decryIv));*/
        } else if (dataEncryType[0] == ((byte) 0x41)) {
            //采用国密sm4解密
            log.info("sm4 key:{}",BytesUtils.bcdToString(encryKeyBytes));
            datadecryKey = SM4Utils.decryptByteData_ECB(encryKeyBytes,BytesUtils.hexToBytes(SM4AndSEAkeys.SMS4kEY));
        }
        //真正的数据包体
        byte[] dataEncrytBytes = Arrays.copyOfRange(msg, 8 + encryKeyLength, totalLenth);
        byte[] dataBytes = {};
        if (dataEncryType[0] == ((byte) 0x42)) {
            //AES解密
            dataBytes = AESUtils.aesDecrypt(dataEncrytBytes, datadecryKey);
           /* String decrypt = BlowfishUtils.decrypt(datadecryKey, dataEncrytBytes, decryIv);
            dataBytes = BytesUtils.getBytes(decrypt);*/
        } else if (dataEncryType[0] == ((byte) 0x41)) {
            //采用sm4解密
            dataBytes = SM4Utils.decryptByteData_ECB(dataEncrytBytes,datadecryKey);
        }
        log.info("解密后数据：{}", BytesUtils.bcdToString(dataBytes));
        //解密字段
        byte[] keySizeLenbytes = Arrays.copyOfRange(dataBytes, 0, 2);
        int keySize = BytesUtils.getShort(keySizeLenbytes);
        log.info("字段数目：{}", keySize);
        Map<String, String> params = getParams(dataBytes, keySize);
        log.info("接受参数：{}", params);
        log.info("-----------------------解密数据-----end---------------------");
        params.put("encryType",BytesUtils.bcdToString(dataEncryType));//通讯加密方式
        return params;
    }


    /**
     * 加密响应数据
     *
     * @param resp          响应数据包体
     * @param dataencryType 数据包体加密类型  sm4 || blowfish
     * @return
     * @throws Exception
     */
    private static byte[] encode(Map<String, String> resp, byte dataencryType) throws Exception {
        log.info("-----------------------加密数据-----start---------------------");
        Set<Map.Entry<String, String>> entries = resp.entrySet();
        int dataSize = entries.size();
        short i = (short) dataSize;
        byte[] dataTemp = BytesUtils.getBytes(i);
        for (Map.Entry entry : entries) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            i = (short) key.length();
            dataTemp = BytesUtils.concat(dataTemp, BytesUtils.getBytes(i));
            dataTemp = BytesUtils.concat(dataTemp, BytesUtils.getBytes(key));
            i = (short) value.length();
            dataTemp = BytesUtils.concat(dataTemp, BytesUtils.getBytes(i));
            dataTemp = BytesUtils.concat(dataTemp, BytesUtils.getBytes(value));
        }
        log.info("加密前数据：{}", BytesUtils.bcdToString(dataTemp));
        //生成加密key
        byte[] dataencryKey = null;
        if (dataencryType == ((byte) 0x42)) {
            //数据包体采用AES
            //生成AES秘钥
            dataencryKey = BytesUtils.getBytes(RandomStringUtils.randomAlphanumeric(16));
        } else if (dataencryType == ((byte) 0x41)) {
            //数据包采用国密sm4加密算法
            String secretKey = RandomStringUtils.randomAlphanumeric(16);
            log.info("sm4加密秘钥：{}", secretKey);
            dataencryKey = secretKey.getBytes();
        }
        //加密数据包体
        byte[] dataEncried = {};
        byte[] encryptKeyBytes = {};
        if (dataencryType == ((byte) 0x42)) {
            //数据包体采用AES
            dataEncried = AESUtils.aesEncrypt(dataTemp,dataencryKey);
            encryptKeyBytes = AESUtils.aesEncrypt(dataencryKey, BytesUtils.hexToBytes(SM4AndSEAkeys.AESkEY));
            /*String dataencry = BlowfishUtils.encrypt(dataencryKey, dataTemp, dataencryIv);
            dataEncried = BytesUtils.hexToBytes(dataencry);
            //加密dataencryKey和dataencryIv
            byte[] keyIvBytes = BytesUtils.concat(dataencryKey, dataencryIv);
            encryptKeyIvBytes = RSAEncrypt.encryptToByteArr(BytesUtils.bcdToString(keyIvBytes), RSAkeys.rsaPublicKey);*/
        } else if (dataencryType == ((byte) 0x41)) {
            //数据包采用国密sm4加密算法
            dataEncried = SM4Utils.encryptByteData_ECB(dataTemp,dataencryKey);
            encryptKeyBytes = SM4Utils.encryptByteData_ECB(dataencryKey,BytesUtils.hexToBytes(SM4AndSEAkeys.SMS4kEY));
            /*SM4Utils.cbcIv = dataencryIv;
            dataEncried = SM4Utils.encryptByteData_CBC(BytesUtils.hexToBytes(BytesUtils.bcdToString(dataTemp)), dataencryKey);
            log.info("sm4密文1：" + BytesUtils.bcdToString(dataEncried));
            log.info("sm4密文2：" + Util.hexToByte(BytesUtils.bcdToString(dataEncried)));
            dataEncried = Util.hexToByte(BytesUtils.bcdToString(dataEncried));
            log.info("sm4密文长度：" + dataEncried.length);
            log.info("sm4秘钥：" + BytesUtils.bcdToString(dataencryKey));
            //加密dataencryKey和dataencryIv
            String encryptKeyIv = SM2Utils.encrypt(Util.hexToByte(SM4AndSEAkeys.sm2PublicKey), BytesUtils.concat(dataencryKey, dataencryIv));
            encryptKeyIvBytes = BytesUtils.hexToBytes(encryptKeyIv);*/
        }
        byte[] result = BytesUtils.concat(encryptKeyBytes, dataEncried);
        result = BytesUtils.concat(BytesUtils.getBytes((short) encryptKeyBytes.length), result);
        byte[] secureKeyTypeBytes = new byte[]{(byte) 0x31};
        result = BytesUtils.concat(secureKeyTypeBytes, result);
        byte[] arithmeticType_byteArr = new byte[]{dataencryType};
        result = BytesUtils.concat(arithmeticType_byteArr, result);
        byte[] totalLen = BytesUtils.getBytes(result.length + 4);
        log.info("响应总长度：" + BytesUtils.getInt(totalLen));
        result = BytesUtils.concat(totalLen, result);
        log.info("-----------------------加密数据-----end---------------------");
        return result;
    }

    private static Map<String, String> getParams(byte[] dataBytes, int i) {
        //正文长度
        int dataLength = dataBytes.length;
        int start = 2, end = 2;
        Map<String, String> params = new HashMap<String, String>();
        //int j=1;
        byte[] temp = null;
        int count = 0;
        while (count < i) {
            //确定复制终点
            temp = Arrays.copyOfRange(dataBytes, start, end += 2);
            int fieldLength = BytesUtils.getShort(temp);
            start = end;
            end += fieldLength;
            temp = Arrays.copyOfRange(dataBytes, start, end);
            String field = BytesUtils.getString(temp);
            //获取value
            start = end;
            end += 2;
            temp = Arrays.copyOfRange(dataBytes, start, end);
            int valueLength = BytesUtils.getShort(temp);
            start = end;
            end += valueLength;
            temp = Arrays.copyOfRange(dataBytes, start, end);
            String value = null;
            if (field.equals("sgcmReqUserPwdAbs") || field.equals("sgcmReqUserEqpMac") || field.equals("sgcmReqChallengeRspKey")){
                value = BytesUtils.bytesToHex(temp);
//                BytesUtils.bcdToString(temp);
            }else{
                value = BytesUtils.getString(temp);
            }
            start = end;
            params.put(field, value);
            count++;
        }
        return params;
    }

    /**
     * 返回长度为【strLength】的随机数，在前面补0
     *
     * @param strLength 长度
     * @return 随机数
     */
    public static String getFixLengthString(int strLength) {

        Random rm = new Random();

        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String fixLengthString = String.valueOf(pross);

        // 返回固定的长度的随机数
        return fixLengthString.substring(2, strLength + 1);
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> resp = new HashMap<>();
        resp.put("tradeCode", "8002");
        //路由器请求流水号
        resp.put("routerReqSerialNum", "0000000000000002");
        //服务器响应流水号
        resp.put("serverRspSerialNum", "0000000000000001");
        //服务器所有响应返回码
        resp.put("allRspReturnCode", "0000");
        //路由器请求路由ID
        resp.put("routerReqRouterId", "qweeeeeeqwwww");
        //客户端请求用户ID
        resp.put("clientReqUserId", "888888");
        //服务器挑战应答码
        resp.put("serverChallengeRspCode", RecBussiness.getFixLengthString(6));
        byte[] response = encode(resp, ((byte) 0x41));
        log.info("------------------------------------------------");
        Map<String, String> decode = decode(response);
        System.out.println(decode);
    }

}
