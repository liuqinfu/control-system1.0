package com.aether.devicemanageservice.service;

import com.aether.deviceapi.entity.ComputerStatus;

public interface ComputerStatusService {

    /**
     * 上报计算机端口状态
     * @param record
     * @return
     */
    int reportComputerStatus(ComputerStatus record);
}
