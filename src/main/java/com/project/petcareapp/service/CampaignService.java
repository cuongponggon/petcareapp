package com.project.petcareapp.service;

import com.project.petcareapp.dto.CampaignDTO;
import com.project.petcareapp.dto.CampaignFullDTO;
import com.project.petcareapp.dto.MailObjectDTO;
import com.project.petcareapp.dto.SegmentDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Campaign;
import com.project.petcareapp.model.Template;

import java.util.List;

public interface CampaignService {
    boolean createCampaign(MailObjectDTO mailObjectDTO, CampaignDTO campaignDTO, Account account, List<SegmentDTO> segmentDTOs, String condition);
    void sendCampaign(int campaignId);
    boolean createCampaignWithTimer(MailObjectDTO mailObjectDTO, CampaignDTO campaignDTO, Account account);
    boolean createAutoResponseCampaign(MailObjectDTO mailObjectDTO, int groupId, Template template);

     boolean editCampaign(MailObjectDTO mailObjectDTO, CampaignDTO campaignDTO, int id, List<SegmentDTO> segmentDTOs, String condition);
    Campaign addContentToCampaign(Campaign campaign);

    CampaignFullDTO getCampaignById(int id);

    int copyCampaign(int campaignId, int workflowId, Account account);


    void getStatisticCampaign();

    CampaignFullDTO getCampaignLatest(Account account);

    boolean copyCampaign(int campaignId, String name, Account account);

    boolean checkDuplicatName(String name, int accountId);

    List<CampaignDTO> getCampaignSegment(int accountId);





}
