package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account implements Serializable {
    public static final String PROP_USERNAME = "username";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Basic
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "password")
    private String password;


    @Basic
    @Column(name = "fullname")
    private String fullname;

    @Basic
    @Column(name = "email")
    private String email;


    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "gender")
    private String gender;

    @Basic
    @Column(name = "address")
    private String address;


    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "created_time")
    private String createdTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Basic
    @Column(name = "updated_time")
    private String updatedTime;

}
