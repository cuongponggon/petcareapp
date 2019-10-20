package com.project.petcareapp.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressResult;
import com.project.petcareapp.Utils.Ultilities;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.service.AccountService;
import com.project.petcareapp.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static javax.security.auth.callback.ConfirmationCallback.OK;
import static org.springframework.http.HttpStatus.*;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")

@CrossOrigin(origins = "*", allowedHeaders = "*")

public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
//    private final AuthenticationManager authenticationManager;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;


    @Autowired
    AccountRepository accountRepository;

    @Autowired
     AccountService accountService;


    MailService mailService;


    //    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }


    @GetMapping("account/getIDByUsername/{username}")
    public Integer getIDByUsername(@PathVariable(value = "username") String username) {
        return accountService.getIDByUsername(username);
    }

    @PostMapping("sign-up")
    public ResponseEntity createAccount(@RequestBody Account account) {
        boolean flag = accountService.createAccount(account);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(awsRegion).withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).build();
        VerifyEmailAddressResult res = client.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(account.getEmail()));
        if (flag == false) {
            return ResponseEntity.status(CONFLICT).body("Username của đã tồn tại, vui lòng chon username khác");
        }
        return ResponseEntity.status(CREATED).body("Đăng kí thành công. Please Verify Email to Accept Google ");

    }


    @PutMapping("account/editProfile")
    public ResponseEntity updateProfile(@RequestBody Account account) {
        Account accountEdited = accountService.editProfile(account);
        if (accountEdited != null) {
            return ResponseEntity.status(OK).body("Successfully");
        }
        return ResponseEntity.status(NOT_FOUND).body("Tài khoản này không tồn tại");
    }
    @GetMapping("account/{id}")
    public Account getAccount(@PathVariable(value = "id") int id) {
        return accountService.getAccountById(id);
    }
//
//    @GetMapping("accounts/allAccountByAuthorityId")
//    public List<Account> getAllAccountByAuthorityId(@RequestParam(value = "authorityId") int authorityId) {
//        return accountService.getAllAccountByauthorityId(authorityId);
//    }

    @PostMapping("/account/search/{searchValue}")
    public ResponseEntity<Page<Account>> searchAccount(@PathVariable(value = "searchValue") String searchValue,
                                                       @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "5") Integer size){
        Sort sortable = null;
        if (sort.equals("ASC")) {
            sortable = Sort.by(Account.PROP_USERNAME).ascending();
        }
        if (sort.equals("DESC")) {
            sortable = Sort.by(Account.PROP_USERNAME).descending();
        }
        Pageable pageable = PageRequest.of(page, size, sortable);
        Page<Account> vm = accountService.searchByUsernameOrFullname(pageable,searchValue);
        LOGGER.info("Search Account: " + vm.getTotalElements());
        return new ResponseEntity<Page<Account>>(vm, HttpStatus.OK);
    }



    @PostMapping("/account/update")
    public boolean updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    @GetMapping("accountByToken")
    public String getAccountByToken(HttpServletRequest request) {
        String username = Ultilities.getUsername(request);
        System.out.println("USER NAME IS :" + username);
        Account account = accountRepository.findAccountByUsername(username);
        System.out.println("ROLE IS " + account.getRole().getRoleName());
        return account.getRole().getRoleName();
    }

    @PostMapping("/account/changePasswordOfProfile/")
    public boolean changePasswordOfProfile(@RequestParam(name = "accountID") String accountID,
                                           @RequestParam(name = "oldPass") String oldPass,
                                           @RequestParam(name = "newPass") String newPass) {
        return accountService.changePasswordOfProfile(accountID, oldPass, newPass);
    }

    @PostMapping("/account/delete/")
    public boolean deleteAccountInStore(@RequestParam(name = "accID") int accountID) {
        return accountService.deleteAccount(accountID);
    }

}
