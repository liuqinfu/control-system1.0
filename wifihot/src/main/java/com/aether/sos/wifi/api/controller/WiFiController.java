package com.aether.sos.wifi.api.controller;

import com.aether.sos.wifi.common.code.RspFailResult;
import com.aether.sos.wifi.common.code.RspResult;
import com.aether.sos.wifi.common.code.RspSuccessResult;
import com.aether.sos.wifi.common.finals.CodeFinals;
import com.aether.sos.wifi.common.utils.PageResult;
import com.aether.sos.wifi.common.utils.StringUtil;
import com.aether.sos.wifi.dao.model.WiFiInfo;
import com.aether.sos.wifi.dao.model.WiFiLink;
import com.aether.sos.wifi.dao.model.WifiUser;
import com.aether.sos.wifi.service.AnalyzeService;
import com.aether.sos.wifi.service.WiFiInfoService;
import com.aether.sos.wifi.service.WiFiUserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/wfhot")
@Api(value = "wfhot", tags = "热点管理", description = "可用热点过滤与连接上报")
@ApiResponses({
        @ApiResponse(code = 200, message = "操作成功", response = RspResult.class),
        @ApiResponse(code = 402, message = "输入数据检查不通过", response = RspResult.class),
        @ApiResponse(code = 500, message = "后台程序异常", response = RspResult.class),
        @ApiResponse(code = 100015, message = "服务端存储失败或wifi已被他人绑定", response = RspResult.class),
})
public class WiFiController {

    @Autowired
    private AnalyzeService analyzeService;

    @Autowired
    private WiFiUserService wiFiUserService;

    @Autowired
    private WiFiInfoService wiFiInfoService;

    /**
     * 客户端用户注册
     * 和已注册修改信息
     *
     * @param request
     * @param imei
     * @param longitude
     * @param latitude
     * @return
     */
    @ApiOperation(value = " 客户端用户注册", notes = " 客户端用户注册")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "imei", value = "IMEI", required = true),
            @ApiImplicitParam(paramType = "query", name = "longitude", value = "经度", required = true),
            @ApiImplicitParam(paramType = "query", name = "latitude", value = "纬度", required = true)})
    @PostMapping("/add/user")
    public RspResult addUser(HttpServletRequest request,
                             @RequestParam("imei") String imei,
                             @RequestParam("longitude") String longitude,
                             @RequestParam("latitude") String latitude) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(imei)
                    || org.springframework.util.StringUtils.isEmpty(longitude)
                    || org.springframework.util.StringUtils.isEmpty(latitude)
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }

            List<WifiUser> list = this.wiFiUserService.selectWiFiUserByIMEI(imei);
            WifiUser wifiUser = new WifiUser();
            if (list != null && list.size() > 0) {
                wifiUser = list.get(0)
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .setLogin_time(new Date())
                        .setUpdate_time(new Date())
                        .setLast_login_time(list.get(0).getLogin_time())
                        .setLogin_count(list.get(0).getLogin_count() + 1);
                this.wiFiUserService.updateWiFiUser(wifiUser);
            } else {
                wifiUser.setImei(imei)
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .setId(StringUtil.get32GUID())
                        .setCreate_time(new Date())
                        .setLogin_time(new Date())
                        .setLogin_count(1);
                this.wiFiUserService.insertWiFiUser(wifiUser);

            }
            List<WiFiInfo> list2 = this.wiFiInfoService.selectWiFiInfoByIMEI(imei);
            HashMap<String, Object> result = new HashMap<String, Object>() {{
                put("WiFiInfo", (list2 != null && list2.size() > 0) ? list2.get(0) : null);
            }};
            result.put("WifiUser", wifiUser);
            return new RspSuccessResult(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * @param request
     * @param imei
     * @param wifi_name
     * @param wifi_password
     * @return
     */
    @ApiOperation(value = " 客户端WIFI热点添加", notes = " 客户端热点WIFI添加")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "imei", value = "IMEI", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_name", value = "热点WIFI名", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_bssid", value = "热点WIFI的bssid", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_password", value = "热点WIFI密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_longitude", value = "热点WIFI经度", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_latitude", value = "热点WIFI纬度", required = true),
            @ApiImplicitParam(paramType = "query", name = "singal_strength", value = "热点WIFI信号强度", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifi_status", value = "热点WIFI状态 0 不可用 1可用", required = true)})
    @PostMapping("/add/wifi")
    public RspResult addUser(HttpServletRequest request,
                             @RequestParam("imei") String imei,
                             @RequestParam("wifi_name") String wifi_name,
                             @RequestParam("wifi_bssid") String wifi_bssid,
                             @RequestParam("wifi_password") String wifi_password,
                             @RequestParam("wifi_longitude") String wifi_longitude,
                             @RequestParam("wifi_latitude") String wifi_latitude,
                             @RequestParam("singal_strength") String singal_strength,
                             @RequestParam("wifi_status") String wifi_status) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(imei)
                    || org.springframework.util.StringUtils.isEmpty(wifi_name)
                    || org.springframework.util.StringUtils.isEmpty(wifi_bssid)
                    || org.springframework.util.StringUtils.isEmpty(wifi_password)
                    || org.springframework.util.StringUtils.isEmpty(wifi_longitude)
                    || org.springframework.util.StringUtils.isEmpty(wifi_latitude)
                    || org.springframework.util.StringUtils.isEmpty(singal_strength)
                    || org.springframework.util.StringUtils.isEmpty(wifi_status)
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            List<WifiUser> list2 = this.wiFiUserService.selectWiFiUserByIMEI(imei);
            if (list2 == null || list2.size() == 0){
                return new RspFailResult(CodeFinals.USER_NOT_EXIST);
            }
            List<WiFiInfo> list = this.wiFiInfoService.selectWiFiInfoByIMEI(imei);
            WiFiInfo wiFiInfo = new WiFiInfo();
            /*if (list != null  && list.size() > 0 ) {
                wiFiInfo = list.get(0).setWifiName(wifi_name)
                        .setWifiPassword(wifi_password)
                        .setSingalStrength(singal_strength)
                        .setWifiUserId(list.get(0).getWifiUserId())
                        .setWifiStatus(Integer.parseInt(wifi_status))
                        .setLatitude(list2.get(0).getLatitude())
                        .setLongitude(list2.get(0).getLongitude());
                this.wiFiInfoService.updateWiFiInfo(wiFiInfo);
            } else*/
            if(list == null  || list.size() == 0){
                //删除原来的
                WiFiInfo wiFiInfoByBssid = wiFiInfoService.selectWifiInfoByBssid(wifi_bssid);
                wiFiInfo.setId(StringUtil.get32GUID())
                        .setWifiName(wifi_name)
                        .setWifiBssid(wifi_bssid)
                        .setWifiPassword(wifi_password)
                        .setSingalStrength(singal_strength)
                        .setWifiUserId(list2.get(0).getId())
                        .setWifiStatus(Integer.parseInt(wifi_status))
                        .setLatitude(wifi_latitude)
                        .setLongitude(wifi_longitude);
                int res =0;
                if (wiFiInfoByBssid != null){
                    if ( StringUtils.isNotEmpty(wiFiInfoByBssid.getWifiUserId())){
                        return new RspFailResult(CodeFinals.ADD_WIFI_ERROR);
                    }else {
                        //更新
                        wiFiInfo.setId(wiFiInfoByBssid.getId());
                        res = wiFiInfoService.updateAndBindByBssid(wiFiInfo);
                    }
                }else {
                    res = this.wiFiInfoService.insertWiFiInfo(wiFiInfo);
                }
                if (res>0){
                    return new RspSuccessResult(wiFiInfo);
                }else {
                    return new RspFailResult(CodeFinals.ADD_WIFI_ERROR);
                }
            }
            return new RspSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    //客户端上传列表并返回可用wifi信息
    @ApiOperation(value = "上传并过滤wifi", notes = "上传并过滤用户上传的可用WiFi")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "wflist", value = "wifi数组 eg:[{\"wifiName\":\"1\",\"wifiBssid\":\"qw:sa:12:ew:43\",\"singalStrength\":\"-50\",\"latitude\":\"12.33\",\"longitude\":\"140.52\"},{\"wifiName\":\"1\",\"wifiBssid\":\"qw:sa:12:ew:43\",\"singalStrength\":\"-50\",\"latitude\":\"12.33\",\"longitude\":\"140.52\"}]", required = true)})
    @PostMapping("/analyze")
    public RspResult analyze(HttpServletRequest request,
                             @RequestParam("wflist") String wifiList) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(wifiList)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            JSONArray jsonArray = JSONObject.parseArray(wifiList);
            List<WiFiInfo> wiFiInfoList = jsonArray.toJavaList(WiFiInfo.class);
            List<String> wfBssidList = new ArrayList<>();
            for (WiFiInfo e:wiFiInfoList){
                if (StringUtils.isEmpty(e.getLatitude()) || StringUtils.isEmpty(e.getLongitude()) || StringUtils.isEmpty(e.getWifiBssid()) || StringUtils.isEmpty(e.getWifiName()) || StringUtils.isEmpty(e.getSingalStrength())) {
                    return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
                }
                if (StringUtils.isNotEmpty(e.getWifiName())) {
                    wfBssidList.add(e.getWifiBssid());
                }
            }
            //上传数据库中不存在的数据
            analyzeService.insertUnexistWiFiInfo(wiFiInfoList);
            //数据库查询可用wifi信息
            List<WiFiInfo> wiFiInfos = analyzeService.selectWiFiInfos(wfBssidList);
            if (wifiList == null) wiFiInfos = new ArrayList<>();
            return new RspSuccessResult(wiFiInfos);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 客户端上传连接的wifi信息
     *
     * @param request
     * @param userId
     * @param wifiId
     * @return
     */
    @ApiOperation(value = "上报连接wifi信息", notes = "上报用户连接的wifi信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "linkUserId", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "query", name = "wifiId", value = "连接的wifiid", required = true),
            @ApiImplicitParam(paramType = "query", name = "flow", value = "当前累计流量单位为Kb", required = true)})
    @PostMapping("/report/linkedinfo")
    public RspResult reportLinkedinfo(HttpServletRequest request,
                                      @RequestParam("linkUserId") String userId,
                                      @RequestParam("wifiId") String wifiId,
                                      @RequestParam("flow") String flow) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(userId)
                    || org.springframework.util.StringUtils.isEmpty(wifiId)
                    || org.springframework.util.StringUtils.isEmpty(flow)
                    || Double.valueOf(flow) < 0
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            String recordId = StringUtil.get32GUID();
            WiFiLink wiFiLink = WiFiLink.create(WiFiLink::new);
            wiFiLink.setId(recordId).setWifiInfoId(wifiId).setWifiLinkUserId(userId).setLinkStartTime(new Date()).setLinkStatus(1).setFlow(flow);
            analyzeService.reportLinkedWiFiInfo(wiFiLink);
            Map res = new HashMap();
            res.put("recordId", recordId);
            return new RspSuccessResult(res);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 更新wifi使用结束时间
     *
     * @param request
     * @param recordId
     * @return
     */
    @ApiOperation(value = "更新wifi结束时间", notes = "更新wifi使用结束时间")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "recordId", value = "连接记录id", required = true)})
    @PostMapping("/stop")
    public RspResult stopWifi(HttpServletRequest request,
                              @RequestParam("recordId") String recordId) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(recordId)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int i = analyzeService.updateLinkEndTime(recordId);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 更新本人热点是否可用
     *
     * @param request
     * @param wfname
     * @param status
     * @return
     */
    @ApiIgnore
    @ApiOperation(value = "更新个人热点状态", notes = "更新本人热点状态是否可用")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "wfname", value = "热点名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "status", value = "热点状态 0：不可用  1：可用", defaultValue = "0", required = true)})
    @PostMapping("/hotstatus/update")
    public RspResult updateWifiStatus(HttpServletRequest request,
                                      @RequestParam("wfname") String wfname,
                                      @RequestParam(value = "status", defaultValue = "0") int status) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(wfname)
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int i = analyzeService.updateWiFiStatus(wfname, status);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 修改黑名单
     *
     * @param request
     * @param imei
     * @param black_list
     * @return
     */
    @ApiOperation(value = "修改黑名单信息", notes = "修改黑名单信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "imei", value = "IMEI", required = true),
            @ApiImplicitParam(paramType = "query", name = "black_list", value = "黑名单标识，0不是黑名单 1是黑名单", required = true)})
    @PostMapping("/update/user/blacklist")
    public RspResult addUserBlackList(HttpServletRequest request,
                                      @RequestParam("imei") String imei,
                                      @RequestParam("black_list") String black_list
    ) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(imei)
                    || org.springframework.util.StringUtils.isEmpty(black_list)

            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            List<WifiUser> list = this.wiFiUserService.selectWiFiUserByIMEI(imei);
            if (list != null && list.size() >= 1) {
                WifiUser wifiUser = list.get(0)
                        .setUpdate_time(new Date())
                        .setBlack_list(Integer.parseInt(black_list));
                this.wiFiUserService.updateWiFiUser(wifiUser);

            }
            return new RspSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * 分享已知密码的他人热点
     *
     * @param request
     * @param wfName
     * @param wfPwd
     * @return
     */
    @ApiOperation(value = "分享他人热点", notes = "分享他人热点")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "wfName", value = "wifi名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "wfPwd", value = "wifi密码", required = true),
            @ApiImplicitParam(paramType = "query", name = "wfBssid", value = "wifiBssid", required = true),
            @ApiImplicitParam(paramType = "query", name = "singalStrength", value = "信号强度", required = true),
            @ApiImplicitParam(paramType = "query", name = "wflongitude", value = "wifi经度", required = true),
            @ApiImplicitParam(paramType = "query", name = "wflatitude", value = "wifi纬度", required = true)})
    @PostMapping("/shareothers")
    public RspResult shareOtherHotPot(HttpServletRequest request,
                                      @RequestParam("wfName") String wfName,
                                      @RequestParam("wfPwd") String wfPwd,
                                      @RequestParam("wfBssid") String wfBssid,
                                      @RequestParam("singalStrength") String singalStrength,
                                      @RequestParam("wflongitude") String wflongitude,
                                      @RequestParam("wflatitude") String wflatitude) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(wfName) || org.springframework.util.StringUtils.isEmpty(wfPwd)
                    || StringUtils.isEmpty(wfBssid) || StringUtils.isEmpty(singalStrength) || StringUtils.isEmpty(wflongitude)
                    || StringUtils.isEmpty(wflatitude)
            ) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            WiFiInfo wiFiInfo = new WiFiInfo().setId(StringUtil.get32GUID()).setWifiName(wfName).setWifiBssid(wfBssid).setSingalStrength(singalStrength).setWifiPassword(wfPwd).setWifiStatus(1).setLongitude(wflongitude).setLatitude(wflatitude);
            analyzeService.shareOtherHotPot(wiFiInfo);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "查询wifi连接用户列表", notes = "分页查询给定wifiId的wifi连接用户列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "wfId", value = "wifiID", required = true),
            @ApiImplicitParam(paramType = "path", name = "pageNum", value = "页码，从0开始", required = true),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "页大小", required = true)})
    @GetMapping("/linkusers/{wfId}/{pageNum}/{pageSize}")
    public RspResult queryLinkedUsersByWIFIId(@PathVariable String wfId, @PathVariable int pageNum, @PathVariable int pageSize) {
        try {
            if (StringUtils.isEmpty(wfId) || pageNum <= 0 || pageSize < 1) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            PageResult result = (PageResult) analyzeService.queryLinkedUsersByWiFiIdToPage(wfId, pageNum, pageSize);
            return new RspSuccessResult(result.getPageData());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    @ApiOperation(value = "设置用户黑名单状态", notes = "设置用户黑名单状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "setBlack", value = "是否设置为黑名单（true||false）", required = true),
            @ApiImplicitParam(paramType = "path", name = "userId", value = "用户id", required = true)})
    @GetMapping("/blackopt/{setBlack}/{userId}")
    public RspResult operationUserBlackStatus(@PathVariable boolean setBlack, @PathVariable String userId) {
        try {
            if (StringUtils.isEmpty(userId)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            int i = analyzeService.updateUserBlackStatus(userId, setBlack ? 1 : 0);
            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

}
