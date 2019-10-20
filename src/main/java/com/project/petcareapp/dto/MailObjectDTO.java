package com.project.petcareapp.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MailObjectDTO {
    @Email
    @NotNull
    @Size(min = 1, message = "Please, set an email address to send the message to it")
    private String subject;
    private String body;
    private String bodyJson;
    private String from;
    private String fromMail;



}
