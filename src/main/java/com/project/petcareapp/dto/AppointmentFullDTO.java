package com.project.petcareapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(  {"handler","hibernateLazyInitializer"} )
public class AppointmentFullDTO {
    private String name;
    private String status;
    private String createdTime;
    private String type;
    private int id;
    private String time;

    // Mail Object
    private String subject;
    private String body;
    private String bodyJson;
    private String from;
    private String fromMail;

    private String segment;
    private String conditon;

    private String updatedTime;

    //Statistic of Campaign
    private String request;
    private String delivery;
    private String click;
    private String open;

    private String spam;
    private String bounce;


//    private String[] groupContactName;
private List<GCAppointmentDTO> gcAppointmentDTOS;

// List Of Contacts
    @JsonView
    private List<SubcriberViewDTO> contactSpam;
    @JsonView
    private List<SubcriberViewDTO> contactOpened;
    @JsonView
    private List<SubcriberViewDTO> contactClicked;
    @JsonView
    private List<SubcriberViewDTO> contactBounce;
    @JsonView
    private List<SubcriberViewDTO> contactDelivery;
    @JsonView
    private List<SubcriberViewDTO> contactRequest;


}
