package com.project.petcareapp.repository;

import com.project.petcareapp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUsername(String username);

    Account findAccountByUsername(String username);



    Account findAccountById(Integer id);

    @Query("SELECT a FROM Account a WHERE " +
            "(LOWER(a.fullname) like %:searchValue% or a.username like %:searchValue%) " +
            "")
    Page<Account> searchByUsernameOrFullname(Pageable pageable, @Param("searchValue") String searchValue);

    Account findAccountByUsernameAndPassword(String username, String password);



    @Query(value = "Select ac From Account ac Where ac.username = :username and ac.status <> :status")
    Account findByUserLogin(@Param("username") String username, @Param("status") String status);


}
