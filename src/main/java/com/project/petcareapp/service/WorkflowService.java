package com.project.petcareapp.service;

import com.project.petcareapp.dto.ViewWorkflowDTO;
import com.project.petcareapp.dto.WorkflowDTO;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.Workflow;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface WorkflowService {
    boolean createWorkflow(WorkflowDTO workflowDTO, Account account);

    List<Workflow> getAllWorkflows();

//    Workflow editWorkflow(Workflow workflow);

//    List<Account> getAllAccountsByStaff();

    Workflow getWorkflowById(int id);

    List<String> findSubcriberInTask(int workflowId, String shapeId);

    List<String> findSubcriberIncoming(int workflowId, String shapeId);

    public void runWorkflow();

    void pauseWorkflow(int id);

    void restartWorkflow(int id);

    ViewWorkflowDTO viewWorkflowDTO(int workflowId, String shapeId);



//    Workflow getWorkflowByUsername(String username);
//    List<Account> getAllAccountsByCucountAllByauthorityIdstomer();
//Workflow loginForCustomer(String username, String password);

}
