package com.project.petcareapp.repository;
import com.project.petcareapp.model.Campaign;
import com.project.petcareapp.model.CampaignGroupContact;
import com.project.petcareapp.model.CampaignGroupContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CampaignGroupContactRepository extends JpaRepository<CampaignGroupContact,Integer> {


        @Query("SELECT com.campaign FROM CampaignGroupContact com WHERE com.campaign.id  = :campaignId")
        List<Campaign> findByCampaignGroupCampaignId(@Param("campaignId") int campaignId);

        @Transactional
        @Modifying(clearAutomatically = true)
        @Query("DELETE FROM CampaignGroupContact com WHERE com.campaign.id  = :campaignId")
        void  deleteCampaignFromCampaginGroup(@Param("campaignId") int campaignId);


//        @Query("SELECT gr.name " +
//                "FROM CampaignGroupContact com JOIN GroupContact gr ON com.groupContact.id = gr.id " +
//                "WHERE com.campaign.id  = :campaignId")
//        String[] findGroupByCampaignId(@Param("campaignId") int campaignId);




}
