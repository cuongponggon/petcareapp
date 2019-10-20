package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_contact_has_subcriber",uniqueConstraints={
        @UniqueConstraint(columnNames = {"subcriber_id", "group_contact_id"})
}

)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,property = "id")
public class GroupContactSubcriber {
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

    @Basic
    @Column(name = "active")
    private boolean active;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subcriber_id")
    private Subcriber subcriber;



//    @Basic
//    @Column(name = "group_contact_id" )
//    private int groupContactId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_contact_id" )
    private GroupContact groupContact;


}
