package com.aether.devicemanageservice.service.impl;

import com.aether.deviceapi.entity.OpRecord;
import com.aether.devicemanageservice.dao.mapper.OpRecordMapper;
import com.aether.devicemanageservice.dto.OpRecordInfoDTO;
import com.aether.devicemanageservice.service.OprecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OprecordServiceImpl implements OprecordService {

    @Autowired
    private OpRecordMapper opRecordMapper;

    @Override
    public int batchInsertRecord(List<OpRecord> opRecordList) {
        return opRecordMapper.batchInsertRecord(opRecordList);
    }

    @Override
    public List<OpRecordInfoDTO> selectOphistoryByUserId(String userId, int deviceType) {
        return opRecordMapper.selectOphistoryByUserId(userId,deviceType);
    }

}
