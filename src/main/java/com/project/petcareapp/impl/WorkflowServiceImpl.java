package com.project.petcareapp.impl;

import com.project.petcareapp.dto.ViewWorkflowDTO;
import com.project.petcareapp.dto.WorkflowDTO;
import com.project.petcareapp.model.*;
import com.project.petcareapp.repository.*;
import com.project.petcareapp.service.AppointmentService;
import com.project.petcareapp.service.CampaignService;
import com.project.petcareapp.service.MailService;
import com.project.petcareapp.service.WorkflowService;
import org.apache.commons.collections4.CollectionUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//import com.emailmkt.emailmarketing.model.Task;
//import com.emailmkt.emailmarketing.repository.TaskRepository;
@Transactional

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    MailService mailService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    CampaignService campaignService;


    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    AppointmentSubcriberRepository appointmentSubcriberRepository;
    @Autowired
    CampaignSubcriberRepository campaignSubcriberRepository;

    @Autowired
    CampaignGroupContactRepository campaignGroupContactRepository;

//    @Autowired
//    WorkflowGroupContactRepository workflowGroupContactRepository;

    @Autowired
    GroupContactRepository groupContactRepository;
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EmbeddedFormRepository embeddedFormRepository;

    @Override
    public boolean createWorkflow(WorkflowDTO workflowDTO, Account account) {
        Workflow newWorkflow = new Workflow();
        newWorkflow.setName(workflowDTO.getWorkflowName());
        newWorkflow.setModel(workflowDTO.getWtWorkflowDTOS());
        newWorkflow.setStatus("Starting");

//        newWorkflow.setWorkflowTasks(workflowTaskList);

        String bpmnString = workflowDTO.getWtWorkflowDTOS();
        InputStream inputStream = new ByteArrayInputStream(bpmnString.getBytes(Charset.forName("UTF-8")));
        org.camunda.bpm.model.bpmn.BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);
        Process process = (Process) modelInstance.getModelElementById("Process_1");
//            System.out.println(format(process.getFlowElements()));
        Collection<FlowElement> elements = process.getFlowElements();
        Iterator<FlowElement> eList = elements.iterator();
        while (eList.hasNext()) {
            String shapeId = eList.next().getId();
            if (shapeId.contains("UserTask")) {
                String name = modelInstance.getModelElementById(shapeId).getAttributeValue("name");
                //find form by name
                EmbeddedForm embeddedForm = embeddedFormRepository.findEmbeddedFormByName(name);
                if (embeddedForm != null) {
                    List<WorkflowGroupContact> workflowGroupContactsGroupContacts = embeddedForm.getFormGroupContacts().stream().map(g -> {
                        WorkflowGroupContact workflowGroupContact = new WorkflowGroupContact();
                        workflowGroupContact.setGroupContact(groupContactRepository.findGroupById(g.getGroupContact().getId()));
                        workflowGroupContact.setWorkflow(newWorkflow);
                        workflowGroupContact.setCreatedTime(LocalDateTime.now().toString());
                        return workflowGroupContact;
                    }).collect(Collectors.toList());

                    newWorkflow.setWorkflowGroupContacts(workflowGroupContactsGroupContacts);
                }// rồi đó m

                workflowRepository.save(newWorkflow);
                break;
            }
        }
        eList = elements.iterator();
        while (eList.hasNext()) {
            String shapeId = eList.next().getId();
            if (shapeId.contains("Task") && !shapeId.contains("UserTask")) {
                String name = modelInstance.getModelElementById(shapeId).getAttributeValue("name");
                org.camunda.bpm.model.bpmn.instance.Task taskModel = (org.camunda.bpm.model.bpmn.instance.Task) modelInstance.getModelElementById(shapeId);
                Collection<FlowNode> sequenceFlowsPrevious = taskModel.getPreviousNodes().list();
                Iterator<FlowNode> sequenceFlowListsPrevious = sequenceFlowsPrevious.iterator();
                Collection<FlowNode> sequenceFlowsNext = taskModel.getSucceedingNodes().list();
                Iterator<FlowNode> sequenceFlowListsNext = sequenceFlowsNext.iterator();
                Task newWorkflowTask = new Task();
                newWorkflowTask.setWorkflow(newWorkflow);
                newWorkflowTask.setName(name);
                newWorkflowTask.setShapeId(shapeId);
                //set task Type
                if (shapeId.contains("SendTask")) {
                    newWorkflowTask.setType("campaign");
                    System.out.println("Campaign name is" + name + account.getId());
                    Campaign campaignTask = campaignRepository.findByNameAndAccount_id(name, account.getId());
                    int campaignOrAppId = campaignService.copyCampaign(campaignTask.getId(), newWorkflow.getId(), account);
                    newWorkflowTask.setCampaignAppointment(campaignOrAppId);
                } else if (shapeId.contains("BusinessRule")) {
                    newWorkflowTask.setType("appointment");
                    Appointment appointmentTask = appointmentRepository.findAppointmentByName(name);

                    int campaignOrAppId = appointmentService.copyAppointment(appointmentTask.getId(), newWorkflow.getId(), account);
                    newWorkflowTask.setCampaignAppointment(campaignOrAppId);
                }

                //Find previous node
                while (sequenceFlowListsPrevious.hasNext()) {
                    FlowNode previousNode = sequenceFlowListsPrevious.next();
                    String previousNodeId = previousNode.getId();
                    if (previousNodeId.contains("Task")) {
                        newWorkflowTask.setPreTask(previousNodeId);

                    } else if (previousNodeId.contains("ExclusiveGateway")) {
                        org.camunda.bpm.model.bpmn.instance.ExclusiveGateway gateway = modelInstance.getModelElementById(previousNodeId);
                        List<FlowNode> prevNodesCollection = gateway.getPreviousNodes().list();
                        Iterator<SequenceFlow> prevFlowCollection = gateway.getOutgoing().iterator();
                        FlowNode conditionNode = prevNodesCollection.get(0);
                        String gatewayNode = previousNode.getName();
                        System.out.println("HU HUHUHUHUHUHUHUHUHUHUHUHUHUHUH" + gateway.getName() + gateway.getAttributeValueNs("magic:targetRef", "http://magic")
                                + gateway.getAttributeValueNs("targetRef", "http://magic") + gateway.getAttributeValue("targetRef")
                        );
                        while (prevFlowCollection.hasNext()) {
                            SequenceFlow sequenceFlow = prevFlowCollection.next();
                            if (sequenceFlow.getTarget().getId().equals(shapeId)) {
                                gatewayNode = gatewayNode.concat(sequenceFlow.getName());
                            }
                        }


                        newWorkflowTask.setGateway(gatewayNode);

                        newWorkflowTask.setPreTask(previousNode.getPreviousNodes().singleResult().getId());
                        newWorkflowTask.setWorkflow(newWorkflow);

//                        newWorkflowTask.setPostTask(nextNodeId);
////                        workflowTaskRepository.save(newWorkflowTask);
//                        workflowTaskList.add(newWorkflowTask);
//                        workflowTaskRepository.save(newWorkflowTask);
                    } else if (name.contains("ExclusiveGateway")) {
                        org.camunda.bpm.model.bpmn.instance.ExclusiveGateway gateway = modelInstance.getModelElementById(name);

                        Collection<FlowNode> nextNodesCollection = gateway.getSucceedingNodes().list();
                        Iterator<FlowNode> nextNodeLists = nextNodesCollection.iterator();
                        Iterator<SequenceFlow> nextFlowCollection = gateway.getIncoming().iterator();

//                        }
                        while (nextNodeLists.hasNext()) {
//                            WorkflowTask newWorkflowTask = new WorkflowTask();
//                            newWorkflowTask.setTask(task);
//                            newWorkflowTask.setWorkflow(newWorkflow);
                            FlowNode conditionNode = nextNodeLists.next();
                            System.out.println(conditionNode.getName() + "---" + conditionNode.getId());

                            Collection<SequenceFlow> f1 = conditionNode.getIncoming();
                            Collection<SequenceFlow> f2 = gateway.getOutgoing();
                            f1.containsAll(f2);
                            SequenceFlow conditionFlow = f1.iterator().next();
//                            newWorkflowTask.setGateway(gateway.getName() + " " + conditionFlow.getName());
//                            newWorkflowTask.setPostTask(conditionNode.getId());
//                            workflowTaskList.add(newWorkflowTask);
//                            workflowTaskRepository.save(newWorkflowTask);
                        }


                    }
                    taskRepository.save(newWorkflowTask);


                }
                //next node
                while (sequenceFlowListsNext.hasNext()) {
                    FlowNode nextNode = sequenceFlowListsNext.next();
                    String nextNodeId = nextNode.getId();
                    if (nextNodeId.contains("Task")) {
                        newWorkflowTask.setPostTask(nextNodeId);

                    } else if (nextNodeId.contains("ExclusiveGateway")) {
                        org.camunda.bpm.model.bpmn.instance.ExclusiveGateway gateway = modelInstance.getModelElementById(nextNodeId);
                        List<FlowNode> nextNodesCollection = gateway.getSucceedingNodes().list();
//                        Iterator<SequenceFlow> prevFlowCollection = gateway.getOutgoing().iterator();
                        String postTask = "";
                        for (FlowNode node : nextNodesCollection) {
                            postTask += node.getId();
                            postTask += "-";
                            System.out.println("POST taskkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" + postTask);
                        }
                        newWorkflowTask.setPostTask(postTask);
                    }
                    taskRepository.save(newWorkflowTask);


                }
            }


        }
        return true;
    }

    @Override
    public List<Workflow> getAllWorkflows() {

        return workflowRepository.findAll();
    }


    @Override
    public Workflow getWorkflowById(int id) {
        return workflowRepository.findWorkflowById(id);
    }

    @Override
    public List<String> findSubcriberInTask(int workflowId, String shapeId) {

        List<String> subcribers = new ArrayList<>();
        Task task = taskRepository.findTaskByShapeIdAndWorkflow_Id(shapeId, workflowId);
        String type = task.getType();
        if (type.contains("campaign")) {
            Campaign campaign = campaignRepository.findCampaignById(task.getCampaignAppointment());
            subcribers = campaignSubcriberRepository.findSubcriberMailByCampaignId(campaign.getId());
        } else {
            Appointment appointment = appointmentRepository.findAppointmentById(task.getCampaignAppointment());
            subcribers = appointmentSubcriberRepository.findSubcriberMailByAppointmentId(appointment.getId());
        }
        return subcribers;
    }

    @Override
    public ViewWorkflowDTO viewWorkflowDTO(int workflowId, String shapeId) {
        ViewWorkflowDTO viewWorkflowDTO = new ViewWorkflowDTO();
        List<String> subcriberInTask = new ArrayList<>();
        Task task = taskRepository.findTaskByShapeIdAndWorkflow_Id(shapeId, workflowId);
        String type = task.getType();
        if (type.contains("campaign")) {
            Campaign campaign = campaignRepository.findCampaignById(task.getCampaignAppointment());
            viewWorkflowDTO.setCampaign(campaign);
            subcriberInTask = campaignSubcriberRepository.findSubcriberMailByCampaignId(campaign.getId());
        } else {
            Appointment appointment = appointmentRepository.findAppointmentById(task.getCampaignAppointment());
            viewWorkflowDTO.setAppointment(appointment);
            subcriberInTask = appointmentSubcriberRepository.findSubcriberMailByAppointmentId(appointment.getId());
        }
        viewWorkflowDTO.setSubcriberInTask(subcriberInTask);
        String pretask = taskRepository.findPreTask(workflowId, shapeId);
        if (pretask.contains("User")) {

            System.out.println("GROUP---------------------------------------:");
        } else {
            Task task1 = taskRepository.findTaskByShapeId(pretask);
            List<String> subcriberPreTask = new ArrayList<String>(findSubcriberInTask(workflowId, task1.getShapeId()));
            viewWorkflowDTO.setSubcriersComing(new ArrayList<>(CollectionUtils.disjunction(subcriberInTask, subcriberPreTask)));
        }


        return viewWorkflowDTO;
    }

    @Override
    public List<String> findSubcriberIncoming(int workflowId, String shapeId) {
        List<String> subcriberIncoming = new ArrayList<>();
        List<String> subcriberTask = new ArrayList<String>(findSubcriberInTask(workflowId, shapeId));
        String pretask = taskRepository.findPreTask(workflowId, shapeId);
        Task task = taskRepository.findTaskByPreTask(pretask);
        List<String> subcriberPreTask = new ArrayList<String>(findSubcriberInTask(workflowId, task.getShapeId()));
        subcriberPreTask.retainAll(subcriberTask);
        subcriberTask.removeAll(subcriberPreTask);
        subcriberIncoming = subcriberTask;

        return subcriberIncoming;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    @Override
    public void runWorkflow() {
        System.out.println("RUN WORK FLOW");
//        ExecutorService executor = Executors.newFixedThreadPool(30);
        ExecutorService executor = Executors.newCachedThreadPool();

        List<Workflow> workflows = workflowRepository.findWorkflowByStatus("Starting");
        if (workflows != null) {
            for (Workflow workflow : workflows) {
                System.out.println("-----------------------------------------------------WORK FLOW:" + workflow.getName());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //get all subcriber
                        for (WorkflowGroupContact workflowGroupContact : workflow.getWorkflowGroupContacts()) {
                            System.out.println("------------------------------------------WOrkflow group contact----------------------");
                            List<Subcriber> subcribers = groupContactRepository
                                    .findSubcriberByGroupContactId(workflowGroupContact.getGroupContact().getId());
                            Set<Subcriber> checkDuplicates = new HashSet<Subcriber>();

                            for (Subcriber subcriber : subcribers) {
                                if (!checkDuplicates.add(subcriber)) {
                                    System.out.println("Duplicate in that list " + subcriber);
                                }
                                System.out.println("-----------------------------------------------------SUBCRIBER:" + subcriber.getEmail());
//                                List<Task> tasks = workflow.getTasks();
                                Task firstTask = taskRepository.findTaskByPreTaskAndWorkflow_Id("UserTask_1sbexqx", workflow.getId());
                                System.out.println("-------------------------first Task Type" + workflow.getId());
                                System.out.println("-------------------------first Task Type" + firstTask.getName());
                                if (firstTask.getType().equalsIgnoreCase("appointment")) {
                                    Appointment firstApp = appointmentRepository.findAppointmentById(firstTask.getCampaignAppointment());
                                    System.out.println("First APP -----------------" + firstApp.getName());
                                    if (appointmentSubcriberRepository.checkConfirmAppointment(firstApp.getId(), subcriber.getEmail()) != null) {
                                        if (appointmentSubcriberRepository.checkSend(firstApp.getId(), subcriber.getEmail()) == true) {
                                            runTask(firstTask, workflow, subcriber);
                                        } else {
                                            System.out.println("-----------------------------------------------------SENDING:");
                                            AppointmentSubcriber appointmentSubcriber = appointmentSubcriberRepository.changeConfirmSend(firstApp.getId(), subcriber.getEmail());
                                            appointmentSubcriber.setSend(true);
                                            appointmentSubcriberRepository.save(appointmentSubcriber);
                                            addContentApppointment(firstApp, subcriber);
                                        }
                                    } else {
                                        System.out.println("TAO MOI APPOINTMENT");
                                        AppointmentSubcriber newAppSub = new AppointmentSubcriber();
                                        AppointmentGroupContact agc = firstApp.getAppointmentGroupContacts().get(0);
                                        newAppSub.setAppointmentGroupContact(agc);
                                        newAppSub.setSubcriberEmail(subcriber.getEmail());
                                        newAppSub.setSend(false);
                                        newAppSub.setConfirmation(false);
                                        appointmentSubcriberRepository.save(newAppSub);
                                    }

                                } else if (firstTask.getType().equalsIgnoreCase(("campaign"))) {
                                    Campaign firstApp = campaignRepository.findCampaignById(firstTask.getCampaignAppointment());

                                    System.out.println("First CAMPAIGN -----------------" + firstApp.getName() + subcriber.getEmail());
                                    if (campaignSubcriberRepository.checkConfirmCampaign(firstApp.getId(), subcriber.getEmail()) != null) {
                                        System.out.println("SUBCRIBER ------------------------" + campaignSubcriberRepository.checkSend(firstApp.getId(), subcriber.getEmail()));
                                        if (campaignSubcriberRepository.checkSend(firstApp.getId(), subcriber.getEmail()) == true &&
                                                campaignSubcriberRepository.changeConfirmSend(firstApp.getId(), subcriber.getEmail()).getMessageId() != "") {
                                            System.out.println("Gửi rồi nha ");
                                            runTask(firstTask, workflow, subcriber);
                                        } else if (campaignSubcriberRepository.checkSend(firstApp.getId(), subcriber.getEmail()) != true) {
//                                            System.out.println("-----------------------------------------------------SENDING:");

                                            CampaignSubcriber campaignSubcriber = campaignSubcriberRepository.changeConfirmSend(firstApp.getId(), subcriber.getEmail());
                                            System.out.println("CAMPAINT SUBCRIBER IS SEND: " + campaignSubcriber.isSend());
                                            campaignSubcriber.setSend(true);
                                            campaignSubcriberRepository.save(campaignSubcriber);
//                                            mailService.sendSimpleMessageV2(firstApp.getSender(), firstApp.getFromMail(), subcriber.getEmail(), firstApp.getSubject(), firstApp.getContent());
                                            sendCampaignWorkflow(firstApp, subcriber);
                                        }
                                    } else {
                                        System.out.println("DANG O DAY NE");
                                        CampaignSubcriber newCampaignSub = new CampaignSubcriber();
                                        CampaignGroupContact campaignGroupContact = firstApp.getCampaignGroupContacts().get(0);
                                        newCampaignSub.setCampaignGroupContact(campaignGroupContact);
                                        newCampaignSub.setSubcriberEmail(subcriber.getEmail());
                                        newCampaignSub.setSend(false);
                                        newCampaignSub.setComfirmation(false);
                                        campaignSubcriberRepository.save(newCampaignSub);
                                    }

                                }

                            }
                        }
                    }
                });
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseWorkflow(int id) {
        Workflow workflow = workflowRepository.findWorkflowById(id);
        workflow.setStatus("Pause");
        workflowRepository.save(workflow);
    }

    @Override
    public void restartWorkflow(int id) {
        Workflow workflow = workflowRepository.findWorkflowById(id);
        workflow.setStatus("Starting");
        workflowRepository.save(workflow);
    }

    public void runTask(Task firstTask, Workflow workflow, Subcriber subcriber) {

        int INTERVAL = 2;
        //clicked yes : 1
//        clicked no : 0
//                opened yes : 2
//                opened no : 3
        System.out.println("RUN TASK NE ---------------------------------------------------------" + firstTask.getName() + firstTask.getType());
        int clicked = 0;
        int open = 0;
        ;
        if (firstTask.getType().equalsIgnoreCase("appointment")) {
            Appointment firstAppointment = appointmentRepository.findAppointmentById(firstTask.getCampaignAppointment());
            AppointmentSubcriber appointmentSubcriberCheck = appointmentSubcriberRepository.changeConfirmSend(firstAppointment.getId(), subcriber.getEmail());
            System.out.println("CHECK---------------------------------" + appointmentSubcriberRepository.checkConfirmAppointment(firstAppointment.getId(), subcriber.getEmail()));
            if (appointmentSubcriberRepository.checkConfirmAppointment(firstAppointment.getId(), subcriber.getEmail()) == true) {
                clicked = 1;
            } else {
                clicked = 0;
            }
            if (appointmentSubcriberRepository.checkSend(firstAppointment.getId(), subcriber.getEmail()) == true) {
                List<String> postsCode = new ArrayList<>();
                try {
                    postsCode = Arrays.asList(firstTask.getPostTask().split("-"));
                } catch (Exception e) {

                }

                if (postsCode == null || postsCode.isEmpty()) {
                    return;
                } else {
                    for (int i = 0; i < postsCode.size(); i++) {
                        System.out.println("POST CODE--------------------------------------------------" + postsCode.get(i) + "CLicked" + clicked);
                        Task tmp = taskRepository.findTaskByShapeIdAndWorkflow_Id(postsCode.get(i), workflow.getId());
                        System.out.println("GATE WAY-------------------------------" + tmp.getGateway());

                        if ((tmp.getGateway().equalsIgnoreCase("Clicked ?yes") && clicked == 1)
                                || (tmp.getGateway().equalsIgnoreCase("Clicked ?no") && clicked == 0
                                || (tmp.getGateway().equalsIgnoreCase("Opened ?yes") && open == 1)
                                || (tmp.getGateway().equalsIgnoreCase("Opened ?no") && open == 0))
                        ) {
                            if (tmp.getType().equalsIgnoreCase("appointment")) {
                                System.out.println("--------------------------------------------------------Clicked ?No");
                                Appointment tmpAppointment = appointmentRepository.findAppointmentById(tmp.getCampaignAppointment());
                                AppointmentSubcriber appointmentSubcriber = appointmentSubcriberRepository.changeConfirmSend(tmpAppointment.getId(), subcriber.getEmail());
                                if (!appointmentSubcriber.isSend()
                                        && concompareTwoTimes(appointmentSubcriberCheck.getCreatedTime(), INTERVAL)
                                ) {

                                    appointmentSubcriber.setSend(true);
                                    appointmentSubcriberRepository.save(appointmentSubcriber);
//                                    mailService.sendAppointment(tmpAppointment.getSender(), tmpAppointment.getFromMail(), subcriber.getEmail(), tmpAppointment.getSubject(), tmpAppointment.getBody());
                                    addContentApppointment(tmpAppointment, subcriber);
//                                    break;
                                }
                                runTask(tmp, workflow, subcriber);
                            } else {
                                // CAMPAIGN
                                Campaign tmpCampaign = campaignRepository.findCampaignById(tmp.getCampaignAppointment());
                                CampaignSubcriber campaignSubcriber = campaignSubcriberRepository.changeConfirmSend(tmpCampaign.getId(), subcriber.getEmail());
                                if (campaignSubcriberRepository.checkConfirmCampaign(tmpCampaign.getId(), subcriber.getEmail()) != null) {
                                    System.out.println("--------------------------------------------------------Clicked ?No");

                                    System.out.println("------ISSEND" + subcriber.getEmail() + tmpCampaign.getName());
                                    System.out.println("-----------ISSEND" + campaignSubcriber.isSend());
                                    if (!campaignSubcriber.isSend()
                                            && concompareTwoTimes(appointmentSubcriberCheck.getCreatedTime(), INTERVAL)
                                    ) {
                                        campaignSubcriber.setSend(true);
                                        campaignSubcriberRepository.save(campaignSubcriber);
//                                        mailService.sendAppointment(tmpCampaign.getSender(), tmpCampaign.getFromMail(), subcriber.getEmail(), tmpCampaign.getSubject(), tmpCampaign.getContent());
                                        sendCampaignWorkflow(tmpCampaign, subcriber);
//                                        break;
                                    }
                                    runTask(tmp, workflow, subcriber);
                                } else {
                                    System.out.println("DANG O DAY NE");
                                    CampaignSubcriber newCampaignSub = new CampaignSubcriber();
                                    CampaignGroupContact cgc = tmpCampaign.getCampaignGroupContacts().get(0);
                                    newCampaignSub.setCampaignGroupContact(cgc);
                                    newCampaignSub.setSubcriberEmail(subcriber.getEmail());
                                    newCampaignSub.setSend(false);
                                    newCampaignSub.setComfirmation(false);
                                    campaignSubcriberRepository.save(newCampaignSub);
                                }

                            }
                        } else {
                            runTask(tmp, workflow, subcriber);
                            return;
                        }

                    }//array of post code
                }


            } else {
                System.out.println("CHUA SEND NE --------------------------------------");

                firstAppointment = appointmentRepository.findAppointmentById(firstTask.getCampaignAppointment());
                AppointmentSubcriber appointmentSubcriber1 = appointmentSubcriberRepository.changeConfirmSend(firstAppointment.getId(), subcriber.getEmail());
                if (appointmentSubcriber1.isSend() == false) {
                    appointmentSubcriber1.setSend(true);
                    appointmentSubcriberRepository.save(appointmentSubcriber1);
//                    mailService.sendAppointment(firstAppointment.getSender(), firstAppointment.getFromMail(), subcriber.getEmail(), firstAppointment.getSubject(), firstAppointment.getBody());
                    addContentApppointment(firstAppointment, subcriber);
                }
//                mailService.sendAppointment(firstAppointment.getSender(),firstAppointment.getFromMail(),subcriber.getEmail(),firstAppointment.getSubject(),firstAppointment.getBody());
            }
            ///IF CAMPAIGN
        } else if (firstTask.getType().equalsIgnoreCase("campaign")) {
            Campaign firstCampaign = campaignRepository.findCampaignById(firstTask.getCampaignAppointment());
//            System.out.println("CHECK---------------------------------" +appointmentSubcriberRepository.checkConfirmAppointment(firstCampaign.getId(), subcriber.getEmail()).toString());
            CampaignSubcriber campaignSubcriberCheck = campaignSubcriberRepository.changeConfirmSend(firstCampaign.getId(), subcriber.getEmail());
            String timeSend = campaignSubcriberCheck.getCreatedTime();

            if (campaignSubcriberRepository.checkConfirmCampaign(firstCampaign.getId(), subcriber.getEmail()) == true) {
                clicked = 1;
            } else {
                clicked = 0;
            }
            if (campaignSubcriberRepository.checkOpen(firstCampaign.getId(), subcriber.getEmail()) == true) {
                open = 1;
            } else {
                open = 0;
            }
            if (campaignSubcriberRepository.checkSend(firstCampaign.getId(), subcriber.getEmail()) == true && campaignSubcriberCheck.getMessageId() != "" && campaignSubcriberCheck.getMessageId() != null) {
                List<String> postsCode = new ArrayList<>();
                try {
                    postsCode = Arrays.asList(firstTask.getPostTask().split("-"));
                } catch (Exception e) {

                }

                if (postsCode == null || postsCode.isEmpty()) {
                    return;
                } else {
                    for (int i = 0; i < postsCode.size(); i++) {
                        System.out.println("POST CODE--------------------------------------------------" + postsCode.get(i) + "c" + timeSend);
                        Task tmp = taskRepository.findTaskByShapeIdAndWorkflow_Id(postsCode.get(i), workflow.getId());

                        if ((tmp.getGateway().equalsIgnoreCase("Clicked ?yes") && clicked == 1) || (tmp.getGateway().equalsIgnoreCase("Clicked ?no") && clicked == 0)
                                || (tmp.getGateway().equalsIgnoreCase("Opened ?yes") && open == 1) || (tmp.getGateway().equalsIgnoreCase("Opened ?no") && open == 0)) {
                            if (tmp.getType().equalsIgnoreCase("appointment")) {
                                System.out.println("--------------------------------------------------------Clicked ?No");
                                Appointment tmpAppointment = appointmentRepository.findAppointmentById(tmp.getCampaignAppointment());
                                AppointmentSubcriber appointmentSubcriber = appointmentSubcriberRepository.changeConfirmSend(tmpAppointment.getId(), subcriber.getEmail());
                                if(appointmentSubcriber != null){
                                    if (!appointmentSubcriber.isSend()
                                            && concompareTwoTimes(timeSend, INTERVAL) == true
                                    ) {

                                        appointmentSubcriber.setSend(true);
                                        appointmentSubcriberRepository.save(appointmentSubcriber);
//                                    mailService.sendAppointment(tmpAppointment.getSender(), tmpAppointment.getFromMail(), subcriber.getEmail(), tmpAppointment.getSubject(), tmpAppointment.getBody());
                                        addContentApppointment(tmpAppointment, subcriber);
//                                    break;
                                    }else if(appointmentSubcriber.isSend() && ! appointmentSubcriber.getMessageId().equalsIgnoreCase("")){
                                        runTask(tmp, workflow, subcriber);
                                    }
                                }
                                else {
                                    System.out.println("DANG O DAY NE");
                                    AppointmentSubcriber newAppSub = new AppointmentSubcriber();
                                    AppointmentGroupContact cgc = tmpAppointment.getAppointmentGroupContacts().get(0);
                                    newAppSub.setAppointmentGroupContact(cgc);
                                    newAppSub.setSubcriberEmail(subcriber.getEmail());
                                    newAppSub.setSend(false);
                                    newAppSub.setConfirmation(false);
                                    appointmentSubcriberRepository.save(newAppSub);
                                }


                            } else if (tmp.getType().equalsIgnoreCase("campaign")) {
                                // CAMPAIGN
                                Campaign tmpCampaign = campaignRepository.findCampaignById(tmp.getCampaignAppointment());
                                CampaignSubcriber campaignSubcriber = campaignSubcriberRepository.changeConfirmSend(tmpCampaign.getId(), subcriber.getEmail());
//                                campaignSubcriber.getCreatedTime();
                                if (campaignSubcriberRepository.checkConfirmCampaign(tmpCampaign.getId(), subcriber.getEmail()) != null) {
                                    System.out.println("--------------------------------------------------------Clicked ?No");

                                    System.out.println("------ISSEND" + subcriber.getEmail() + tmpCampaign.getName());
                                    System.out.println("-----------ISSEND" + campaignSubcriber.isSend() + concompareTwoTimes(timeSend, INTERVAL));
                                    if (!campaignSubcriber.isSend() && campaignSubcriber.getMessageId() != ""
                                            && concompareTwoTimes(timeSend, INTERVAL) && timeSend != "" && timeSend != null
                                    ) {
                                        System.out.println("IM HERE");
                                        campaignSubcriber.setSend(true);
                                        campaignSubcriberRepository.save(campaignSubcriber);
//                                        mailService.sendAppointment(tmpCampaign.getSender(), tmpCampaign.getFromMail(), subcriber.getEmail(), tmpCampaign.getSubject(), tmpCampaign.getContent());
                                        sendCampaignWorkflow(tmpCampaign, subcriber);
                                    } else if (campaignSubcriber.isSend() && campaignSubcriber.getMessageId().equalsIgnoreCase("")) {
                                        continue;
                                    } else if (campaignSubcriber.isSend() && !campaignSubcriber.getMessageId().equalsIgnoreCase("")) {
                                        runTask(tmp, workflow, subcriber);
                                    }
                                } else {
                                    System.out.println("DANG O DAY NE");
                                    CampaignSubcriber newCampaignSub = new CampaignSubcriber();
                                    CampaignGroupContact cgc = tmpCampaign.getCampaignGroupContacts().get(0);
                                    newCampaignSub.setCampaignGroupContact(cgc);
                                    newCampaignSub.setSubcriberEmail(subcriber.getEmail());
                                    newCampaignSub.setSend(false);
                                    newCampaignSub.setComfirmation(false);
                                    campaignSubcriberRepository.save(newCampaignSub);
                                }

                            }
                        }

                    }//array of post code
                }


            } else if (campaignSubcriberRepository.changeConfirmSend(firstCampaign.getId(), subcriber.getEmail()).getMessageId() != "" &&
                    !campaignSubcriberRepository.changeConfirmSend(firstCampaign.getId(), subcriber.getEmail()).isSend()) {
                System.out.println("CHUA SEND NE --------------------------------------");

                firstCampaign = campaignRepository.findCampaignById(firstTask.getCampaignAppointment());
                CampaignSubcriber campaignSubcriber1 = campaignSubcriberRepository.changeConfirmSend(firstCampaign.getId(), subcriber.getEmail());
                if (campaignSubcriber1.isSend() == false) {
                    campaignSubcriber1.setSend(true);

                    campaignSubcriberRepository.save(campaignSubcriber1);
//                    mailService.sendAppointment(firstCampaign.getSender(), firstCampaign.getFromMail(), subcriber.getEmail(), firstCampaign.getSubject(), firstCampaign.getContent());
                    sendCampaignWorkflow(firstCampaign, subcriber);
                }
//                mailService.sendAppointment(firstAppointment.getSender(),firstAppointment.getFromMail(),subcriber.getEmail(),firstAppointment.getSubject(),firstAppointment.getBody());
            }
        }
    }

    public void sendCampaignWorkflow(Campaign campaign, Subcriber subcriber) {

        String content = campaign.getContent();
        try {
            content = content.replace("{{email}}", subcriber.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            content = content.replace("{{last_name}}", subcriber.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            content = content.replace("{{first_name}}", subcriber.getFirstName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CampaignSubcriber campaignSubcriber = campaignSubcriberRepository.changeConfirmSend(campaign.getId(), subcriber.getEmail());
        campaignSubcriber.setSend(true);
        campaignSubcriberRepository.save(campaignSubcriber);
        campaign.setStatus("Done");
        campaignRepository.save(campaign);
        String messageId = "";
        messageId = mailService.sendSimpleMessageV2(campaign.getSender(), campaign.getFromMail(), subcriber.getEmail(), campaign.getSubject(), content);
        campaignSubcriber.setMessageId(messageId.trim());
        campaignSubcriberRepository.save(campaignSubcriber);

    }

    public void addContentApppointment(Appointment appointment, Subcriber subcriber) {
        try {

            String content = appointment.getBody();
            try {
                content = content.replace("{{email}}", subcriber.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                content = content.replace("{{last_name}}", subcriber.getLastName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                content = content.replace("{{first_name}}", subcriber.getFirstName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                content = content.replace("{{date}}", appointment.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index = content.indexOf("<a href=\"\"") + 8;
            String newString = new String();
            for (int i = 0; i < content.length(); i++) {

                newString += content.charAt(i);
                if (i == index) {
                    newString += "http://localhost:8080/api/accept-appointment?confirmationToken=" + appointment.getToken() + "&subcriberEmail=" + subcriber.getEmail();
                }
            }
            String messageId = "";
            messageId = mailService.sendAppointment(appointment.getSender(),
                    appointment.getFromMail(),
                    subcriber.getEmail()
                    , appointment.getSubject(),
                    newString);
            AppointmentSubcriber appointmentSubcriber = appointmentSubcriberRepository.changeConfirmSend(appointment.getId(), subcriber.getEmail());
            appointmentSubcriber.setMessageId(messageId.trim());
            appointmentSubcriberRepository.save(appointmentSubcriber);
        } catch (MailException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public boolean concompareTwoTimes(String timeSend, int interval) {


        if (timeSend == "" || timeSend.isEmpty()) {
            return false;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime formatTimeSend = LocalDateTime.parse(timeSend, formatter);
            if (formatTimeSend.plusMinutes(interval).isBefore(now)) {
                return true;
            }
        }

        return false;


    }

}
