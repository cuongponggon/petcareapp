package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Workflow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "timestart")
    private String timeStart;

    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @Basic
    @Column(name = "model")
    private String model;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;

    @OneToMany( mappedBy = "workflow",cascade = CascadeType.ALL)
    private List<Task> tasks;


    @OneToMany( mappedBy = "workflow",cascade = CascadeType.ALL)
    private List<WorkflowGroupContact> workflowGroupContacts;

    @Column(name = "account_id")
    private Integer account_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;


}
