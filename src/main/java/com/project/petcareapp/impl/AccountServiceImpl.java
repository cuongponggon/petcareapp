package com.project.petcareapp.impl;

import com.project.petcareapp.model.Account;
import com.project.petcareapp.repository.AccountRepository;
import com.project.petcareapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
//    @Autowired
//    BCryptPasswordEncoder encoder;
//    private final AccountRepository accountRepository;
//
//    public AccountServiceImpl(AccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//    }

    @Override
    public boolean createAccount(Account account) {
        System.out.println(account.getUsername());
        Account checkExistedAccount = accountRepository.findByUsername(account.getUsername());

        if (checkExistedAccount != null) {
            return false;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        account.setCreatedTime(LocalDateTime.now().toString());
//        account.setPassword(encoder.encode(account.getPassword()));
        account.setPassword(encoder.encode(account.getPassword()));

        accountRepository.save(account);
        return true;
    }

    @Override
    public List<Account> getAllAccounts() {

        return accountRepository.findAll();
    }

    @Override
    public Account editProfile(Account account) {
        try {
            Account checkExistedAccount = accountRepository.findAccountById(account.getId());
            if (checkExistedAccount != null) {
                System.out.println("TEST");
                checkExistedAccount.setEmail(account.getEmail());
                checkExistedAccount.setPhone(account.getPhone());
                checkExistedAccount.setGender(account.getGender());
                checkExistedAccount.setAddress(account.getAddress());
                checkExistedAccount.setUpdatedTime(LocalDateTime.now().toString());
                checkExistedAccount.setFullname(account.getFullname());

                return accountRepository.save(checkExistedAccount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    @Override
//    public List<Account> getAllAccountsByStaff() {
//        return accountRepository.findAllByauthorityIdGreaterThanEqual(3);
//    }
//
//    @Override
//    public List<Account> getAllAccountsByCustomer() {
//        return accountRepository.findAllByauthorityIdGreaterThanEqual(2);
//    }

    @Override
    public Account getAccountById(int id) {
        return accountRepository.findAccountById(id);
    }

    @Override
    public Account loginForStaff(String username, String password) {
        return null;
    }

    @Override
    public boolean updateAccount(Account account) {
        Account exitedAccount = accountRepository.findAccountById(account.getId());
        if (exitedAccount != null) {
            exitedAccount.setFullname(account.getFullname());
            exitedAccount.setGender(account.getGender());
            exitedAccount.setEmail(account.getEmail());
            exitedAccount.setPhone(account.getPhone());
            accountRepository.save(exitedAccount);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Account loginForCustomer(String username, String password) {
        return accountRepository.findAccountByUsernameAndPassword(username, password);
    }

    @Override
     public Page<Account> searchByUsernameOrFullname(Pageable pageable , String searchValue) {
        Page<Account> page = accountRepository.searchByUsernameOrFullname(pageable,searchValue);
        if (page == null || page.isEmpty()) {
            return null;
        }
        return page;
    }

    @Override
    public Integer getIDByUsername(String username) {
        return accountRepository.findByUsername(username).getId();
    }

    @Override
    public boolean changePasswordOfProfile(String accountID, String oldPass, String newPass) {
        Account account = accountRepository.findAccountById(Integer.parseInt(accountID));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(oldPass, account.getPassword())) {
            return false;
        } else {
            account.setPassword(encoder.encode(newPass));
            accountRepository.save(account);
            return true;
        }
    }

    @Override
    public boolean deleteAccount(int accountID) {
        Account exitedAccount = accountRepository.findAccountById(accountID);
        if (exitedAccount != null) {
            exitedAccount.setStatus("Deleted");
            accountRepository.save(exitedAccount);
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public List<Account> getAllAccountByauthorityId(int authorityId) {
//        return accountRepository.findAllByauthorityIdOrderByCreatedTimeDesc(authorityId);
//    }

//    @Override
//    public int countTotalUserAccount(int authorityId) {
//        return accountRepository.countAllByauthorityId(authorityId);
//    }

    @Override
    public Account createNewAccount(Account account) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        account.setPassword(encoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    @Override
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

}
