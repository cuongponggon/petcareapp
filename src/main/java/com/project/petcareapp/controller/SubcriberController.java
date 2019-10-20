package com.project.petcareapp.controller;

import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.dto.*;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Subcriber;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.repository.SubcriberRepository;
import com.project.petcareapp.service.SubcriberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")

@CrossOrigin(origins = "*", allowedHeaders = "*")

public class SubcriberController {
    private final SubcriberRepository subcriberRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(SubcriberController.class);
    @Autowired
    SubcriberService subcriberService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    public SubcriberController(SubcriberRepository subcriberRepository) {
        this.subcriberRepository = subcriberRepository;
    }


    @GetMapping("/subcribers")
    Iterable<Subcriber> getAll() {
        return subcriberRepository.findAllByOrderByCreatedTimeDesc();
    }



    @GetMapping("/subcribersV2")
    public ResponseEntity<List<SubcriberDTO>> getAllSubcriber(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        List<SubcriberDTO> vms = subcriberService.getAllSubcriberV2(account.getId());
        return new ResponseEntity<List<SubcriberDTO>>(vms, HttpStatus.OK);
    }

    @GetMapping(value="subcriber/{id}")
    SubcriberViewDTO read(@PathVariable int id) {
        return subcriberService.getSubcriberById(id);
    }

    @PostMapping("subcriber/create")
    public ResponseEntity createSubcriber(@RequestBody SubcriberDTO dto, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = subcriberService.createSubcrbier(dto,account);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Email Existed");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }

    @PutMapping("subcriber/movetoblacklist/{id}")
    public ResponseEntity moveToBlackList(@PathVariable int id) {
        boolean flag = subcriberService.moveToBlackList(id);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Subcriber Not Found");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }



    @PostMapping("subcriber/createForm")
    public ResponseEntity createSubcriberForm(@RequestBody SubcriberFormDTO dto) {
        boolean flag = subcriberService.createSubcriberForm(dto);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Email Existed");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }

    @PostMapping("subcriber/createListSubcriber")
    public ResponseEntity createListSubcriber(@RequestBody List<SubcriberDTO> dtos, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = subcriberService.createListSubcrbier(dtos,account);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Email đã tồn tại vui lòng thêm email khác");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }

    @PostMapping("subcriber/getSubcriberBySegment")
    public List<Subcriber> getSubcriberBySegment(@RequestBody List<SegmentDTO> dtos, @RequestParam String condition) {
        return subcriberService.getSubcriberBySegment(dtos,condition);
    }


    @PutMapping("subcriber/edit/{id}")
    Subcriber update(@RequestBody Subcriber updatingSubcriber, @PathVariable int id) {
        return subcriberRepository.findById(id)
                .map(subcriber -> {
                    subcriber.setEmail(updatingSubcriber.getEmail());
                    subcriber.setFirstName(updatingSubcriber.getFirstName());
                    subcriber.setLastName(updatingSubcriber.getLastName());
                    subcriber.setDob(updatingSubcriber.getDob());
                    subcriber.setPhone(updatingSubcriber.getPhone());
                    subcriber.setAddress(updatingSubcriber.getAddress());
                    subcriber.setUpdatedTime(LocalDateTime.now().toString());
                   return subcriberRepository.save(subcriber);
                })
                .orElseGet(() -> {
                    updatingSubcriber.setId(id);

                    return subcriberRepository.save(updatingSubcriber);
                });

    }

//
    @GetMapping("/subcriber/getAllSubcriberByAccountId")
    public List<Subcriber> getAllSubcriberByAccountId(@RequestParam(value = "account_id") int accountId) {
        return subcriberService.getSubcriberByAccountId(accountId);
    }

    @PostMapping("/subcriber/search/{searchValue}")
    public List<Subcriber> searchSubcriber(@PathVariable(value = "searchValue") String searchValue){
        return subcriberService.searchByNameorEmail(searchValue);
    }

    @DeleteMapping("/delete-subcriber")
    public ResponseEntity<String> deleteSubcriber(@RequestParam int id, @RequestParam int groupId) {
        String message = subcriberService.deleteSubcriber(id,groupId);
        LOGGER.info("delete contact: " + id);
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
    //5 thằng sub mới
    @GetMapping("/subcriber/latest")
    public List<Subcriber> getSubcriberLatest() {
        return subcriberService.getContactLatest();
    }

    @GetMapping("/subcriber/dashboard")
    public ResponseEntity<StatisticContactDTO> getStatistic() {
        StatisticContactDTO vms = subcriberService.countSubcriber();
        return new ResponseEntity<StatisticContactDTO>(vms, HttpStatus.OK);
    }

    @GetMapping("/subcriber/statistic")
    public ResponseEntity getStatisticCampaign() {
        subcriberService.getStatisticSubcriber();
        return ResponseEntity.status(HttpStatus.OK).body("Successfully");
    }

    @GetMapping("/subcriber/autoupdate")
    public ResponseEntity autoUpdatePoing() {
        subcriberService.autoUpdatePointSubcriber();
        return ResponseEntity.status(HttpStatus.OK).body("Successfully");
    }





    }
