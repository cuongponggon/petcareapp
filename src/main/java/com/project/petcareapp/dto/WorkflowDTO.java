package com.project.petcareapp.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkflowDTO implements Serializable {
    private String workflowName;
    private String status;
    private String createdTime;
    private List<GCWorkflowDTO> gcWorkflowDTOS;
    private String type;

    private String wtWorkflowDTOS;



}
