package com.project.petcareapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupContactDTO {

    private int id;

    private String name;

    private String description;

    private String created_time;

    private String updated_time;

    private List<SubcriberGCDTO> subcriberGCDTOS;

}
