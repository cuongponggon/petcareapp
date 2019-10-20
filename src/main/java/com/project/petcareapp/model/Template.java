package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Template implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "name")
    private String nameTemplate;

    @Basic
    @Column(name = "type")
    private String type;

    @Basic
    @Column(name = "content_html")
    private String contentHtml;

    @Basic
    @Column(name = "content_json")
    private String contentJson;

    @Basic
    @Column(name = "account_id")
    private int account_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;


    @Basic
    @Column(name = "created_time")
    private String created_time;

    @Basic
    @Column(name = "updated_time")
    private String updated_time;

    @Basic
    @Column(name = "preview")
    private String preview;
}

