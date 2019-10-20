package com.project.petcareapp.service;

import com.project.petcareapp.dto.AppointmentDTO;
import com.project.petcareapp.dto.AppointmentFullDTO;
import com.project.petcareapp.dto.MailObjectDTO;
import com.project.petcareapp.dto.SegmentDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Appointment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentService {
//    boolean createCampaign(MailObjectDTO mailObjectDTO, CampaignDTO campaignDTO);
//    void sendCampaign(int campaignId);
//    boolean createCampaignWithTemplate(MailObjectDTO mailObjectDTO, int groupId, Template template);
//    boolean createAutoResponseCampaign(MailObjectDTO mailObjectDTO, int groupId, Template template);
//
//     boolean editCampaign(MailObjectDTO mailObjectDTO, CampaignDTO campaignDTO, int id);
//    Campaign addContentToCampaign(Campaign campaign);
//
//    CampaignFullDTO getCampaginById(int id);
        Appointment findByName(String token);
        Appointment findByToken(String token);

    boolean createAppointment(MailObjectDTO mailObjectDTO, AppointmentDTO appointmentDTO, Account accountId, List<SegmentDTO> segmentDTOs, String condition);

    void sendAppointment(int appointmentId);
    Appointment addContentToAppointment(Appointment appointment);
    boolean editAppointment(MailObjectDTO mailObjectDTO, AppointmentDTO appointmentDTO, int id);
    AppointmentFullDTO getAppointmentById(int id);
    public ResponseEntity<String> acceptAppointment(String token, String email);

    int copyAppointment(int appointmentId, int workflowId, Account account);
    void getStatisticAppointment();

    ResponseEntity<String> denyAppointment(String token, String email);

    List<AppointmentDTO> getAppointmentSegment(int accountId);
}
