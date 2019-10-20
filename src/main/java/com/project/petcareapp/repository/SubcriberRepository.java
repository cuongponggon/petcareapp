package com.project.petcareapp.repository;

import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Subcriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubcriberRepository extends JpaRepository<Subcriber, Integer> {
    Subcriber findByEmailAndAccount(String email, Account account);
    Subcriber findByEmail(String email);

    @Query("SELECT su FROM Subcriber su where LOWER(su.email) in :searchMail")
    List<Subcriber> findByEmailInList(@Param("searchMail") List<String> searchMail);



    @Query("SELECT su.email FROM Subcriber su")
    List<String>listEmailSubcriber();

    @Query("SELECT su FROM Subcriber su WHERE " +
            "(LOWER(su.lastName) like %:searchValue% or su.email like %:searchValue%) ")
    List<Subcriber> searchByEmailAndName(@Param("searchValue") String searchValue);

    @Query("SELECT gr FROM GroupContactSubcriber gr WHERE gr.subcriber.id = :subcriberId AND gr.groupContact.id = :groupContactId")
    Subcriber findSubcriberExisted(@Param("subcriberId") int subcriberId, @Param("groupContactId") int groupContactId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE GroupContactSubcriber gr SET gr.active = false WHERE gr.subcriber.id  = :subcriberId ")
    void  deleteSubcriberFromGroup(@Param("subcriberId") int subcriberId);
//


    Subcriber findSubcriberById(Integer id);

    List<Subcriber> findSubcriberByAccount_id(Integer id);


    Subcriber findSubcriberByEmail(String Email);

    int countAllById(int subcriberId);

    List<Subcriber> findTop5ByOrderByCreatedTimeDesc();

    long countByType(String type);

    List<Subcriber> findAllByAccount_idOrderByCreatedTimeDesc(int accountId);

    //Segment
    List<Subcriber> findAllByLastNameContains(String name);

    @Query("SELECT sub FROM Subcriber sub WHERE sub.lastName NOT LIKE  %:name%")
    List<Subcriber> findAllByLastNameNotLike(@Param("name") String name);

    List<Subcriber> findAllByLastNameIs(String name);

    List<Subcriber> findAllByLastNameIsNot(String name);

    //Find All By Emails
    List<Subcriber> findAllByEmailContains(String mail);

    @Query("SELECT sub FROM Subcriber sub WHERE sub.email NOT LIKE  %:email%")
    List<Subcriber> findAllByEmailNotLike(@Param("email") String email);

    List<Subcriber> findAllByEmailIs(String mail);

    List<Subcriber> findAllByEmailIsNot(String mail);

    //Address
    List<Subcriber> findAllByAddressContains(String address);

    //Dob
    List<Subcriber> findAllByDobBefore(String date);

    List<Subcriber> findAllByDobAfter(String date);

    List<Subcriber> findAllByDob(String date);

    //Created Time
    List<Subcriber> findAllByCreatedTimeContains(String date);

    List<Subcriber> findAllByCreatedTimeBefore(String date);

    List<Subcriber> findAllByCreatedTimeAfter(String date);

    //Group

    @Query("SELECT gr.subcriber FROM GroupContactSubcriber gr WHERE gr.groupContact.id = :groupContactId")
    List<Subcriber> findAllByGroupContact(@Param("groupContactId") int groupContactId);

    @Query("SELECT gr.subcriber FROM GroupContactSubcriber gr WHERE gr.groupContact.id <> :groupContactId")
    List<Subcriber> findAllByGroupContactNot(@Param("groupContactId") int groupContactId);


    //Level
    List<Subcriber> findAllByTypeContains(String type);

//    @Query("SELECT sub FROM Subcriber sub WHERE sub.type NOT LIKE  %:type%")
//    List<Subcriber> findAllByTypeNotLike(@Param("type") String type);

    List<Subcriber>findSubcriberByTypeIsNot(String type);

    List<Subcriber> findAllByOrderByCreatedTimeDesc();










}
