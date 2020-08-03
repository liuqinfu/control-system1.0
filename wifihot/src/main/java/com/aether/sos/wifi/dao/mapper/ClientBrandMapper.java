package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.ClientBrand;
import com.aether.sos.wifi.dao.model.ClientBrandSeries;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClientBrandMapper {
    int insert(ClientBrand record);

    int insertSelective(ClientBrand record);

    List<ClientBrandSeries> queryClientBrandSeries();
}