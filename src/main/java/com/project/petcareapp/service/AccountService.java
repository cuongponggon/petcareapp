package com.project.petcareapp.service;

import com.project.petcareapp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface AccountService {
    boolean createAccount(Account account);

    List<Account> getAllAccounts();

    Account editProfile(Account account);

//    List<Account> getAllAccountsByStaff();

    Account getAccountById(int id);

    Account loginForStaff(String username, String password);

//    List<Account> getAllAccountByauthorityId(int authorityId);

    boolean updateAccount(Account account);

//    int countTotalUserAccount(int authorityId);

    Account createNewAccount(Account account);

    Account getAccountByUsername(String username);
//    List<Account> getAllAccountsByCucountAllByauthorityIdstomer();
    Account loginForCustomer(String username, String password);
    Page<Account> searchByUsernameOrFullname(Pageable pageable, String searchValue);
    Integer getIDByUsername(String username);
    boolean changePasswordOfProfile(String accountID, String oldPass, String newPass);
    boolean deleteAccount(int id);
}
