package com.aether.devicemanageservice.service;

import com.aether.deviceapi.entity.OpRecord;
import com.aether.devicemanageservice.dto.OpRecordInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OprecordService {

    /**
     * 上报用户认证操作记录
     * @param opRecordList
     * @return
     */
    int batchInsertRecord(List<OpRecord> opRecordList);

    /**
     * 查询我的设备||授权给我的设备的解锁操作情况
     * @param userId
     * @param deviceType
     * @return
     */
    List<OpRecordInfoDTO> selectOphistoryByUserId(String userId, int deviceType);
}
