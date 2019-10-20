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
@Table(name = "task",uniqueConstraints={
        @UniqueConstraint(columnNames = {"workflow_id","id"})
})



//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,property = "id")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "pre_task")
    private String preTask;

    @Basic
    @Column(name = "post_task")
    private String postTask;

    @Basic
    @Column(name = "gateway")
    private String gateway;

    @Basic
    @Column(name = "shape_id")
    private String shapeId;

    @Basic
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;

    @Basic
    @Column(name = "campaign_apointment")
    private int campaignAppointment;

    @Basic
    @Column(name = "type")
    private String type;

    @Basic
    @Column(name = " name")
    private String name;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

//    public String postTask;

//    @JsonIgnore
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "task_id",referencedColumnName = "id"),
//            @JoinColumn(name = "shape_id",referencedColumnName = "shape_id")
//    })
//    private Task task;
}
