package com.aether.sos.wifi.api.controller;

import com.aether.sos.wifi.common.code.RspFailResult;
import com.aether.sos.wifi.common.code.RspResult;
import com.aether.sos.wifi.common.code.RspSuccessResult;
import com.aether.sos.wifi.common.finals.CodeFinals;
import com.aether.sos.wifi.common.utils.StringUtil;
import com.aether.sos.wifi.dao.model.ClientBrandSeries;
import com.aether.sos.wifi.dao.model.WiFiInfo;
import com.aether.sos.wifi.service.AnalyzeService;
import com.aether.sos.wifi.service.ClientBrandService;
import com.aether.sos.wifi.service.WifiUserBrandService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuqinfu
 * @date 2019-07-18 10:18
 */
@Slf4j
@RestController
@RequestMapping("/wfhot")
@Api(value = "wfhot", tags = "手机品牌", description = "与手机品牌相关操作")
@ApiResponses({
        @ApiResponse(code = 200, message = "操作成功", response = RspResult.class),
        @ApiResponse(code = 402, message = "输入数据检查不通过", response = RspResult.class),
        @ApiResponse(code = 500, message = "后台程序异常", response = RspResult.class)
})
public class ClientBrandController {

    @Autowired
    private ClientBrandService clientBrandService;
    @Autowired
    private WifiUserBrandService wifiUserBrandService;
    @Autowired
    private AnalyzeService analyzeService;

    @ApiOperation(value = " 查询设备品牌列表", notes = " 查询平台已有的所有品牌列表信息")
    @GetMapping("/brand/get/all")
    public RspResult queryAllBrands(HttpServletRequest request){
        try {
            List<ClientBrandSeries> clientBrandSeries = clientBrandService.queryClientBrandSeries();
            return new RspSuccessResult(clientBrandSeries);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    /**
     * imei绑定上手机品牌
     * @param imei
     * @param brandId
     * @return
     */
    @PostMapping("/brand/bind/")
    public RspResult bindBrand(@RequestParam("imei")String imei,
                               @RequestParam("brandId")Integer brandId){
        if (StringUtils.isEmpty(imei) || brandId == null || brandId < 0){
            return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
        }
        try {
            wifiUserBrandService.insertRecordByImei(StringUtil.get32GUID(), imei, brandId);
            return new RspSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }

    //客户端上传列表并返回可用wifi信息
    @ApiOperation(value = "上传并过滤wifi", notes = "上传手机型号并过滤用户上传的可用WiFi")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "wflist", value = "wifi数组 eg:[{\"wifiName\":\"1\",\"wifiBssid\":\"qw:sa:12:ew:43\",\"singalStrength\":\"-50\",\"latitude\":\"12.33\",\"longitude\":\"140.52\"},{\"wifiName\":\"1\",\"wifiBssid\":\"qw:sa:12:ew:43\",\"singalStrength\":\"-50\",\"latitude\":\"12.33\",\"longitude\":\"140.52\"}]", required = true)})
    @PostMapping("/analyze2")
    public RspResult analyze2(HttpServletRequest request,
                             @RequestParam("wflist") String wifiList,
                              @RequestParam("brandId")String brandId) {
        try {
            if (org.springframework.util.StringUtils.isEmpty(wifiList) || org.springframework.util.StringUtils.isEmpty(brandId)) {
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
            List<WiFiInfo> wiFiInfos = analyzeService.selectWiFiInfos(wfBssidList,brandId);
            if (wifiList == null) wiFiInfos = new ArrayList<>();
            return new RspSuccessResult(wiFiInfos);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RspFailResult();
        }
    }
}
