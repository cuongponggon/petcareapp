package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "message_from_sqs")
public class MyMessage implements Serializable {

    private static final long serialVersionUID = -8013965441896177936L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;

    @Column(name = "content")
    private String content;



    @JsonCreator
    public MyMessage(String createdTime, String updatedTime, String content) {
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.content = content;

    }
}
