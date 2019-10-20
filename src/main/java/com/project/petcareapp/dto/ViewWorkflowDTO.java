package com.project.petcareapp.dto;

import com.project.petcareapp.model.Appointment;
import com.project.petcareapp.model.Campaign;
import lombok.Data;

import java.util.List;

@Data
public class ViewWorkflowDTO {
   private Campaign campaign;
   private Appointment appointment;
   private List<String> subcriersComing;
   private List<String> subcriberInTask;



}
