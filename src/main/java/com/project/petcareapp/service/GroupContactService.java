package com.project.petcareapp.service;

import com.project.petcareapp.dto.GroupContactDTO;
import com.project.petcareapp.dto.SubcriberDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.GroupContact;
import com.project.petcareapp.model.GroupContactSubcriber;

import java.util.List;

public interface GroupContactService {
    boolean createGroupContact(GroupContactDTO groupContactDTO, Account account);

    List<GroupContact> getAllGroupContacts(Account account);

    GroupContact editGroupContact(GroupContact GroupContact);



    GroupContact getGroupContactByName(String name);
    GroupContact getGroupById(int id);

    GroupContact updateGroupContact(GroupContact subcriber);

    Long countTotalGroupContacts();

    GroupContact createNewGroupContact(GroupContact GroupContact);

    List<GroupContact> searchByName(String searchValue);
//
    Long countTotalContactsByGroupId(int groupContactId);
    List<GroupContactSubcriber> getAllSubcriber();
    List<SubcriberDTO> findSubcriberByGroupContactId(int groupContactId);

    boolean deleteSubcriberOutGroup(int subcriberId);

    boolean createGroupContactFromSegment(GroupContactDTO groupContactDTO, Account account);








}
