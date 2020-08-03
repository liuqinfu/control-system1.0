package com.aether.sos.wifi.dao.model;

import lombok.Data;

@Data
public class ClientToClientSignal {
    private String id;

    private String hotBrandSeriesId;

    private String clientBrandSeriesId;

    private Integer hotNetType;

    private String n90dbm;

    private String n70dbm;

    private String n50dbm;


}