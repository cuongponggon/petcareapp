package com.project.petcareapp.impl;

import com.project.petcareapp.dto.GroupContactDTO;
import com.project.petcareapp.dto.SubcriberDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.GroupContact;
import com.project.petcareapp.model.GroupContactSubcriber;
import com.project.petcareapp.model.Subcriber;
import com.project.petcareapp.repository.GroupContactRepository;
import com.project.petcareapp.repository.SubcriberRepository;
import com.project.petcareapp.service.GroupContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupContactServiceImpl implements GroupContactService {

    @Autowired
    GroupContactRepository groupContactRepository;

    @Autowired
    SubcriberRepository subcriberRepository;


    @Override
    public boolean createGroupContact(GroupContactDTO groupContactDTO, Account account) {
        System.out.println(groupContactDTO.getName());
        GroupContact checkExistedGroupContact = groupContactRepository.findByNameAndAccount_id(groupContactDTO.getName(), account.getId());
        if (checkExistedGroupContact != null) {
            return false;
        }
        GroupContact groupContact = new GroupContact();
        groupContact.setCreatedTime(LocalDateTime.now().toString());
        groupContact.setName(groupContactDTO.getName());
        groupContact.setDescription(groupContactDTO.getDescription());
        groupContact.setAccount_id(account.getId());
        groupContactRepository.save(groupContact);
        return true;
    }

    @Override
    public List<GroupContact> getAllGroupContacts(Account account) {
        return groupContactRepository.findGroupContactByAccount_idOrderByCreatedTimeDesc(account.getId());
    }

    @Override
    public GroupContact editGroupContact(GroupContact GroupContact) {
        try {
            GroupContact checkExistedGroupContact = groupContactRepository.findGroupById(GroupContact.getId());
            if (checkExistedGroupContact != null) {
                System.out.println("TEST");
                checkExistedGroupContact.setName(GroupContact.getName());

                checkExistedGroupContact.setDescription(GroupContact.getDescription());
                checkExistedGroupContact.setUpdatedTime(LocalDateTime.now().toString());

                return groupContactRepository.save(checkExistedGroupContact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null ;
    }

    @Override
    public GroupContact getGroupContactByName(String name) {
        return null;
    }

//    @Override
//    public GroupContact getGroupContactByName(String name, Account account) {
//        return groupContactRepository.findByNameAndAccount_id(name, account.getId());
//    }

    @Override
    public GroupContact getGroupById(int id) {
        return groupContactRepository.findGroupById(id);

    }

    @Override
    public GroupContact updateGroupContact(GroupContact GroupContact) {
        return null;
    }

    @Override
    public Long countTotalGroupContacts() {
        return groupContactRepository.countTotalGroupContacts();
    }



    @Override
    public GroupContact createNewGroupContact(GroupContact GroupContact) {
        return groupContactRepository.save(GroupContact);
    }

    @Override
    public List<GroupContact> searchByName(String searchValue) {
        return groupContactRepository.searchByName(searchValue);
    }

    @Override
    public List<GroupContactSubcriber> getAllSubcriber() {
        return groupContactRepository.getAllSubcriber();
    }

    @Override
    public List<SubcriberDTO> findSubcriberByGroupContactId(int groupContactId) {
        List<Subcriber>subcribers = groupContactRepository.findSubcriberByGroupContactId(groupContactId);
        List<SubcriberDTO> dtos = new ArrayList<>();
        for(Subcriber subcriber : subcribers){
            SubcriberDTO dto = new SubcriberDTO();
            dto.setId(subcriber.getId());
            dto.setBlackList(subcriber.isBlackList());
            dto.setEmail(subcriber.getEmail());
            dto.setFirstName(subcriber.getFirstName());
            dto.setLastName(subcriber.getLastName());
            dto.setType(subcriber.getType());
            dtos.add(dto);
        }
        return dtos;

    }

    @Override
    public boolean deleteSubcriberOutGroup(int subcriberId) {
        return false;
    }

    @Override
    public boolean createGroupContactFromSegment(GroupContactDTO groupContactDTO, Account account) {
        GroupContact checkExistedGroupContact = groupContactRepository.findByNameAndAccount_id(groupContactDTO.getName(), account.getId());
        if (checkExistedGroupContact != null) {
            return false;
        }

        GroupContact groupContact = new GroupContact();
        groupContact.setCreatedTime(LocalDateTime.now().toString());
        groupContact.setName(groupContactDTO.getName());
        groupContact.setDescription(groupContactDTO.getDescription());
        groupContact.setAccount_id(account.getId());
        List<GroupContactSubcriber> groupContactSubcribers = groupContactDTO.getSubcriberGCDTOS().stream().map(g -> {
            GroupContactSubcriber groupContactSubcriber = new GroupContactSubcriber();
            groupContactSubcriber.setActive(true);
            groupContactSubcriber.setGroupContact(groupContact);
            groupContactSubcriber.setCreatedTime(LocalDateTime.now().toString());
            groupContactSubcriber.setSubcriber(subcriberRepository.findSubcriberById(g.getSubcriberId()));
            return groupContactSubcriber;
        }).collect(Collectors.toList());
        groupContact.setGroupContactSubcribers(groupContactSubcribers);
        groupContactRepository.save(groupContact);
        return true;
    }


    @Override
    public Long countTotalContactsByGroupId(int groupContactId ) {
        return groupContactRepository.countTotalContactsByGroupId(groupContactId);
    }
}
