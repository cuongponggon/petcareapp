package com.project.petcareapp.service;

import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Template;

import java.util.List;


public interface TemplateService {
    boolean createTemplate(Template template, Account account);


    List<Template> getAllTemplates();

    List<Template> getAllTemplatesbyType(String type);

    Template updateTemplate(Template template);

    Template editTemplate(Template template);

//    Template getTemplateById(int id);

    boolean editTemplate(int id);

    boolean copyTemplateGallery(int id, String name, Account account);



//    List<Template> searchByNameorType( String searchValue);
}
