package com.project.petcareapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SnsNotificationDTO implements Serializable {

    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("mail")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Mail mails ;

    @JsonProperty("delivery")
    private String delivery;





}
