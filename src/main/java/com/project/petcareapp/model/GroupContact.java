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
public class GroupContact implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Basic
    @Column(name = "name")
    private String name;


    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;
    @Basic
    @Column(name = "account_id")
    private int account_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.MERGE
//            })
//    @JoinTable(name = "group_contact_has_subcriber",
//            joinColumns = { @JoinColumn(name = "group_contact_id") },
//            inverseJoinColumns = { @JoinColumn(name = "subcriber_id") })
//    private Set<Subcriber> subcribers = new HashSet<>();

    @OneToMany(mappedBy = "groupContact", cascade = CascadeType.ALL)
    private List<GroupContactSubcriber> groupContactSubcribers;






}