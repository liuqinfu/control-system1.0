package com.aether.sos.wifi.dao.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class WifiFlowLog {

    private String id;//
    private String wifiInfoId;//     Lwifi信息表ID
    private Date flowTime;//     时间
    private String flowTimeStr;
    private String flow;//    app统计到的流量

}
