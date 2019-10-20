package com.project.petcareapp.controller;

import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.dto.CampaignDTO;
import com.project.petcareapp.dto.CampaignFullDTO;
import com.project.petcareapp.dto.MailObjectDTO;
import com.project.petcareapp.dto.SegmentDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Campaign;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.repository.CampaignRepository;
import com.project.petcareapp.repository.SubcriberRepository;
import com.project.petcareapp.service.CampaignService;
import com.project.petcareapp.service.SubcriberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CampaignController {
    private final CampaignRepository campaignRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);

    CampaignService campaignService;

    SubcriberService subcriberService;

    @Autowired
    SubcriberRepository subcriberRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    public CampaignController(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    static class MailAndCampaign {
        public MailObjectDTO mailObjectDTO;
        public CampaignDTO campaignDTO;
        public List<SegmentDTO> segmentDTOs;
        public String condition;
    }

    //    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }

    @ApiOperation(value = "Create Campaign Template")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping(value = "campaign/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCampaignWithoutTemplate(@RequestBody MailAndCampaign mailAndCampaign, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = campaignService.createCampaign(mailAndCampaign.mailObjectDTO, mailAndCampaign.campaignDTO, account, mailAndCampaign.segmentDTOs, mailAndCampaign.condition);

        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Campaign Existed");
        }
        Campaign temp = campaignRepository.findByNameAndAccount_id(mailAndCampaign.campaignDTO.getCampaignName(), account.getId());
        return ResponseEntity.status(CREATED).body(temp.getId());

    }

    @ApiOperation(value = "Create Campaign With Timer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping(value = "campaign/create/timer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCampaignWithoutTimer(@RequestBody MailAndCampaign mailAndCampaign, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);

        LocalDate date = LocalDate.parse(mailAndCampaign.campaignDTO.getTimeStart(), DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body("This time is over");
        }

        boolean flag = campaignService.createCampaignWithTimer(mailAndCampaign.mailObjectDTO, mailAndCampaign.campaignDTO, account);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Campaign Existed");
        }
        Campaign temp = campaignRepository.findByNameAndAccount_id(mailAndCampaign.campaignDTO.getCampaignName(), account.getId());

        return ResponseEntity.status(CREATED).body(temp.getId());

    }

//    @GetMapping(value="campaign/{id}")
//    Campaign read(@PathVariable int id) {
//        return campaignRepository.findById(id)
//        .orElseThrow(() -> new RuntimeException("Not found"));
//    }

    @GetMapping(value = "campaign/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    CampaignFullDTO read(@PathVariable int id) {
        return campaignService.getCampaignById(id);
    }

    @PutMapping("campaign/edit/{id}")
    public ResponseEntity updateCampaign(@RequestBody MailAndCampaign mailAndCampaign, @PathVariable int id) {
        boolean flag = campaignService.editCampaign(mailAndCampaign.mailObjectDTO, mailAndCampaign.campaignDTO, id, mailAndCampaign.segmentDTOs, mailAndCampaign.condition);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Campaign can not edit");
        }
        return ResponseEntity.status(ACCEPTED).body("Successfully");
    }

    @PutMapping(value = "campaign/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addContent(@RequestBody Campaign campaign) {
        Campaign accountEdited = campaignService.addContentToCampaign(campaign);
        if (accountEdited != null) {
            return ResponseEntity.status(ACCEPTED).body(accountEdited);
        }
        return ResponseEntity.status(NOT_ACCEPTABLE).body("Updated Fail");
    }


    @GetMapping("/campaigns")
    Iterable<Campaign> getAll(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        List<Campaign> campaignList = campaignRepository.findCampaignByAccount_idOrderByCreatedTimeDesc(account.getId()).stream().map(g -> {
            Campaign campaign = new Campaign();
            campaign.setId(g.getId());
            if (g.getName().contains(">")) {
                String[] output = g.getName().split(">");
                campaign.setName(output[0]);
            } else {
                campaign.setName(g.getName());
            }
            campaign.setAutomation(g.getAutomation());
            campaign.setStatus(g.getStatus());
            campaign.setDelivery(g.getDelivery());
            campaign.setOpenRate(g.getOpenRate());
            campaign.setClickRate(g.getClickRate());
            return campaign;
        }).collect(Collectors.toList());
        return campaignList;

    }

    @GetMapping("/campaigns/segment")
    Iterable<CampaignDTO> getCampaignSegment(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        return campaignService.getCampaignSegment(account.getId());
    }

    @ApiOperation(value = "Send Campaign Without Template")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping(value = "campaign/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendCampaign(@RequestParam(value = "id") int id) {
        campaignService.sendCampaign(id);
    }


    @PostMapping("campaign/copy/")
    public ResponseEntity copyCampaign(@RequestParam int id, @RequestParam int workflowId, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        int number = campaignService.copyCampaign(id, workflowId, account);
        if (number != 1) {
            return ResponseEntity.status(CREATED).body("Đã copy thành công ");
        }
        return ResponseEntity.status(CONFLICT).body("Fail");

    }

    @GetMapping("/campaign/dashboard")
    public ResponseEntity<CampaignFullDTO> getStatisticDashboard(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        CampaignFullDTO vms = campaignService.getCampaignLatest(account);
        return new ResponseEntity<CampaignFullDTO>(vms, HttpStatus.OK);
    }

    @GetMapping("/campaign/statistic")
    public ResponseEntity getStatisticCampaign() {
        campaignService.getStatisticCampaign();
        return ResponseEntity.status(HttpStatus.OK).body("Successfully");
    }

    @PostMapping("campaign/duplicate/")
    public ResponseEntity duplicateCampaign(@RequestParam int id, @RequestParam String name, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = campaignService.copyCampaign(id, name, account);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Fail ");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }


    @PostMapping("campaign/check/")
    public ResponseEntity checkDuplicateName(@RequestParam String name, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = campaignService.checkDuplicatName(name, 1);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Fail ");
        }
        return ResponseEntity.status(ACCEPTED).body("Successfully");

    }


}

