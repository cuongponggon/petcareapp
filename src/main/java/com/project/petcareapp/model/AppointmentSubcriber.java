package com.project.petcareapp.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Indexed
@Table(name = "appointment_has_subcriber", uniqueConstraints={@UniqueConstraint(columnNames = { "appointment_id","subcriber_email"})}

)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,property = "id")

public class AppointmentSubcriber implements Serializable {
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


    @Column(name = "subcriber_email")
    private String subcriberEmail;

    @Column(name = "confirmation")
    private boolean confirmation;



    @Column(name = "opened")
    private boolean opened;

    @Column(name = "send")
    private boolean send;
    @Column(name = "delivery")
    private boolean delivery;


    @Column(name = "bounce")
    private boolean bounce;

    @Column(name = "spam")
    private boolean spam;

    @Column(name = "message_id")
    @Field(termVector = TermVector.YES)
    private String messageId;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "appointment_id",referencedColumnName = "appointment_id"),
            @JoinColumn(name = "group_contact_id",referencedColumnName = "group_contact_id")
    })

    private AppointmentGroupContact appointmentGroupContact;




}