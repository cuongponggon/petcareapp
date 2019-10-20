package com.project.petcareapp.service;


import com.project.petcareapp.dto.*;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Subcriber;

import java.util.List;

public interface SubcriberService {
    boolean createSubcrbier(SubcriberDTO dto, Account account);

    boolean createSubcriberForm(SubcriberFormDTO dto);

    boolean createListSubcrbier(List<SubcriberDTO> subcriberDTO, Account account);


    Subcriber editSubcriber(Subcriber subcriber);

    void getStatisticSubcriber();

    List<Subcriber>getContactLatest();

    boolean moveToBlackList(int subcriberId);

    SubcriberViewDTO getSubcriberById(int id);


    List<Subcriber> getSubcriberByAccountId(int accountId);

    Subcriber updateSubcriber(Subcriber subcriber);

    int countTotalSubcriber(int accountId);

    StatisticContactDTO countSubcriber();


    Subcriber getSubcriberByEmail(String email);

    List<Subcriber> searchByNameorEmail(String searchValue);

    List<SubcriberDTO>getAllSubcriberV2(int accountId);

    String deleteSubcriber(int id, int groupId);

    void autoUpdatePointSubcriber();


    List<Subcriber>getSubcriberBySegment(List<SegmentDTO> segmentDTO, String condition);



//    List<Account> getAllAccountsByCustomer();
//    Account loginForCustomer(String username, String password);
}
