package com.aether.customerservice.service.impl;

import com.aether.customerapi.entity.EmergencyContact;
import com.aether.customerservice.dao.EmergencyContactDao;
import com.aether.customerservice.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyContactServiceImpl implements EmergencyContactService {

    @Autowired
    private EmergencyContactDao emergencyContactDao;
    @Override
    public int insertCollectList(List<EmergencyContact> contactList) {
        return emergencyContactDao.insertCollectList(contactList);
    }

    @Override
    public void update(EmergencyContact contact) {
        emergencyContactDao.update(contact);
        return;
    }

    @Override
    public List<EmergencyContact> queryEmergencyContacts(String userId) {
        return emergencyContactDao.queryEmergencyContacts(userId);
    }
}
