package com.aether.customerservice.service;



import com.aether.customerapi.entity.EmergencyContact;

import java.util.List;

public interface EmergencyContactService {
    int insertCollectList(List<EmergencyContact> contactList);
    void update(EmergencyContact contactList);
    List<EmergencyContact> queryEmergencyContacts(String userId);
}
