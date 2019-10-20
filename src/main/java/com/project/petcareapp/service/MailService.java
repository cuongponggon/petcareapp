package com.project.petcareapp.service;

public interface MailService {
//    void sendSimpleMessage(String from, String fromMail,String[] to, String subject, String body);
    String sendSimpleMessageV2(String from, String fromMail, String to, String subject, String body);
    String sendAppointment(String from, String fromMail, String to, String subject, String body);
    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);



}
