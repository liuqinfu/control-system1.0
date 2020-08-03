package com.aether.sos.wifi.api.controller;

import com.aether.sos.wifi.common.code.RspFailResult;
import com.aether.sos.wifi.common.code.RspResult;
import com.aether.sos.wifi.common.code.RspSuccessResult;
import com.aether.sos.wifi.dao.model.WifiFlowLog;
import com.aether.sos.wifi.service.WifiFlowLogService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wfhot")
@Api(value = "wfhot", tags = "热点统计管理")
@ApiResponses({
        @ApiResponse(code = 200, message = "操作成功", response = RspResult.class),
        @ApiResponse(code = 402, message = "输入数据检查不通过", response = RspResult.class),
        @ApiResponse(code = 500, message = "后台程序异常", response = RspResult.class),
})
public class WiFiFlowLogController {

    @Autowired
    private WifiFlowLogService wifiFlowLogService;


    @ApiOperation(value = "热点统计管理")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌")
    })
    @PostMapping("/countFlowLog")
    @ResponseBody
    public RspResult countFlowLog(HttpServletRequest request) {
        try {
            wifiFlowLogService.countFlowLog();

            return new RspSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "日报表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "AUTH_CODE", value = "令牌")
    })
    @PostMapping("/getFlowLog")
    @ResponseBody
    public RspResult getFlowLog(HttpServletRequest request) {
        try {
            WifiFlowLog wifiFlowLog = new WifiFlowLog();
            List<WifiFlowLog> wifiFlowLogList = wifiFlowLogService.findWifiFlowLog(wifiFlowLog);

            return new RspSuccessResult(wifiFlowLogList);
        } catch (Exception e) {
            e.printStackTrace();
            return new RspFailResult();
        }
    }

}
