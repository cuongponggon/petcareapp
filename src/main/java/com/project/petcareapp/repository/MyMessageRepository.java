package com.project.petcareapp.repository;

import com.project.petcareapp.model.MyMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface MyMessageRepository extends JpaRepository<MyMessage,Integer> {

    @Query("SELECT mes.content FROM MyMessage mes WHERE mes.id = :id and mes.content = :content")
    String findContentByMessageId(@Param("id") int id, @Param("content") String content);






}
