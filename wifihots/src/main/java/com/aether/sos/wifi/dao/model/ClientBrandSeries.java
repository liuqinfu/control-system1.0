package com.aether.sos.wifi.dao.model;

import lombok.Data;

import java.util.List;

/**
 * @author liuqinfu
 * @date 2019-07-18 10:08
 */
@Data
public class ClientBrandSeries {
    private String brand;
    private List<ClientBrand> clientBrands;
}
