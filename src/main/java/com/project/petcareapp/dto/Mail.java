package com.project.petcareapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Mail {

    private String timestamp;
    private String source;
    private String sourceArn;
    private String sendingAccountId;
    private String messageId;
    private String[] destination;
    private String headersTruncated;
    private String[][] headers;
    @JsonProperty("commonHeaders")
    private Commonheaders commonHeaders;
    @JsonProperty("tags")
    private String tags;
}

class Commonheaders{
    private String []from;
    private String date;
    private String []to;
    private String messageId;
    private String subject;

}


