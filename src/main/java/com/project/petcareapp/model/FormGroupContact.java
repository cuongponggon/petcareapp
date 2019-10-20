package com.project.petcareapp.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "form_has_group_contact",uniqueConstraints={
        @UniqueConstraint(columnNames = {"form_id", "group_contact_id"})
}

)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,property = "id")
public class FormGroupContact {
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


    @ManyToOne
    @JoinColumn(name = "form_id")
    private EmbeddedForm embeddedForm;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_contact_id" )
    private GroupContact groupContact;

}
