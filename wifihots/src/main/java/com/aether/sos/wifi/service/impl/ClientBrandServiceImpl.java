package com.aether.sos.wifi.service.impl;

import com.aether.sos.wifi.dao.mapper.ClientBrandMapper;
import com.aether.sos.wifi.dao.model.ClientBrandSeries;
import com.aether.sos.wifi.service.ClientBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuqinfu
 * @date 2019-07-18 10:15
 */
@Service
public class ClientBrandServiceImpl implements ClientBrandService {

    @Autowired
    private ClientBrandMapper clientBrandMapper;

    @Override
    public List<ClientBrandSeries> queryClientBrandSeries() {
        return clientBrandMapper.queryClientBrandSeries();
    }
}
