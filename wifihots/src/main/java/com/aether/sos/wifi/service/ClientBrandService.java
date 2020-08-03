package com.aether.sos.wifi.service;

import com.aether.sos.wifi.dao.model.ClientBrandSeries;

import java.util.List;

/**
 * @author liuqinfu
 * @date 2019-07-18 10:15
 */
public interface ClientBrandService {
    List<ClientBrandSeries> queryClientBrandSeries();
}
