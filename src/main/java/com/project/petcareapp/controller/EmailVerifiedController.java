package com.project.petcareapp.controller;

import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.EmailVerified;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.service.EmailVerifiedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")

@CrossOrigin(origins = "*", allowedHeaders = "*")

public class EmailVerifiedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailVerifiedController.class);
//    private final AuthenticationManager authenticationManager;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;


    @Autowired
    EmailVerifiedService emailVerifiedService;

    @Autowired
    AccountRepository accountRepository;



    @GetMapping("/emailverified")
    public List<EmailVerified> getAllEmailVerified(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        return emailVerifiedService.getEmailVerifed(account.getId());

    }

    @PostMapping("emailverified/verify")
    public ResponseEntity verifyEmail(@RequestBody EmailVerified emailVerified, HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        Account account = accountRepository.findAccountByUsername(username);
        boolean flag = emailVerifiedService.verifyEmail(emailVerified,account.getId());
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Verify fail");
        }
        return ResponseEntity.status(ACCEPTED).body("Sent mail to verify");

    }





    }
