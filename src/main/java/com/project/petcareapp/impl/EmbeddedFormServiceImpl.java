package com.project.petcareapp.impl;

import com.project.petcareapp.dto.EmbeddedFormDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.EmbeddedForm;
import com.project.petcareapp.model.FormGroupContact;
import com.project.petcareapp.repository.*;
import com.project.petcareapp.service.EmbeddedFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddedFormServiceImpl implements EmbeddedFormService {
   @Autowired
    EmbeddedFormRepository embeddedFormRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EmbeddedGroupContactRepository embeddedGroupContactRepository;



    @Autowired
    GroupContactRepository groupContactRepository;


    @Override
    public boolean createForm(EmbeddedFormDTO embeddedFormDTO) {
        System.out.println(embeddedFormDTO.getName());
        EmbeddedForm checkExistedForm = embeddedFormRepository.findEmbeddedFormByName(embeddedFormDTO.getName());
        if (checkExistedForm != null) {
            return false;
        }
        EmbeddedForm embeddedForm = new EmbeddedForm();
        Account account = accountRepository.findAccountById(1);
        embeddedForm.setAccount_id(account.getId());
        embeddedForm.setForm(embeddedFormDTO.getForm());
        embeddedForm.setName(embeddedFormDTO.getName());embeddedForm.setCode(embeddedFormDTO.getCode());
        embeddedForm.setCreatedTime(LocalDateTime.now().toString());
        List<FormGroupContact> formGroupContacts = embeddedFormDTO.getGcFormDTOS().stream().map(g->{
            FormGroupContact formGroupContact = new FormGroupContact();
            formGroupContact.setGroupContact(groupContactRepository.findGroupById(g.getGroupContactId()));
            formGroupContact.setCreatedTime(LocalDateTime.now().toString());
            formGroupContact.setEmbeddedForm(embeddedForm);
            return formGroupContact;
        }).collect(Collectors.toList());
        embeddedForm.setFormGroupContacts(formGroupContacts);

        embeddedFormRepository.save(embeddedForm);
        return true;
    }

    @Override
    public boolean editForm(EmbeddedFormDTO embeddedFormDTO, int id) {
        EmbeddedForm embeddedForm = embeddedFormRepository.findEmbeddedFormById(id);
        embeddedGroupContactRepository.deleteEmbeddedFormFromFormGroup(id);
        Account account = accountRepository.findAccountById(1);
        embeddedForm.setAccount_id(account.getId());
        embeddedForm.setName(embeddedFormDTO.getName());
        embeddedForm.setUpdatedTime(LocalDateTime.now().toString());
        embeddedForm.setForm(embeddedFormDTO.getForm());
        embeddedForm.setCode(embeddedFormDTO.getCode());
        embeddedForm.setUpdatedTime(LocalDateTime.now().toString());
        List<FormGroupContact> formGroupContacts = embeddedFormDTO.getGcFormDTOS().stream().map(g->{
            FormGroupContact formGroupContact = new FormGroupContact();
            formGroupContact.setGroupContact(groupContactRepository.findGroupById(g.getGroupContactId()));
            formGroupContact.setCreatedTime(LocalDateTime.now().toString());
            formGroupContact.setEmbeddedForm(embeddedForm);

            return formGroupContact;
        }).collect(Collectors.toList());

        embeddedForm.setFormGroupContacts(formGroupContacts);

        embeddedFormRepository.save(embeddedForm);
        return true;
    }


    @Override
    public EmbeddedForm getFormById(int id) {
        return embeddedFormRepository.findEmbeddedFormById(id);
    }
}
