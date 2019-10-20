package com.project.petcareapp.impl;

import com.project.petcareapp.dto.*;
import com.project.petcareapp.model.Account;
import com.project.petcareapp.model.CampaignSubcriber;
import com.project.petcareapp.model.GroupContactSubcriber;
import com.project.petcareapp.model.Subcriber;
import com.project.petcareapp.repository.*;
import com.project.petcareapp.service.SubcriberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubcriberServiceImpl implements SubcriberService {

    private static final Logger log = LoggerFactory.getLogger(SubcriberServiceImpl.class);
    @Autowired
    SubcriberRepository subcriberRepository;

    @Autowired
    GroupContactRepository groupContactRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppointmentSubcriberRepository appointmentSubcriberRepository;

    @Autowired
    CampaignSubcriberRepository campaignSubcriberRepository;

    @Autowired
    GroupContactSubcriberRepository groupContactSubcriberRepository;


    @Override
    public boolean createSubcrbier(SubcriberDTO dto,Account account) {
        System.out.println(dto.getEmail());
        Subcriber checkExistedSubcriber = subcriberRepository.findByEmailAndAccount(dto.getEmail(),account);
        if (checkExistedSubcriber != null) {

            return false;
        }
        Subcriber subcriber = new Subcriber();

        subcriber.setCreatedTime(LocalDateTime.now().toString());
        subcriber.setEmail(dto.getEmail());
        subcriber.setAddress(dto.getAddress());
        subcriber.setPhone(dto.getPhone());
        subcriber.setPoint(Long.valueOf(0));
        subcriber.setFirstName(dto.getFirstName());
        subcriber.setLastName(dto.getLastName());
        subcriber.setType("0");
        subcriber.setAccount_id(account.getId());
        List<GroupContactSubcriber> groupContactSubcribers = dto.getGcSubcriberDTOS().stream().map(g -> {
            GroupContactSubcriber groupContactSubcriber = new GroupContactSubcriber();
            groupContactSubcriber.setGroupContact(groupContactRepository.findGroupById(g.getGroupContactId()));
            groupContactSubcriber.setActive(true);
            groupContactSubcriber.setCreatedTime(LocalDateTime.now().toString());
            groupContactSubcriber.setSubcriber(subcriber);

            return groupContactSubcriber;
        }).collect(Collectors.toList());
        subcriber.setGroupContactSubcribers(groupContactSubcribers);
        subcriberRepository.save(subcriber);
        return true;
    }

    @Override
    public boolean createSubcriberForm(SubcriberFormDTO dto) {
        System.out.println(dto.getEmail());
        Subcriber checkExistedSubcriber = subcriberRepository.findByEmail(dto.getEmail());
        if (checkExistedSubcriber != null) {
            return false;
        }
        Subcriber subcriber = new Subcriber();

        subcriber.setLastName(dto.getLastName());

        subcriber.setCreatedTime(LocalDateTime.now().toString());
        subcriber.setEmail(dto.getEmail());
        subcriber.setFirstName(dto.getFirstName());
        subcriber.setType("Beginner Contacts");
        subcriber.setPoint(Long.valueOf(0));
        Account account = accountRepository.findAccountById(1);
        subcriber.setAccount_id(account.getId());
        List<GroupContactSubcriber> groupContactSubcribers = new ArrayList<>();

        GroupContactSubcriber groupContactSubcriber = new GroupContactSubcriber();
        groupContactSubcriber.setGroupContact(groupContactRepository.findGroupById(1));
        groupContactSubcriber.setSubcriber(subcriber);
        groupContactSubcriber.setActive(true);
        groupContactSubcribers.add(groupContactSubcriber);
        subcriber.setGroupContactSubcribers(groupContactSubcribers);
        subcriberRepository.save(subcriber);
        return true;
    }

    @Override
    public boolean createListSubcrbier(List<SubcriberDTO> subcriberDTOS,Account account) {
        for (SubcriberDTO subcriberDTO : subcriberDTOS) {

            Subcriber result = subcriberRepository.findByEmailAndAccount(subcriberDTO.getEmail(),account);

            if (result != null) {
                List<GroupContactSubcriber> groupContactSubcriber1st = result.getGroupContactSubcribers();
                List<Integer> groupId = result.getGroupContactSubcribers().stream().map(x -> x.getGroupContact().getId()).collect(Collectors.toList());
                List<Integer> groupInputId = subcriberDTO.getGcSubcriberDTOS().stream().map(x -> x.getGroupContactId()).collect(Collectors.toList());
                groupId.retainAll(groupInputId);// Group after filter will set Active
                for (int number : groupId) {
                    GroupContactSubcriber groupContactSubcriber = groupContactSubcriberRepository.findGroupContactSubcriberBySubcriberIdAndGroupContactId(result.getId(), number);
                    if (!groupContactSubcriber.isActive()) {
                        groupContactSubcriber.setActive(true);
                        groupContactSubcriber.setUpdatedTime(LocalDateTime.now().toString());
                        groupContactSubcriberRepository.save(groupContactSubcriber);
                    }
                }
                Set<Integer> intersection = new HashSet<Integer>(groupId);
                intersection.retainAll(groupInputId);
                // Subtract the intersection from the union
                groupInputId.removeAll(intersection);
                if (!groupInputId.isEmpty()) {
                    List<GroupContactSubcriber> groupContactSubcribers = groupInputId.stream().map(g -> {
                        GroupContactSubcriber groupContactSubcriber = new GroupContactSubcriber();
                        groupContactSubcriber.setActive(true);
                        groupContactSubcriber.setGroupContact(groupContactRepository.findGroupById(g));
                        groupContactSubcriber.setCreatedTime(LocalDateTime.now().toString());
                        groupContactSubcriber.setSubcriber(result);
                        return groupContactSubcriber;
                    }).collect(Collectors.toList());
                    Set<GroupContactSubcriber> fooSet = new LinkedHashSet<>(groupContactSubcriber1st);
                    fooSet.addAll(groupContactSubcribers);
                    List<GroupContactSubcriber> finalGroupContact = new ArrayList<>(fooSet);
                    result.setGroupContactSubcribers(finalGroupContact);
                    subcriberRepository.save(result);
                    return true;
                }
                return false;
            }

            Subcriber subcriber = new Subcriber();
            subcriber.setLastName(subcriberDTO.getLastName());
            subcriber.setFirstName(subcriberDTO.getFirstName());
            subcriber.setDob(subcriberDTO.getDob());
            subcriber.setEmail(subcriberDTO.getEmail());
            subcriber.setAddress(subcriberDTO.getAddress());
            subcriber.setBlackList(false);
            subcriber.setPoint(Long.valueOf(0));
            subcriber.setPhone(subcriberDTO.getPhone());
            subcriber.setLastName(subcriberDTO.getLastName());
            subcriber.setFirstName(subcriberDTO.getFirstName());
            subcriber.setCreatedTime(LocalDateTime.now().toString());
            subcriber.setType(subcriberDTO.getType());
            subcriber.setAccount_id(account.getId());
            List<GroupContactSubcriber> groupContactSubcribers = subcriberDTO.getGcSubcriberDTOS().stream().map(g -> {

                GroupContactSubcriber groupContactSubcriber = new GroupContactSubcriber();
                groupContactSubcriber.setActive(true);
                if (subcriberDTO.getGcSubcriberDTOS().size() == 0 || g.getGroupContactId() == 0) {
                    groupContactSubcriber.setGroupContact(groupContactRepository.findGroupById(1));
                } else {
                    groupContactSubcriber.setGroupContact(groupContactRepository.findGroupById(g.getGroupContactId()));
                }
                groupContactSubcriber.setCreatedTime(LocalDateTime.now().toString());
                groupContactSubcriber.setSubcriber(subcriber);
                return groupContactSubcriber;
            }).collect(Collectors.toList());
            subcriber.setGroupContactSubcribers(groupContactSubcribers);
            subcriberRepository.save(subcriber);
        }

        return true;
    }


    @Override
    public Subcriber editSubcriber(Subcriber subcriber) {
        return null;
    }


    @Override
    public void getStatisticSubcriber() {

        log.info("Get Statistic By Subcriber .\n");
        for (Subcriber subcriber : subcriberRepository.findAll()) {

            double requestCampaign = campaignSubcriberRepository.countCampaignSubcriberBySubcriberEmail(subcriber.getEmail());
            double requestAppointment = appointmentSubcriberRepository.countAppointmentSubcriberBySubcriberEmail(subcriber.getEmail());
            double total = requestAppointment + requestCampaign;
            double totalOpen = campaignSubcriberRepository.countBySubcriberEmailAndOpened(subcriber.getEmail(), true)
                    + appointmentSubcriberRepository.countBySubcriberEmailAndOpened(subcriber.getEmail(), true);
            double totalClick = campaignSubcriberRepository.countBySubcriberEmailAndComfirmation(subcriber.getEmail(), true) + appointmentSubcriberRepository.countBySubcriberEmailAndConfirmation(subcriber.getEmail(), true);
            subcriber.setTotalRequest(String.valueOf((int) total));
            subcriber.setOpenRate(String.valueOf((int) totalOpen));
            subcriber.setClickRate(String.valueOf((int) totalClick));
            if (subcriber.getPoint() >= 0 && subcriber.getPoint() < 30) {
                subcriber.setType("1");
            } else if (subcriber.getPoint() >= 30 && subcriber.getPoint() < 65) {
                subcriber.setType("2");
            } else if (subcriber.getPoint() >= 65 && subcriber.getPoint() < 102) {
                subcriber.setType("3");
            } else if (subcriber.getPoint() >= 102 && subcriber.getPoint() < 237) {
                subcriber.setType("4");
            } else if (subcriber.getPoint() >= 237) {
                subcriber.setType("5");
            }
            subcriberRepository.save(subcriber);
        }


    }

    @Override
    public List<Subcriber> getContactLatest() {
        return subcriberRepository.findTop5ByOrderByCreatedTimeDesc();
    }

    @Override
    public boolean moveToBlackList(int subcriberId) {
        Subcriber subcriber = subcriberRepository.findSubcriberById(subcriberId);
        if (subcriber == null) {
            return false;
        }
        if (subcriber.isBlackList()) {
            subcriber.setBlackList(false);
            subcriberRepository.save(subcriber);
        } else {
            subcriber.setBlackList(true);
            subcriberRepository.save(subcriber);
        }
        return true;
    }


    @Override
    public SubcriberViewDTO getSubcriberById(int id) {
        SubcriberViewDTO subcriberViewDTO = new SubcriberViewDTO();
        Subcriber subcriber = subcriberRepository.findSubcriberById(id);
        subcriberViewDTO.setAddress(subcriber.getAddress());
        subcriberViewDTO.setCreatedTime(subcriber.getCreatedTime());
        subcriberViewDTO.setType(subcriber.getType());
        subcriberViewDTO.setFirstName(subcriber.getFirstName());
        subcriberViewDTO.setLastName(subcriber.getLastName());
        subcriberViewDTO.setDob(subcriber.getDob());
        subcriberViewDTO.setEmail(subcriber.getEmail());
        subcriberViewDTO.setPhone(subcriber.getPhone());
        int belongGroup = groupContactSubcriberRepository.countAllBySubcriberId(subcriber.getId());
        int belongCampaign = campaignSubcriberRepository.countAllBySubcriberEmail(subcriber.getEmail());
        subcriberViewDTO.setClickRate(subcriber.getClickRate());
        subcriberViewDTO.setOpenRate(subcriber.getOpenRate());
        subcriberViewDTO.setBelongCampaign(belongCampaign);
        subcriberViewDTO.setBelongGroup(belongGroup);
        List<GroupContactDTO> groupContactDTOS = groupContactSubcriberRepository.findGroupContactSubcriberBySubcriber(subcriber).stream().map(g -> {
            GroupContactDTO groupContactDTO = new GroupContactDTO();
            groupContactDTO.setName(g.getGroupContact().getName());
            groupContactDTO.setCreated_time(g.getGroupContact().getCreatedTime());
            groupContactDTO.setId(g.getGroupContact().getId());
            groupContactDTO.setDescription(g.getGroupContact().getDescription());
            return groupContactDTO;
        }).collect(Collectors.toList());
        subcriberViewDTO.setGroupContactDTOList(groupContactDTOS);
        List<CampaignDTO> campaignDTOS = campaignSubcriberRepository.findCampaignSubcriberBySubcriberEmail(subcriber.getEmail()).stream()
                .map(g -> {
                    CampaignDTO campaignDTO = new CampaignDTO();
                    campaignDTO.setCampaignName(g.getCampaignGroupContact().getCampaign().getName());
                    campaignDTO.setId(g.getCampaignGroupContact().getCampaign().getId());
                    campaignDTO.setStatus(g.getCampaignGroupContact().getCampaign().getStatus());
                    campaignDTO.setCreatedTime(g.getCreatedTime());
                    campaignDTO.setType(g.getCampaignGroupContact().getCampaign().getType());
                    return campaignDTO;
                }).collect(Collectors.toList());

        subcriberViewDTO.setCampaignDTOList(campaignDTOS);
        return subcriberViewDTO;
    }


    @Override
    public List<Subcriber> getSubcriberByAccountId(int accountId) {
        return subcriberRepository.findSubcriberByAccount_id(accountId);
    }

    @Override
    public Subcriber updateSubcriber(Subcriber subcriber) {
        return null;
    }

    @Override
    public int countTotalSubcriber(int subcriberId) {
        return subcriberRepository.countAllById(subcriberId);
    }

    @Override
    public StatisticContactDTO countSubcriber() {
        StatisticContactDTO statisticContactDTO = new StatisticContactDTO();
        statisticContactDTO.setLevel1(subcriberRepository.countByType("1"));
        statisticContactDTO.setLevel2(subcriberRepository.countByType("2"));
        statisticContactDTO.setLevel3(subcriberRepository.countByType("3"));
        statisticContactDTO.setLevel4(subcriberRepository.countByType("4"));
        statisticContactDTO.setLevel5(subcriberRepository.countByType("5"));
        return statisticContactDTO;
    }


    @Override
    public Subcriber getSubcriberByEmail(String email) {
        return subcriberRepository.findSubcriberByEmail(email);
    }

    @Override
    public List<Subcriber> searchByNameorEmail(String searchValue) {
        return subcriberRepository.searchByEmailAndName(searchValue);
    }

    @Override
    public List<SubcriberDTO> getAllSubcriberV2(int accountId) {

        List<Subcriber> subcribers = subcriberRepository.findAllByAccount_idOrderByCreatedTimeDesc(accountId);

        List<SubcriberDTO> dtos = new ArrayList<>();
        for (Subcriber subcriber : subcribers) {
            SubcriberDTO dto = new SubcriberDTO();
            dto.setId(subcriber.getId());
            dto.setEmail(subcriber.getEmail());
            dto.setLastName(subcriber.getLastName());
            dto.setFirstName(subcriber.getFirstName());
            dto.setDob(subcriber.getDob());
            dto.setPhone(subcriber.getPhone());
            dto.setAddress(subcriber.getAddress());
            dto.setType(subcriber.getType());
            dto.setCreatedTime(subcriber.getCreatedTime());
            dto.setBlackList(subcriber.isBlackList());
            dtos.add(dto);
        }
        Comparator<SubcriberDTO> createTimeComparator = (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime());
        dtos.sort(createTimeComparator.reversed());
        return dtos;
    }

    @Override
    public String deleteSubcriber(int id, int groupId) {
        GroupContactSubcriber groupContactSubcriber = groupContactSubcriberRepository.findGroupContactSubcriberBySubcriberIdAndGroupContactId(id, groupId);

        if (groupContactSubcriber == null) {
            return "This subcriber is not exist!";
        }
        if (groupId == 1) {
            subcriberRepository.deleteSubcriberFromGroup(id);
            return "delete all success";
        } else {
            groupContactSubcriberRepository.deleteSubcriberFromGroup(id, groupId);
            return "sucess";
        }
    }


    @Override
    public void autoUpdatePointSubcriber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//        String formatTime = LocalDateTime.now().minusDays(7).format(formatter);
//        String formatTime = LocalDateTime.now().format(formatter);
        String formatTime = "09/26/2019";
        for (Subcriber subcriber : subcriberRepository.findAll()) {
            Long point = subcriber.getPoint();
            if (point < 0) {
                point = (long) 0;
            }
            double totalRequest = campaignSubcriberRepository.countBySubcriberEmailAndCreatedTimeContains(subcriber.getEmail(), formatTime)
                    + appointmentSubcriberRepository.countBySubcriberEmailAndCreatedTimeContains(subcriber.getEmail(), formatTime);
            double totalOpen = campaignSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndOpenedIsTrue(subcriber.getEmail(), formatTime)
                    + appointmentSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndOpenedIsTrue(subcriber.getEmail(), formatTime);
            double totalClick = campaignSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndComfirmationIsTrue(subcriber.getEmail(), formatTime)
                    + appointmentSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndConfirmationIsTrue(subcriber.getEmail(), formatTime);
            double totalSent = campaignSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndSendIsTrue(subcriber.getEmail(), formatTime)
                    + appointmentSubcriberRepository.countBySubcriberEmailAndCreatedTimeContainsAndConfirmationIsTrue(subcriber.getEmail(), formatTime);

            double openRate = (Math.round(totalOpen / totalRequest));
            double clickRate = (Math.round(totalClick / totalRequest));
            List<CampaignSubcriber> campaignSubcribers = campaignSubcriberRepository.findCampaignSubcriberBySubcriberEmailAndCreatedTimeContainsOrderByCreatedTimeDesc(subcriber.getEmail(),
                    formatTime);
//            ListIterator<CampaignSubcriber> listIterator = campaignSubcribers.listIterator();
            boolean clickRow = false;
            boolean openRow = false;
            //Dx
            if (campaignSubcribers.size() > 0) {
                for (int counter = 1; counter <= campaignSubcribers.size(); counter++) {
                    int next = counter + 1;

                    if (next == campaignSubcribers.size()) {
                        break;
                    }
                    CampaignSubcriber campaignSubcriber = campaignSubcribers.get(counter);
                    CampaignSubcriber campaignSubcriberNext = campaignSubcribers.get(next);
                    CampaignSubcriber campaignSubcriberPrevious = campaignSubcribers.get(counter - 1);
                    if (campaignSubcriber.isOpened() && campaignSubcriberNext.isOpened() && campaignSubcriberPrevious.isOpened()) {
                        openRow = true;
                    }
                    if (campaignSubcriber.isComfirmation() && campaignSubcriberNext.isComfirmation() && campaignSubcriberPrevious.isComfirmation()) {
                        clickRow = true;
                    }
                }
            }
            if (totalSent >= 1) {
                point += 2;
            }

            if (totalOpen <= 2 && totalSent >= 3) {
                point -= 5;
            }
            if (openRate <= 25 && openRate >= 10) {
                point += 5;

            }
            if (openRate > 25 && openRate <= 75) {
                point += 10;

            }
            if (openRate > 75) {
                point += 15;
            }
            if (clickRate <= 25 && clickRate >= 10) {
                point += 10;

            }
            if (clickRate > 25 && clickRate <= 75) {
                point += 15;

            }
            if (clickRate > 75) {
                point += 20;
            }
            if (openRow == true) {
                point += 10;
            }
            if (clickRow == true) {
                point += 20;
            }
            System.out.println(point);
            //+ mark by Level
            double level = Integer.parseInt(subcriber.getType());
            if (level > 0) {
                double pointPlus = (level + 1) * Math.log(level);
                point = point + (int) pointPlus;
            }


            subcriber.setPoint(point);
            subcriberRepository.save(subcriber);
        }

    }

    @Override
    public List<Subcriber> getSubcriberBySegment(List<SegmentDTO> segmentDTOs, String condition) {
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
                //Group
                if (segmentDTO.getSelect2().equalsIgnoreCase("Group")) {
                    if (segmentDTO.getSelect3().equalsIgnoreCase("is group")) {
                        subcriberList = subcriberRepository.findAllByGroupContact(Integer.valueOf(segmentDTO.getSelect4()));
                    }
                    if (segmentDTO.getSelect3().equalsIgnoreCase("is not group")) {
                        subcriberList = subcriberRepository.findAllByGroupContactNot(Integer.valueOf(segmentDTO.getSelect4()));
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
//                                Subcriber subcriber = subcriberRepository.findSubcriberByEmail(subcriberMail);
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
        return subcribers;

    }


}
