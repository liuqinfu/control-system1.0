package com.aether.sos.wifi.api.controller;

import com.aether.sos.wifi.common.code.RspFailResult;
import com.aether.sos.wifi.common.code.RspResult;
import com.aether.sos.wifi.common.code.RspSuccessResult;
import com.aether.sos.wifi.common.finals.CodeFinals;
import com.aether.sos.wifi.common.utils.PageResult;
import com.aether.sos.wifi.dao.model.WiFi;
import com.aether.sos.wifi.dao.model.WifiUser;
import com.aether.sos.wifi.service.WifiService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wfhot")
@Api(value = "wfhot", tags = "热点查询管理", description = "可用热点过滤与连接上报")
@ApiResponses({
        @ApiResponse(code = 200, message = "操作成功", response = RspResult.class),
        @ApiResponse(code = 402, message = "输入数据检查不通过", response = RspResult.class),
        @ApiResponse(code = 500, message = "后台程序异常", response = RspResult.class),
})
public class WiFiFindController {

    @Autowired
    private WifiService wifiService;


    /**
     * 提供100米范围内 wifi热点列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "提供100米范围内 wifi热点列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌"),
            @ApiImplicitParam(paramType = "query", name = "longitude", value = "经度", required = true),
            @ApiImplicitParam(paramType = "query", name = "latitude", value = "纬度", required = true)
    })
    @PostMapping("/findWifiByGps")
    @ResponseBody
    public RspResult findWifiByGps(String longitude, String latitude, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            WifiUser wifiUser = new WifiUser()
                    .setLongitude(longitude)
                    .setLatitude(latitude);
            List<WiFi> wifiByGps = wifiService.findWifiByGps(wifiUser);
            return new RspSuccessResult(wifiByGps);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }


    /**
     * wifi热点列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询 wifi热点列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌"),
            @ApiImplicitParam(paramType = "path", name = "pageNum", value = "页码（1开始）",required = true),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "页大小",required = true)
    })
    @GetMapping("/findWifiByParam/{pageNum}/{pageSize}")
    public RspResult findWifiByParam(HttpServletRequest request,
                                     @PathVariable("pageNum")Integer pageNum,
                                     @PathVariable("pageSize")Integer pageSize) {
        try {
            if (pageNum == null || pageNum <0 || pageSize == null || pageSize < 1){
                return new RspFailResult(CodeFinals.DATA_VALID_FAIL);
            }
            PageResult wifiByGps =(PageResult) wifiService.findWifiByParamToPage(pageNum,pageSize);
            return new RspSuccessResult(wifiByGps.getPageData());
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }


}
