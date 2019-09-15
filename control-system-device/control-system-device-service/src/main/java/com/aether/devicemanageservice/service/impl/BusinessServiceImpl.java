package com.aether.devicemanageservice.service.impl;

import com.aether.devicemanageservice.dao.mapper.BusinessMapper;
import com.aether.devicemanageservice.dao.model.Business;
import com.aether.devicemanageservice.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private BusinessMapper businessMapper;

    @Override
    public int insertSelective(Business record) {
        return businessMapper.insertSelective(record);
    }

    @Override
    public Business selectBusinessByBusinessId(String businessId) {
        return businessMapper.selectBusinessByBusinessId(businessId);
    }

    @Override
    public int dealMsg( String businessId) {
        return businessMapper.dealMsg(businessId);
    }


}
