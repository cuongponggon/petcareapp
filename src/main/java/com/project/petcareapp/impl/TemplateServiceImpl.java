package com.project.petcareapp.impl;

import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Template;
import com.project.petcareapp.repository.TemplateRepository;
import com.project.petcareapp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    TemplateRepository templateRepository;


    @Override
    public boolean createTemplate(Template template, Account account) {
            System.out.println(template.getNameTemplate());
            Template checkExistedTemplate = templateRepository.findByNameTemplate(template.getNameTemplate());
            if (checkExistedTemplate != null) {
                return false;
            }


            template.setAccount_id(account.getId());
            template.setNameTemplate(template.getNameTemplate());
            if(account.getRole().getRoleName().equalsIgnoreCase("Admin") ){
                template.setType("tp");
            } else template.setType("ct");
            template.setCreated_time(LocalDateTime.now().toString());
            template.setContentHtml(template.getContentHtml());
            template.setContentJson(template.getContentJson());
        templateRepository.save(template);
        String previewImage = convertHtmlToString(template.getContentHtml(),  template.getId());
        template.setPreview(previewImage);
        templateRepository.save(template);
            return true;
    }

    @Override
    public boolean copyTemplateGallery(int templateId, String name,Account account) {
        Template templateGallery = templateRepository.findTemplateById(templateId);
        Template templateTemp = templateRepository.findByNameTemplate(name);
        Template template = new Template();

        if(templateGallery != null && templateTemp == null) {
            System.out.println("Test Account");
//            template.setAccount_id(account.getId());
            template.setAccount_id(account.getId());

            //Lỗi dòng này nha m? sao nó k get dc account ne
            System.out.println("Not Account");
            // ko get dc account nè m?
            // Ko truyền được account vô nè. bị null ngay account
            template.setContentHtml(templateGallery.getContentHtml());
            template.setContentJson(templateGallery.getContentJson());


            template.setCreated_time(LocalDateTime.now().toString());
            template.setNameTemplate(name);
            if(account.getRole().getRoleName().equalsIgnoreCase(("Admin"))){
                template.setType("tp");
            }
            else template.setType("ct");
            templateRepository.save(template);
            String previewImage = convertHtmlToString(template.getContentHtml(), template.getId());
            template.setPreview(previewImage);
            templateRepository.save(template);
            return true;
        }
            return false;
    }

    @Override
    public List<Template> getAllTemplates() {
        System.out.println("hehe");
        return templateRepository.findAll();
    }

    @Override
    public List<Template> getAllTemplatesbyType(String type) {
        if(type.equalsIgnoreCase("mindsending")){
            List<Template> tp1 = templateRepository.findByType("iv");
            List<Template> tp2 = templateRepository.findByType("tp");
            List<Template> custom = new ArrayList<>();
            custom.addAll(tp1);
            custom.addAll(tp2);
        return custom;
        } else return templateRepository.findByType(type);
    }

    @Override
    public Template updateTemplate(Template template) {
        return null;
    }

    @Override
    public Template editTemplate(Template template) {

        Template templateEdit = templateRepository.findTemplateById(template.getId());
        if (templateEdit == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "This template is not exist!");
        }

        templateEdit.setNameTemplate(template.getNameTemplate());
//        templateEdit.setType("ct");
        templateEdit.setContentHtml(template.getContentHtml());
        String previewImage = convertHtmlToString(template.getContentHtml(), template.getId());
        template.setPreview(previewImage);
        templateEdit.setContentJson(template.getContentJson());

        return templateRepository.save(templateEdit);


    }

//    @Override
//    public Template getTemplateById(int id) {
//        return templateRepository.findById(id);
//    }

    @Override
    public boolean editTemplate(int id) {
        return false;
    }


//    @Override
//    public List<Template> searchByNameorType(String searchValue) {
//        return templateRepository.searchByNameorType(searchValue);
//    }

    private static String convertHtmlToString(String html, int id){
        int index = html.indexOf("<body");
        int index1 = html.indexOf("</html>");
        System.out.println(index + " --" + index1);
        String a = html.substring(index, index1);
        a = "<html>"+a+"</html>";
        System.out.println(a);
// html = html.replaceFirst(a, "");
//        System.out.println(html);
        JLabel label = new JLabel(a);

        label.setSize(550, 800);

        BufferedImage image = new BufferedImage(
                label.getWidth(), label.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        {
            Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            label.paint(g);
            g.dispose();
        }
        String imgstr = null;
        // get the byte array of the image (as jpeg)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
//            ImageIO.write(image, "png", new File("aaaa.png"));
            ImageIO.write(image, "png",new File("E:\\EmailMarketing\\EmailMarketing\\Code\\Front-End\\mind-sending\\src\\assets\\img\\"+id+".png"));
             imgstr = Integer.toString(id);
            return imgstr;
        }catch(Exception e){

        }
        return imgstr;
    }
    private static String concatenateProperties(String oldProp, String newProp) {
        oldProp = oldProp.trim();
        if (!newProp.endsWith(";"))
            newProp += ";";
        return newProp + oldProp; // The existing (old) properties should take precedence.
    }
    private static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageString;
    }

}
