package com.project.petcareapp.repository;

import com.project.petcareapp.model.EmbeddedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EmbeddedFormRepository extends JpaRepository<EmbeddedForm,Integer> {
        EmbeddedForm findEmbeddedFormByName(String name);
        EmbeddedForm findEmbeddedFormById(int id);

        @Modifying
        @Transactional
        @Query("DELETE from EmbeddedForm fr where fr.id=:formId")
        void deleteFormById(@Param("formId") int formId);


        List<EmbeddedForm> findAllByOrderByCreatedTimeDesc();






}
