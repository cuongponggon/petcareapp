package com.project.petcareapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubcriberDTO {
    private int id;


    private String firstName;

    private String lastName;

    private String dob;

    private String address;

    private String phone;

    private String email;

    private String type;

    private String tag;

    boolean blackList;

    private List<GCSubcriberDTO> gcSubcriberDTOS;
    private String createdTime;
}
