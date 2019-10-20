package com.project.petcareapp.service;

import com.project.petcareapp.dto.EmbeddedFormDTO;
import com.project.petcareapp.model.EmbeddedForm;

public interface EmbeddedFormService {
    boolean createForm(EmbeddedFormDTO embeddedFormDTO);

     boolean editForm(EmbeddedFormDTO embeddedFormDTO, int id);


    EmbeddedForm getFormById(int id);



}
