package com.aether.sos.wifi.dao.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class WifiUser {
    private String id;

    private String imei;

    private int black_list;

    private String longitude;

    private String latitude;

    private Date create_time;

    private Date update_time;

    private Date login_time;

    private Date last_login_time;

    private int login_count;
}
