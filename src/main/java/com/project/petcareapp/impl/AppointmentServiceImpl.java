package com.project.petcareapp.impl;

import camundajar.com.google.gson.Gson;
import com.project.petcareapp.dto.*;
import com.project.petcareapp.model.*;
import com.project.petcareapp.repository.*;
import com.project.petcareapp.service.AppointmentService;
import com.project.petcareapp.service.MailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    public static final int NUM_OF_THREAD = 10;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final Configuration templates;

    @Autowired
    MyMessageRepository myMessageRepository;

    @Autowired
    TemplateEngine htmlTemplateEngine;

    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppointmentSubcriberRepository appointmentSubcriberRepository;

    @Autowired
    CampaignSubcriberRepository campaignSubcriberRepository;

    @Autowired
    GroupContactRepository groupContactRepository;

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    SubcriberRepository subcriberRepository;
    @Autowired
    MailService mailService;

    public AppointmentServiceImpl(Configuration templates) {
        this.templates = templates;
    }


    @Override
    public Appointment findByName(String token) {
        return null;
    }

    @Override
    public Appointment findByToken(String token) {
        return null;
    }

    @Override
    public boolean createAppointment(MailObjectDTO mailObjectDTO, AppointmentDTO appointmentDTO, Account account, List<SegmentDTO> segmentDTOs, String condition) {
        System.out.println(appointmentDTO.getName());
        Appointment checkExistedAppointment = appointmentRepository.findByName(appointmentDTO.getName());
        if (checkExistedAppointment != null) {
            return false;
        }
        Appointment appointment = new Appointment();
        //Mail Object
        appointment.setBody(mailObjectDTO.getBody());
        appointment.setBodyJson(mailObjectDTO.getBodyJson());
        appointment.setFromMail(mailObjectDTO.getFromMail());
        appointment.setSender(mailObjectDTO.getFrom());
        appointment.setSubject(mailObjectDTO.getSubject());
        //Appointment Info
        appointment.setCreatedTime(LocalDateTime.now().toString());
        appointment.setName(appointmentDTO.getName());
        appointment.setStatus(appointmentDTO.getStatus());
        appointment.setTime(appointmentDTO.getTime());
        appointment.setAutomation(false);


        //Add to Group Contacts
//        Account account = accountRepository.findAccountById(3);
        appointment.setAccount_id(account.getId());
//        appointment.setTo("tannm@unicode.edu.vn");
//        String[] strArray = new String[] {appointment.getTo()};
        List<String> mailLists = new ArrayList<>();
        List<AppointmentGroupContact> appointmentGroupContacts = appointmentDTO.getGcAppointmentDTOS().stream().map(g -> {
            AppointmentGroupContact appointmentGroupContact = new AppointmentGroupContact();
            appointmentGroupContact.setGroupContact(groupContactRepository.findGroupById(g.getGroupContactId()));
            appointmentGroupContact.setAppointment(appointment);
            appointmentGroupContact.setCreatedTime(LocalDateTime.now().toString());
            System.out.println("Tới đây 1");
            String[] mailList = groupContactRepository.findSubcriberMailByGroupContactId(appointmentGroupContact.getGroupContact().getId());
            //Add Subcriber To Appointments
            List<AppointmentSubcriber> appointmentSubcribers = new ArrayList<>();
            for (int i = 0; i < mailList.length; i++) {
                if (segmentDTOs.size() == 0 || segmentDTOs.isEmpty() || segmentDTOs == null) {
                    mailLists.add(mailList[i]);
                    AppointmentSubcriber appointmentSubcriber = new AppointmentSubcriber();
                    appointmentSubcriber.setConfirmation(false);
                    appointmentSubcriber.setCreatedTime("");
                    appointmentSubcriber.setAppointmentGroupContact(appointmentGroupContact);
                    appointmentSubcriber.setSend(false);
                    appointmentSubcriber.setOpened(false);
                    appointmentSubcriber.setSubcriberEmail(mailList[i]);
                    appointmentSubcribers.add(appointmentSubcriber);
                }else {
                    String mailString = mailList[i];
                    List<Subcriber> subcribers = new ArrayList(new LinkedHashSet());

                    for (SegmentDTO segmentDTO : segmentDTOs) {
                        List<Subcriber> subcriberList = new ArrayList<>();
                        if (segmentDTO.getSelect1().equalsIgnoreCase("Contact Details")) {
                            //Name
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Name")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is")) {
                                    subcriberList = subcriberRepository.findAllByLastNameIs(segmentDTO.getSelect4());

                                } else if (segmentDTO.getSelect3().equalsIgnoreCase("is not")) {
                                    subcriberList = subcriberRepository.findAllByLastNameIsNot(segmentDTO.getSelect4());
                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("contains")) {
                                    subcriberList = subcriberRepository.findAllByLastNameContains(segmentDTO.getSelect4());
                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("doesn't contain")) {
                                    subcriberList = subcriberRepository.findAllByLastNameNotLike(segmentDTO.getSelect4());
                                }
                            }
                            //Email
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Email")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is")) {
                                    subcriberList = subcriberRepository.findAllByEmailIs(segmentDTO.getSelect4());
                                } else if (segmentDTO.getSelect3().equalsIgnoreCase("is not")) {
                                    subcriberList = subcriberRepository.findAllByEmailIsNot(segmentDTO.getSelect4());
                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("contains")) {
                                    subcriberList = subcriberRepository.findAllByEmailContains(segmentDTO.getSelect4());
                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("doesn't contain")) {
                                    subcriberList = subcriberRepository.findAllByEmailNotLike(segmentDTO.getSelect4());
                                }
                            }
                            //Birthday
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Birthday")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is before")) {
                                    subcriberList = subcriberRepository.findAllByDobBefore(segmentDTO.getSelect4());

                                } else if (segmentDTO.getSelect3().equalsIgnoreCase("is after")) {
                                    subcriberList = subcriberRepository.findAllByDobAfter(segmentDTO.getSelect4());
                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is on")) {
                                    subcriberList = subcriberRepository.findAllByDob(segmentDTO.getSelect4());
                                }
                            }
                            //Address
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Address")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("contains")) {
                                    subcriberList = subcriberRepository.findAllByAddressContains(segmentDTO.getSelect4());
                                }
                            }
                            //Create Time
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Subscription date")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is before")) {
                                    subcriberList = subcriberRepository.findAllByCreatedTimeBefore(segmentDTO.getSelect4());

                                } else if (segmentDTO.getSelect3().equalsIgnoreCase("is after")) {
                                    subcriberList = subcriberRepository.findAllByCreatedTimeAfter(segmentDTO.getSelect4());

                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is on")) {
                                    String dateTime = segmentDTO.getSelect4();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    LocalDate dateTimeFormated = LocalDate.parse(dateTime,formatter);
                                    subcriberList = subcriberRepository.findAllByCreatedTimeContains(dateTimeFormated.toString());

                                }
                            }
                            //Engagement Score
                            if (segmentDTO.getSelect2().equalsIgnoreCase("Engagement Score")) {
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is equal to")) {
                                    subcriberList = subcriberRepository.findAllByTypeContains(segmentDTO.getSelect4());


                                }
                                if (segmentDTO.getSelect3().equalsIgnoreCase("is not equal to")) {
                                    subcriberList = subcriberRepository.findSubcriberByTypeIsNot(segmentDTO.getSelect4().trim());

                                }
                            }

                        } else {
                            //Mail not Opened
                            if (segmentDTO.getSelect1().equalsIgnoreCase("Contact Actions")) {
                                //Mail Not Opened
                                if (segmentDTO.getSelect2().equalsIgnoreCase("Mail not opened")) {
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("campaign")) {
                                        List<Subcriber> subcriberMails = campaignSubcriberRepository.findSubcriberByCampaignAndOpened(Integer.valueOf(segmentDTO.getSelect4()), false);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                          Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("appointment")) {
                                        List<Subcriber> subcriberMails = appointmentSubcriberRepository.findSubcriberByAppointmentAndOpened(Integer.valueOf(segmentDTO.getSelect4()), false);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                }

                                //Mail Opened
                                if (segmentDTO.getSelect2().equalsIgnoreCase("Mail opened")) {
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("campaign")) {
                                        List<Subcriber> subcriberMails = campaignSubcriberRepository.findSubcriberByCampaignAndOpened(Integer.valueOf(segmentDTO.getSelect4()), true);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("appointment")) {
                                        List<Subcriber> subcriberMails = appointmentSubcriberRepository.findSubcriberByAppointmentAndOpened(Integer.valueOf(segmentDTO.getSelect4()), true);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                }
                                //Mail Clicked
                                if (segmentDTO.getSelect2().equalsIgnoreCase("Mail clicked")) {
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("Campaign")) {
                                        List<Subcriber> subcriberMails = campaignSubcriberRepository.findSubcriberByCampaignAndClicked(Integer.valueOf(segmentDTO.getSelect4()), true);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("appointment")) {
                                        List<Subcriber> subcriberMails = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndClicked(Integer.valueOf(segmentDTO.getSelect4()), true);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                }

                                //Mail not clicked
                                if (segmentDTO.getSelect2().equalsIgnoreCase("Mail not clicked")) {
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("Campaign")) {
                                        List<Subcriber> subcriberMails = campaignSubcriberRepository.findSubcriberByCampaignAndClicked(Integer.valueOf(segmentDTO.getSelect4()), false);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                    if (segmentDTO.getSelect3().equalsIgnoreCase("appointment")) {
                                        List<Subcriber> subcriberMails = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndClicked(Integer.valueOf(segmentDTO.getSelect4()), false);
                                        for (Subcriber subcriberMail : subcriberMails) {
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
                                            subcriberList.add(subcriberMail);

                                        }

                                    }
                                }
                            }
                        }
                        if (condition.equalsIgnoreCase("or")) {
                            subcribers.addAll(subcriberList);
                        }
                        if (condition.equalsIgnoreCase("and")) {
                            if (subcribers.isEmpty()) {
                                subcribers.addAll(subcriberList);
                            }
                            subcribers.retainAll(subcriberList);
                        }


                    }
                    Optional<Subcriber> result = subcribers.stream().filter(element -> element.getEmail().contains(mailString)).findAny();
                    if (result.isPresent()) {
                        mailLists.add(mailList[i]);
                        AppointmentSubcriber appointmentSubcriber = new AppointmentSubcriber();
                        appointmentSubcriber.setConfirmation(false);
                        appointmentSubcriber.setCreatedTime("");
                        appointmentSubcriber.setAppointmentGroupContact(appointmentGroupContact);
                        appointmentSubcriber.setSubcriberEmail(mailList[i]);
                        appointmentSubcribers.add(appointmentSubcriber);
                        appointmentSubcriber.setOpened(false);
                        appointmentSubcriber.setSend(false);
                    }


                }

            }

            appointmentGroupContact.setAppointment(appointment);
            appointmentGroupContact.setAppointmentSubcribers(appointmentSubcribers);

            return appointmentGroupContact;
        }).collect(Collectors.toList());

        appointment.setAppointmentGroupContacts(appointmentGroupContacts);
//
        appointment.setToken(UUID.randomUUID().toString());
        appointmentDTO.setToken(appointment.getToken());
        String segmentString = new Gson().toJson(segmentDTOs);
        appointment.setSegment(segmentString);
        appointment.setConditionsegment(condition);
        appointmentRepository.save(appointment);

        try {

//            String bodyTemp = appointment.getBody();
//            int index = bodyTemp.indexOf("<a href=\"\"") + 8;
//            System.out.println(index);

            for (int counter = 0; counter < mailLists.size(); counter++) {
                String bodyTemp = appointment.getBody();
                int index = bodyTemp.indexOf("<a href=\"\"") + 8;
                String newString = new String();
                for (int i = 0; i < bodyTemp.length(); i++) {

                    newString += bodyTemp.charAt(i);
                    if (i == index) {
                        newString += "http://localhost:8080/api/accept-appointment?confirmationToken=" + appointment.getToken() + "&subcriberEmail=" + mailLists.get(counter);
                    }
                }
                try {
                    newString = newString.replace("{{email}}", mailLists.get(counter));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    newString = newString.replace("{{reject}}", "http://localhost:8080/api/deny-appointment?confirmationToken=" + appointment.getToken() + "&subcriberEmail=" + mailLists.get(counter));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Subcriber personalization = subcriberRepository.findSubcriberByEmail(mailLists.get(counter));
                    newString = newString.replace("{{last_name}}", personalization.getLastName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Subcriber personalization = subcriberRepository.findSubcriberByEmail(mailLists.get(counter));
                    newString = newString.replace("{{first_name}}", personalization.getFirstName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    newString = newString.replace("{{date}}", appointment.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String messageId = mailService.sendAppointment(appointment.getSender(),
                        appointment.getFromMail(),
                        mailLists.get(counter), appointment.getSubject(),
                        newString);
                AppointmentSubcriber appointmentSubcriber = appointmentSubcriberRepository.changeConfirmSend(appointment.getId(), mailLists.get(counter));
                appointmentSubcriber.setSend(true);
                appointmentSubcriber.setConfirmation(false);
                appointmentSubcriber.setDelivery(false);
                appointmentSubcriber.setOpened(false);
                appointmentSubcriber.setMessageId(messageId.trim());
                appointmentSubcriberRepository.save(appointmentSubcriber);


            }


        } catch (MailException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }


        return true;
    }

    @Override
    public void sendAppointment(int appointmentId) {

    }

    @Override
    public Appointment addContentToAppointment(Appointment appointment) {
        Appointment appointmentEdit = appointmentRepository.findAppointmentById(appointment.getId());
        if (appointmentEdit == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "This appointment is not exist!");
        }

        appointmentEdit.setBodyJson(appointment.getBodyJson());
        appointmentEdit.setBody(appointment.getBody());

        appointmentEdit.setUpdatedTime(LocalDateTime.now().toString());
        return appointmentRepository.save(appointmentEdit);
    }

    @Override
    public boolean editAppointment(MailObjectDTO mailObjectDTO, AppointmentDTO appointmentDTO, int id) {
        return false;
    }

    @Override
    public AppointmentFullDTO getAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findAppointmentById(id);
        // Get Statistic of Campaign
        AppointmentFullDTO appointmentDTO = new AppointmentFullDTO();

        appointmentDTO.setName(appointment.getName());
        appointmentDTO.setTime(appointment.getTime());
        appointmentDTO.setStatus(appointment.getStatus());
        appointmentDTO.setBody(appointment.getBody());
        appointmentDTO.setFrom(appointment.getSender());
        appointmentDTO.setSubject(appointment.getSubject());
        appointmentDTO.setFrom(appointment.getSender());
        appointmentDTO.setCreatedTime(appointment.getCreatedTime());
        appointmentDTO.setUpdatedTime(LocalDateTime.now().toString());
        appointmentDTO.setFromMail(appointment.getFromMail());
        appointmentDTO.setBodyJson(appointment.getBodyJson());
        appointmentDTO.setConditon(appointment.getConditionsegment());
        appointmentDTO.setSegment(appointment.getSegment());

        List<GCAppointmentDTO> gcAppointmentDTOS = appointment.getAppointmentGroupContacts().stream().map(g -> {
            GCAppointmentDTO gcAppointmentDTO = new GCAppointmentDTO();
            gcAppointmentDTO.setGroupContactId(g.getGroupContact().getId());

            return gcAppointmentDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setGcAppointmentDTOS(gcAppointmentDTOS);
        //Statistic
        appointmentDTO.setRequest(appointment.getRequest());
        appointmentDTO.setOpen(appointment.getOpenRate());
        appointmentDTO.setBounce(appointment.getBounce());
        appointmentDTO.setDelivery(appointment.getDelivery());
        appointmentDTO.setClick(appointment.getClickRate());
        appointmentDTO.setSpam(appointment.getSpamRate());
        //Contact Request
        List<Subcriber> contactRequest = appointmentSubcriberRepository.findSubcriberByAppointment(appointment.getId());
        List<SubcriberViewDTO> contactRequestDto = contactRequest.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactRequest(contactRequestDto);
        //Contact Delivery
        List<Subcriber> contactDelivery = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndDelivery(appointment.getId(), true);
        List<SubcriberViewDTO> contactDeliveryDto = contactDelivery.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactDelivery(contactDeliveryDto);
        //Contact Opened
        List<Subcriber> contactOpened = appointmentSubcriberRepository.findSubcriberByAppointmentAndOpened(appointment.getId(), true);
        List<SubcriberViewDTO> contactOpenDto = contactOpened.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactOpened(contactOpenDto);
        //Contact Bounce
        List<Subcriber> contactBounce = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndBounce(appointment.getId(), true);
        List<SubcriberViewDTO> contactBounceDTO = contactBounce.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactBounce(contactBounceDTO);
        //Contact Clicked
        List<Subcriber> contactClick = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndClicked(appointment.getId(), true);
        List<SubcriberViewDTO> contactClickDTO = contactClick.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactClicked(contactClickDTO);
        //Contact Spam
        List<Subcriber> contactSpam = appointmentSubcriberRepository.findSubcriberMailByAppointmentAndSpam(appointment.getId(), true);
        List<SubcriberViewDTO> contactSpamDTO = contactSpam.stream().map(g -> {
            SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
            subcriberViewDTO.setFirstName(g.getFirstName());
            subcriberViewDTO.setLastName(g.getLastName());
            subcriberViewDTO.setId(g.getId());
            subcriberViewDTO.setEmail(g.getEmail());
            subcriberViewDTO.setType(g.getType());
            return subcriberViewDTO;
        }).collect(Collectors.toList());
        appointmentDTO.setContactSpam(contactSpamDTO);
        return appointmentDTO;
    }

    @Override
    public ResponseEntity<String> acceptAppointment(String token, String email) {
        Appointment appointment = appointmentRepository.findByToken(token);
        AppointmentSubcriber appointmentSubcriber = appointmentRepository.findMailByAppointmentId(appointment.getId(), email);

        if (appointmentSubcriber == null) {
            return ResponseEntity.badRequest().body("Invalid token.");
        } else {
            appointmentSubcriber.setConfirmation(true);
            appointmentRepository.save(appointment);
            String body = "";
            try {

                Template t = templates.getTemplate("thankyou.html");
                Map<String, String> map = new HashMap<>();
                map.put("DATE", appointment.getTime());
                if (appointment.getName().contains(">")) {
                    String[] output = appointment.getName().split(">");
                    map.put("APPOINTMENT_NAME", output[0]);
                } else {
                    map.put("APPOINTMENT_NAME", appointment.getName());
                }
                map.put("REJECT_APPOINTMENT", "http://localhost:8080/api/deny-appointment?confirmationToken=" + token + "&subcriberEmail=" + email);
                body = FreeMarkerTemplateUtils.processTemplateIntoString(t, map);

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            mailService.sendAppointment(appointment.getFromMail(), appointment.getFromMail(), appointmentSubcriber.getSubcriberEmail(), "Confirm Invite Email", body);

        }
        final org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
        ctx.setVariable("name", appointmentSubcriber.getSubcriberEmail());
        String htmlContent = this.htmlTemplateEngine.process("accept.html", ctx);
        return ResponseEntity.ok().body(htmlContent);
    }

    @Override
    public int copyAppointment(int appointmentId, int workflowId, Account account) {

        Appointment temp = appointmentRepository.findAppointmentById(appointmentId);
        Workflow workflow = workflowRepository.findWorkflowById(workflowId);
        if (temp == null || workflow == null) {
            return 1;
        }
        Appointment appointment = new Appointment();
        appointment.setAccount_id(account.getId());
        List<AppointmentGroupContact> appointmentGroupContacts = workflow.getWorkflowGroupContacts().stream().map(g -> {
            AppointmentGroupContact appointmentGroupContact = new AppointmentGroupContact();
            appointmentGroupContact.setGroupContact(g.getGroupContact());
            appointmentGroupContact.setAppointment(appointment);
            appointmentGroupContact.setCreatedTime(LocalDateTime.now().toString());
            String[] mailList = groupContactRepository.findSubcriberMailByGroupContactId(appointmentGroupContact.getGroupContact().getId());
            //Add Subcriber To Appointments
            List<AppointmentSubcriber> appointmentSubcribers = new ArrayList<>();
            for (int i = 0; i < mailList.length; i++) {
                AppointmentSubcriber appointmentSubcriber = new AppointmentSubcriber();
                appointmentSubcriber.setConfirmation(false);
                appointmentSubcriber.setCreatedTime("");
                appointmentSubcriber.setAppointmentGroupContact(appointmentGroupContact);
                appointmentSubcriber.setSend(false);
                appointmentSubcriber.setOpened(false);

                appointmentSubcriber.setSubcriberEmail(mailList[i]);
                appointmentSubcribers.add(appointmentSubcriber);
            }
            appointmentGroupContact.setAppointmentSubcribers(appointmentSubcribers);

            return appointmentGroupContact;
        }).collect(Collectors.toList());
        appointment.setAppointmentGroupContacts(appointmentGroupContacts);
        appointment.setBody(temp.getBody());
        appointment.setToken(UUID.randomUUID().toString());
        appointment.setTime(temp.getTime());
        appointment.setBodyJson(temp.getBodyJson());
        appointment.setCreatedTime(LocalDateTime.now().toString());
        appointment.setName(temp.getName() + "-" + workflow.getName() + ">" + UUID.randomUUID().toString());
        appointment.setSubject(temp.getSubject());
        appointment.setStatus("Sending");
        appointment.setAutomation(true);
        appointment.setMessageId(temp.getMessageId());
        appointment.setFromMail(temp.getFromMail());
        appointment.setSender(temp.getSender());
        appointmentRepository.save(appointment);
        return appointment.getId();
    }

    @Override
    public void getStatisticAppointment() {
        log.info("Get Statistic Appointment.\n");
        for (Appointment appointment : appointmentRepository.findAll()) {
            // Get Statistic of Campaign
            double request = appointmentSubcriberRepository.countRequest(appointment.getId());
            double bounce = appointmentSubcriberRepository.countBounce(appointment.getId());
            double delivery = appointmentSubcriberRepository.countDelivery(appointment.getId());
            double open = appointmentSubcriberRepository.countOpen(appointment.getId());
            double click = appointmentSubcriberRepository.countClick(appointment.getId());
            double spam = appointmentSubcriberRepository.countSpam(appointment.getId());
            String requestStr = String.valueOf((int) request);

//                    String requestStr =new Double(request).toString();
            appointment.setRequest(requestStr);
            appointment.setOpenRate(String.valueOf((int) open));
            appointment.setBounce(String.valueOf((int) bounce));
            appointment.setDelivery(String.valueOf((int) delivery));
            appointment.setClickRate(String.valueOf((int) click));
//            appointment.setSpamRate(String.valueOf((int) spam)+"("+Math.round((spam/request)*100) +"%)");
//            appointment.setOpenRate(String.valueOf((int) open)+"("+Math.round((open/request)*100)+"%)");
//            appointment.setBounce(String.valueOf((int) bounce)+"("+Math.round((bounce/request)*100)+"%)");
//            appointment.setDelivery(String.valueOf((int) delivery)+"("+Math.round((delivery/request)*100)+"%)");
//            appointment.setClickRate(String.valueOf((int) click)+"("+Math.round((click/request)*100) +"%)");
//            appointment.setSpamRate(String.valueOf((int) spam)+"("+Math.round((spam/request)*100) +"%)");

            appointmentRepository.save(appointment);
        }
    }

    @Override
    public ResponseEntity<String> denyAppointment(String token, String email) {
        Appointment appointment = appointmentRepository.findByToken(token);
        AppointmentSubcriber appointmentSubcriber = appointmentRepository.findMailByAppointmentId(appointment.getId(), email);
        if (appointmentSubcriber == null) {
            return ResponseEntity.badRequest().body("Invalid token.");
        } else {
            appointmentSubcriber.setConfirmation(false);
            appointmentRepository.save(appointment);
            final org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
            ctx.setVariable("name", appointmentSubcriber.getSubcriberEmail());
            String htmlContent = this.htmlTemplateEngine.process("deny.html", ctx);

            return ResponseEntity.ok().body(htmlContent);
        }


    }

    @Override
    public List<AppointmentDTO> getAppointmentSegment(int accountId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentByAccount_idOrderByCreatedTimeDesc(accountId);
        List<AppointmentDTO> appointmentDTOS = appointments.stream().map(g -> {
            AppointmentDTO appointmentDTO = new AppointmentDTO();
            appointmentDTO.setId(g.getId());
            appointmentDTO.setCreatedTime(g.getCreatedTime());
            if (g.getName().contains(">")) {
                String[] output = g.getName().split(">");
                appointmentDTO.setName(output[0]);
            } else {
                appointmentDTO.setName(g.getName());
            }
            return appointmentDTO;
        }).collect(Collectors.toList());
        return appointmentDTOS;
    }
}
