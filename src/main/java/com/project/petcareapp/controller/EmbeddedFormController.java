package com.project.petcareapp.controller;

import com.project.petcareapp.dto.EmbeddedFormDTO;
import com.project.petcareapp.model.EmbeddedForm;
import com.project.petcareapp.repository.EmbeddedFormRepository;
import com.project.petcareapp.repository.EmbeddedGroupContactRepository;
import com.project.petcareapp.repository.SubcriberRepository;
import com.project.petcareapp.service.EmbeddedFormService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import static org.springframework.http.HttpStatus.*;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmbeddedFormController {
    private final EmbeddedFormRepository embeddedFormRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedFormController.class);
    @Autowired
    EmbeddedFormService embeddedFormService;


    @Autowired
    SubcriberRepository subcriberRepository;

    @Autowired
    EmbeddedGroupContactRepository embeddedGroupContactRepository;



    @Autowired
    public EmbeddedFormController(EmbeddedFormRepository embeddedFormRepository) {
        this.embeddedFormRepository = embeddedFormRepository;
    }




    @ApiOperation(value = "Create Embedded Form")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Invalid  ID"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @PostMapping(value="form/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createForm(@RequestBody EmbeddedFormDTO embeddedFormDTO) {
        boolean flag = embeddedFormService.createForm(embeddedFormDTO);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Form Existed");
        }
        EmbeddedForm temp = embeddedFormRepository.findEmbeddedFormByName(embeddedFormDTO.getName());
        return ResponseEntity.status(CREATED).body(temp.getId());

    }





    @GetMapping("form/{id}")
    public EmbeddedForm getCampaignById(@PathVariable(value = "id") int id) {
        return embeddedFormService.getFormById(id);
    }

    @PutMapping("form/edit/{id}")
    public ResponseEntity updateForm(@RequestBody EmbeddedFormDTO embeddedFormDTO, @PathVariable int id) {
        boolean flag = embeddedFormService.editForm(embeddedFormDTO,id);
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Embedded can not edit");
        }
        return ResponseEntity.status(ACCEPTED).body("Successfully");
    }




    @GetMapping("/forms")
    Iterable<EmbeddedForm> getAll() {
        return embeddedFormRepository.findAllByOrderByCreatedTimeDesc();
    }
    @RequestMapping(value = "form/delete/{id}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        try {
            embeddedGroupContactRepository.deleteEmbeddedFormFromFormGroup(id);
            embeddedFormRepository.deleteFormById(id);
            return ResponseEntity.status(ACCEPTED).body("Deleted Successfully");
        }
        catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(CONFLICT).body("This form can't delete");
        }

    }







    }

