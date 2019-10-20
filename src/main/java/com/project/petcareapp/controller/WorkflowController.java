package com.project.petcareapp.controller;

import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.dto.ViewWorkflowDTO;
import com.project.petcareapp.dto.WorkflowDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Workflow;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.repository.SubcriberRepository;
import com.project.petcareapp.repository.WorkflowRepository;
import com.project.petcareapp.service.WorkflowService;
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
import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")
@Transactional
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WorkflowController {


    @Autowired
    private final WorkflowRepository workflowRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowController.class);
    @Autowired
    WorkflowService workflowService;
//    SubcriberService subcriberService;

    @Autowired
    SubcriberRepository subcriberRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    public WorkflowController(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }




    @ApiOperation(value = "Create Workflow")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @PostMapping(value="workflow/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCampaignWithoutTemplate(@RequestBody WorkflowDTO workflowDTO, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = workflowService.createWorkflow(workflowDTO, account);


        return ResponseEntity.status(CREATED).body("aaa");

    }


    @GetMapping("/workflows")
    Iterable<Workflow> getAll() {
        return workflowRepository.findAll();
    }


    @GetMapping("workflow/task")
    public List<String> findSubcriberInTask(@RequestParam(value = "workflowId")int workflowId, @RequestParam(value = "shapeId")String shapeId) {
        List<String> sucribers = workflowService.findSubcriberInTask(workflowId,shapeId);
        return sucribers;
    }

    @GetMapping("workflow/pretask")
    public List<String> findSubcriberInComing(@RequestParam(value = "workflowId")int workflowId, @RequestParam(value = "shapeId")String shapeId) {
        List<String> sucribers = workflowService.findSubcriberIncoming(workflowId,shapeId);
        return sucribers;
    }

    @GetMapping("/workflow/view")
    public ResponseEntity<ViewWorkflowDTO> getWorkflowDTO(@RequestParam(value = "workflowId")int workflowId, @RequestParam(value = "shapeId")String shapeId) {
        ViewWorkflowDTO vms = workflowService.viewWorkflowDTO(workflowId,shapeId);
        return new ResponseEntity<ViewWorkflowDTO>(vms, HttpStatus.OK);
    }

    @PutMapping("workflow/pause/{id}")
    public ResponseEntity pauseWorkflow(@PathVariable int id) {
        workflowService.pauseWorkflow(id);
        return ResponseEntity.status(ACCEPTED).body("Successfully");
    }

    @PutMapping("workflow/restart/{id}")
    public ResponseEntity restartWorkflow(@PathVariable int id) {
        workflowService.restartWorkflow(id);
        return ResponseEntity.status(ACCEPTED).body("Successfully");
    }





    }

