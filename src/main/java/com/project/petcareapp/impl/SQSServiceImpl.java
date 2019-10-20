package com.project.petcareapp.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.project.petcareapp.model.AppointmentSubcriber;
import com.project.petcareapp.model.CampaignSubcriber;
import com.project.petcareapp.model.MyMessage;
import com.project.petcareapp.repository.AppointmentSubcriberRepository;
import com.project.petcareapp.repository.CampaignSubcriberRepository;
import com.project.petcareapp.service.SQSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component

public class SQSServiceImpl implements SQSService {
    private static final Logger log = LoggerFactory.getLogger(SQSServiceImpl.class);
    private static final String CREATE_MESSAGE_ENDPOINT_URL = "http://localhost:8080/api/messages";


    @Autowired
    CampaignSubcriberRepository campaignSubcriberRepository;

    @Autowired
    AppointmentSubcriberRepository appointmentSubcriberRepository;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${sqs.url}")
    private String sqsURL;

    @Override
    @Scheduled(fixedDelay = 1000)
    public void getMessage() {
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(awsRegion).withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).build();
        while (true) {
        log.info("Receiving messages from MyQueue.\n");
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(sqsURL)
                        .withMaxNumberOfMessages(10)
                        .withWaitTimeSeconds(3);
        final List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
                .getMessages();
        for (final Message message : messages) {
            log.debug("Message");
            log.debug("  MessageId:     "
                    + message.getMessageId());
            log.debug("  ReceiptHandle: "
                    + message.getReceiptHandle());
            log.debug("  MD5OfBody:     "
                    + message.getMD5OfBody());
            System.out.println("  Body:          "
                    + message.getBody());
            if ((!message.getBody().isEmpty())) {
                System.out.println("Calling POST /notification to insert records into database");
                String checked = "uncheck";
                ObjectMapper mapper = new ObjectMapper();
                //Convert Json
                try {
                    String jsonInString = message.getBody();
                    JSONObject jsonObject = new JSONObject(jsonInString);
                    JSONObject mail = jsonObject.getJSONObject("mail");
                    String messageId = (String) mail.get("messageId");
                    String timeSend = (String)mail.get("timestamp");
                    String eventType = (String) jsonObject.get("eventType");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

                    // Convert UTC to LocalTime
                    Instant timestamp = Instant.parse(timeSend);
                    ZonedDateTime saigonTime = timestamp.atZone(ZoneId.of("Asia/Saigon"));
                    String timeSendStr =  saigonTime.format(formatter);
                    List<AppointmentSubcriber> appointmentSubcribers = appointmentSubcriberRepository.findMessageId(messageId.trim());
                    List<CampaignSubcriber> campaignSubcribers = campaignSubcriberRepository.findMessageId(messageId.trim());
                    if (!appointmentSubcribers.isEmpty()) {
                        AppointmentSubcriber appointmentSubcriber = appointmentSubcribers.stream().findFirst().get();
                        if (eventType.contains("Open")) {
                            appointmentSubcriber.setOpened(true);
                            appointmentSubcriber.setCreatedTime(timeSendStr);
                            checked = "checked";
                        } else if (eventType.contains("Click")) {
                            appointmentSubcriber.setConfirmation(true);
                            appointmentSubcriber.setCreatedTime(timeSendStr);
                            checked = "checked";
                        }
                        if(eventType.contains("Delivery")){
                            appointmentSubcriber.setDelivery(true);
                            appointmentSubcriber.setCreatedTime(timeSendStr);
                            checked = "checked";
                        }
                        if(eventType.contains("Bounce")){
                            appointmentSubcriber.setBounce(true);
                            appointmentSubcriber.setCreatedTime(timeSendStr);
                            checked = "checked";
                        }

                        appointmentSubcriberRepository.save(appointmentSubcriber);

                    } else {
                        if(!campaignSubcribers.isEmpty()) {
                            CampaignSubcriber campaignSubcriber = campaignSubcribers.stream().findFirst().get();
                            if (eventType.contains("Open")) {
                                campaignSubcriber.setOpened(true);
                                campaignSubcriber.setCreatedTime(timeSendStr);
                                checked = "checked";
                            } else if (eventType.contains("Click")) {
                                campaignSubcriber.setComfirmation(true);
                                campaignSubcriber.setCreatedTime(timeSendStr);
                                checked = "checked";
                            }
                            if (eventType.contains("Delivery")) {
                                campaignSubcriber.setDelivery(true);
                                campaignSubcriber.setCreatedTime(timeSendStr);
                                checked = "checked";
                            }
                            if(eventType.contains("Bounce")){
                                campaignSubcriber.setBounce(true);
                                campaignSubcriber.setCreatedTime(timeSendStr);
                                checked = "checked";
                            }
                            campaignSubcriberRepository.save(campaignSubcriber);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyMessage myMessage = new MyMessage(LocalDateTime.now().toString(), "", message.getBody());
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.postForEntity(CREATE_MESSAGE_ENDPOINT_URL, myMessage, String.class);
                System.out.println("Deleting a message.\n");
                String messageReceiptHandle = messages.get(0).getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(sqsURL, messageReceiptHandle));


            }
        }
        }
    }

}

