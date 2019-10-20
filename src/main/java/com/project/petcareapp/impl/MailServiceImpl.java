package com.project.petcareapp.impl;

import com.project.petcareapp.service.MailService;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {




    @Autowired
    public JavaMailSender emailSender;


    @Autowired
    private SpringTemplateEngine templateEngine;
        //Ver 2
//    static final String CONFIGSET = "SES-SNS";
//    static final String HOST = "email-smtp.us-west-2.amazonaws.com";
//    static final int PORT = 587;
//
//    // Replace smtp_username with your Amazon SES SMTP user name.
//    static final String SMTP_USERNAME = "AKIAYD4J7KHBZ5CEMVEB";
//
//    // Replace smtp_password with your Amazon SES SMTP password.
//    static final String SMTP_PASSWORD = "BIX35Wr2KHwQki6PWcoezK37aH1bvyHRU9sbNNWOz6G3";

    static final String CONFIGSET = "Engagement";
    static final String HOST = "email-smtp.us-west-2.amazonaws.com";
    static final int PORT = 587;

    // Replace smtp_username with your Amazon SES SMTP user name.
    static final String SMTP_USERNAME = "AKIAXTZGLCQ6D5TDU5KZ";

    // Replace smtp_password with your Amazon SES SMTP password.
    static final String SMTP_PASSWORD = "BOTVwUto/Dcqr+iURWA7NUSrSN8o3zisLqHl849z0ZVi";





//    @Override
//    public void sendSimpleMessage(String from, String fromMail,String[]to, String subject, String body) {
//        try {
//
//
//            MimeMessage message = emailSender.createMimeMessage();
//
//            Session session = Session.getInstance(properties, null);
//            Transport transport = session.getTransport();
//            transport.connect();
//            MimeMessageHelper helper = new MimeMessageHelper(message,
//                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                    StandardCharsets.UTF_8.name());
//            message.setFrom(new InternetAddress(fromMail, from));
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            message.setContent(body,"text/html");
//            message.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
//
//            emailSender.send(message);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//
//    }Map

    @Override
    public String sendSimpleMessageV2(String from, String fromMail, String to, String subject, String body) {
        String messageId = "";
        try {

            Properties properties = System.getProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.debug", "true");
            Session session = Session.getInstance(properties, null);
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            message.setFrom(new InternetAddress(fromMail, from));

            helper.setTo(to);
            helper.setSubject(subject);
            message.setContent(body,"text/html");
            message.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);

            Transport transport = session.getTransport();
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            if (!transport.isConnected())//make sure the connection is alive
                transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            if (transport instanceof SMTPTransport){

                String response = ((SMTPTransport) transport).getLastServerResponse();
                    System.out.println(response.split(" ")[2]);
                    messageId = response.split(" ")[2];

            }


        }catch (Exception e) {

            e.printStackTrace();
        }
        return messageId;
    }


    @Override
    public String sendAppointment(String from, String fromMail, String to, String subject, String body) {
        String appointmentMessageId = "";
        try {

            Properties properties = System.getProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.debug", "true");
            Session session = Session.getInstance(properties, null);
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            message.setFrom(new InternetAddress(fromMail, from));

            helper.setTo(to);
            helper.setSubject(subject);
            message.setContent(body, "text/html");

            message.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);

            Transport transport = session.getTransport();
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            if (transport instanceof SMTPTransport){

                String response = ((SMTPTransport) transport).getLastServerResponse();
                System.out.println(response.split(" ")[2]);
                appointmentMessageId = response.split(" ")[2];

            }

        }catch (Exception e) {

            e.printStackTrace();
        }
        return appointmentMessageId;

    }



    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }




}
