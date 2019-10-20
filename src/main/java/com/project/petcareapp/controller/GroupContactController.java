package com.project.petcareapp.controller;


import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.dto.GroupContactDTO;
import com.project.petcareapp.dto.SubcriberDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.GroupContact;
import com.project.petcareapp.model.GroupContactSubcriber;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.repository.GroupContactRepository;
import com.project.petcareapp.service.GroupContactService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")@CrossOrigin(origins = "*")
public class GroupContactController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupContactController.class);

    @Autowired
    GroupContactService groupContactService;

    @Autowired
    GroupContactRepository groupContactRepository;

    @Autowired
    AccountRepository accountRepository;




    @GetMapping("/groupContacts")
    public List<GroupContact> getAllGroups(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        return groupContactService.getAllGroupContacts(account);
    }

    @GetMapping("/groupContact/contactById")
    public GroupContact getGroupById(@RequestParam(value = "id") int id) {
        return groupContactService.getGroupById(id);
    }

//    ko dung
    @GetMapping("/groupContacts/subcribers")
    public List<GroupContactSubcriber> getAllSubcribers(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        return groupContactService.getAllSubcriber();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 409, message = "Existed Group"),
    })
    @PostMapping("/groupContact/create")
    public ResponseEntity createGroupContact(@RequestBody GroupContactDTO groupContactDTO, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = groupContactService.createGroupContact(groupContactDTO, account);
        if (flag == false) {

            return ResponseEntity.status(CONFLICT).body("Group Existed");

        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }


    @PostMapping("/groupContact/create/segment")
    public ResponseEntity createGroupContactBySegment(@RequestBody GroupContactDTO groupContactDTO, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = groupContactService.createGroupContactFromSegment(groupContactDTO, account);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Group Existed");
        }
        return ResponseEntity.status(CREATED).body("Successfully");

    }

    @PostMapping("/groupContact/search/{searchValue}")
    public List<GroupContact> searchGroupContact(@PathVariable(value = "searchValue") String searchValue){
        return groupContactService.searchByName(searchValue);
    }
    @GetMapping("/groupContact/count")
    public Long countTotalGroup() {
        return groupContactService.countTotalGroupContacts();
    }

    @GetMapping("/groupContact/countContact/{id}")
    public Long countTotalContactsByGroupId(@PathVariable(value = "id") int id) {
        return groupContactService.countTotalContactsByGroupId(id);
    }
    @GetMapping("/groupContact={id}/contacts")
    public List<SubcriberDTO> findSubcriberByGroupContactId(@PathVariable(value = "id") int id) {
        System.out.println("Tới đây chưa ?");
        return groupContactService.findSubcriberByGroupContactId(id);
    }

    @PutMapping("/groupcontact/edit/{id}")
    GroupContact update(@RequestBody GroupContact updatingGroupContact, @PathVariable int id) {
        return groupContactRepository.findById(id)
                .map(groupContact -> {
                    groupContact.setDescription(updatingGroupContact.getDescription());
                    groupContact.setName(updatingGroupContact.getName());
                    groupContact.setUpdatedTime(LocalDateTime.now().toString());


                    return groupContactRepository.save(groupContact);
                })
                .orElseGet(() -> {
                    updatingGroupContact.setId(id);

                    return groupContactRepository.save(updatingGroupContact);
                });

    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        try {

            groupContactRepository.deleteGroupContactById(id);
            return ResponseEntity.status(ACCEPTED).body("Deleted Successfully");
        }
        catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(CONFLICT).body("This Group can't delete");
        }

    }

    @GetMapping("/groupContact/latest")
    public List<GroupContact> getGroupContactLatest() {
        return groupContactRepository.findTop5ByOrderByCreatedTimeDesc();
    }








}
