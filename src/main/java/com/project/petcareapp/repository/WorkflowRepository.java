package com.project.petcareapp.repository;

import com.project.petcareapp.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Integer> {
    Workflow findByName(String name);

    Workflow findWorkflowById(Integer id);

    @Query("SELECT wl FROM Workflow wl WHERE wl.status = 'Pending' or wl.status='Starting'")
    List<Workflow> findWorkflowByStatus(String status);


    @Query("SELECT wl FROM Workflow wl WHERE wl.status = 'Pending' or wl.status='Starting'")
    int find(String status);





}
