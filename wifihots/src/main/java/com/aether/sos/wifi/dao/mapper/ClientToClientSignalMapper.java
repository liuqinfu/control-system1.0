package com.aether.sos.wifi.dao.mapper;

import com.aether.sos.wifi.dao.model.ClientToClientSignal;

public interface ClientToClientSignalMapper {
    int insert(ClientToClientSignal record);

    int insertSelective(ClientToClientSignal record);
}