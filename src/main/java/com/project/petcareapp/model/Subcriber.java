package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Subcriber implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Basic
    @Column(name = "firstname")
    private String firstName;

    @Basic
    @Column(name = "lastname")
    private String lastName;


    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "type")
    private String type;

    @Column(name = "dob")
    private String dob;

    @Basic
    @Column(name = "totalRequest")
    private String totalRequest;

    @Basic
    @Column(name = "open_rate")
    private String openRate;

    @Basic
    @Column(name = "click_rate")
    private String clickRate;


    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;

    @Basic
    @Column(name = "point")
    private Long point;

    @Basic
    @Column(name = "black_list")
    private boolean blackList;

    @Basic
    @Column(name = "account_id")
    private int account_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @OneToMany( mappedBy = "subcriber", cascade = CascadeType.ALL)
    private List<GroupContactSubcriber> groupContactSubcribers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subcriber subcriber = (Subcriber) o;
        return id == subcriber.id &&
                Objects.equals(firstName, subcriber.firstName) &&
                Objects.equals(lastName, subcriber.lastName) &&
                Objects.equals(email, subcriber.email) &&
                Objects.equals(address, subcriber.address) &&
                Objects.equals(type, subcriber.type) &&
                Objects.equals(totalRequest, subcriber.totalRequest) &&
                Objects.equals(createdTime, subcriber.createdTime) &&
                Objects.equals(updatedTime, subcriber.updatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName,lastName,dob, email, address, type, totalRequest, createdTime, updatedTime);
    }
//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.MERGE
//            })
////            mappedBy = "group_contact_has_subcriber")
//    private Set<GroupContact> groupContact = new HashSet<>();


}
